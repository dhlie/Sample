package com.fdd.downloader

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.closeQuietly
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 15:41
 * Description:
 *
 */
internal open class DownloadTask constructor(override val taskInfo: TaskInfo) : Task {

    interface TaskListener {
        /**
         * 等待开始下载
         */
        fun onPending(task: DownloadTask)

        /**
         * 下载开始
         */
        fun onStart(task: DownloadTask): Boolean

        /**
         * 下载停止
         */
        fun onStop(task: DownloadTask)

        /**
         * 下载进度更新
         * @return true - 更新成功, false - 更新失败
         */
        fun onProgressChanged(task: DownloadTask, downBytes: Long, totalBytes: Long)

        /**
         * 保存文件大小
         * @return true - 保存成功, false - 保存失败
         */
        fun onSaveFileLength(task: DownloadTask, length: Long): Boolean

        /**
         * 下载完成
         */
        fun onFinish(task: DownloadTask)

        /**
         * 下载失败
         */
        fun onError(task: DownloadTask, errorCode: Int)

        /**
         * 任务被删除
         * @param hasClearUp 下载中产生的临时文件是否被清理完毕
         */
        fun onDelete(task: DownloadTask, hasClearUp: Boolean)
    }

    private var status = TaskInfo.TaskStatus.PENDING
    private var tmpFile: File? = null
    private var lastNotifyTime: Long = 0L

    var listener: TaskListener? = null

    private fun getTempFile(): File? {
        if (tmpFile == null) {
            //创建临时文件保存目录
            val file = File(taskInfo.filePath)
            val tmpDir = File(FileUtil.concat(file.parent, ".tmp"))
            if (!tmpDir.exists()) {
                tmpDir.mkdirs()
                if (!tmpDir.exists()) {
                    return null
                }
            }
            //创建临时文件
            val fileName = FileUtil.getFileName(taskInfo.filePath)
            tmpFile = File(tmpDir, "$fileName.tmp${taskInfo.id}")
        }
        return tmpFile
    }

    override fun start() {
        onStart()

        //任务开启失败
        synchronized(this@DownloadTask) {
            if (status != TaskInfo.TaskStatus.RUNNING) {
                onError(TaskInfo.ERROR_CANNOT_START)
                return
            }
        }

        val tempFile = getTempFile()
        if (tempFile == null) {
            onError(TaskInfo.ERROR_MKDIR)
            return
        }

        taskInfo.downBytes = tempFile.length()
        val request: Request = try {
            val builder = Request.Builder()
                .url(taskInfo.url)
                .header("Range", "bytes=${taskInfo.downBytes}-")
            taskInfo.header?.forEach { entry ->
                builder.header(entry.key, entry.value)
            }

            builder.build()
        } catch (e: Exception) {
            onError(TaskInfo.ERROR_BUILD_REQUEST)
            return
        }
        val okHttpClient = DownloadManager.config.getOkHttpClient()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onError(TaskInfo.ERROR_NET_CONNECT)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val body = response.body
                    if (!response.isSuccessful || body == null) {
                        onError(TaskInfo.ERROR_NET_CONNECT)
                    } else {
                        save(body, tempFile)
                    }
                }
            }
        })
    }

    override fun stop() {
        synchronized(this@DownloadTask) {
            if (status == TaskInfo.TaskStatus.RUNNING || status == TaskInfo.TaskStatus.PENDING) {
                status = TaskInfo.TaskStatus.PAUSED
            }
        }
    }

    override fun delete(deleteFile: Boolean) {
        synchronized(this@DownloadTask) {
            if (status == TaskInfo.TaskStatus.RUNNING) {
                status = if (deleteFile) TaskInfo.TaskStatus.DELETING_WITH_FILE else TaskInfo.TaskStatus.DELETING_RECORD
            }
            onDelete(deleteFile, getTempFile())
        }
    }

    override fun pending() {
        synchronized(this@DownloadTask) {
            if (status == TaskInfo.TaskStatus.RUNNING) {
                status = TaskInfo.TaskStatus.PENDING
            } else {
                onPending()
            }
        }
    }

    /**
     * 保存文件
     */
    private fun save(body: ResponseBody, file: File) {
        var outFile: RandomAccessFile? = null
        var inputStream: InputStream? = null
        try {
            outFile = try {
                RandomAccessFile(file, "rw").apply {
                    seek(file.length())
                }
            } catch (e: Exception) {
                file.delete()
                onError(TaskInfo.ERROR_CREATE_OR_OPEN_FILE)
                return
            }
            if (taskInfo.downBytes == 0L) {
                taskInfo.totalBytes = body.contentLength()
                if (listener?.onSaveFileLength(this, taskInfo.totalBytes) == false) {
                    onError(TaskInfo.ERROR_SAVE_LENGTH)
                    return
                }
            }
            inputStream = body.byteStream()
            val buffer = ByteArray(8192)
            while (true) {
                val len = inputStream.read(buffer, 0, buffer.size)
                if (len == -1) {
                    if (taskInfo.totalBytes == -1L/**流大小未知*/ ||
                        (taskInfo.totalBytes == taskInfo.downBytes && taskInfo.totalBytes == file.length())
                    ) {
                        if (file.renameTo(File(taskInfo.filePath))) {
                            onFinish()
                        } else {
                            file.delete()
                            onError(TaskInfo.ERROR_RENAME)
                        }
                    } else {
                        file.delete()
                        onError(TaskInfo.ERROR_FILE_LENGTH)
                    }
                    break
                }

                synchronized(this@DownloadTask) {
                    when (status) {
                        TaskInfo.TaskStatus.PAUSED -> {
                            onStop()
                            return
                        }
                        TaskInfo.TaskStatus.DELETING_RECORD, TaskInfo.TaskStatus.DELETING_WITH_FILE -> {
                            return
                        }
                        TaskInfo.TaskStatus.PENDING -> {
                            onPending()
                            return
                        }
                        TaskInfo.TaskStatus.RUNNING -> {
                            outFile?.write(buffer, 0, len)
                            outFile?.fd?.sync()
                            taskInfo.downBytes += len
                            notifyProgressChanged()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            onError(TaskInfo.ERROR_IO)
        } finally {
            outFile?.closeQuietly()
            inputStream?.closeQuietly()
        }
    }

    private fun notifyProgressChanged() {
        if (taskInfo.downBytes == taskInfo.totalBytes) {
            listener?.onProgressChanged(this, taskInfo.downBytes, taskInfo.totalBytes)
        } else {
            val time = System.currentTimeMillis()
            if (time - lastNotifyTime >= DownloadManager.config.progressNotifyInterval) {
                listener?.onProgressChanged(this, taskInfo.downBytes, taskInfo.totalBytes)
                lastNotifyTime = time
            }
        }
    }

    private fun onStart() {
        synchronized(this@DownloadTask) {
            status = TaskInfo.TaskStatus.RUNNING
        }
        val result = listener?.onStart(this)
        if (result == false) {
            synchronized(this@DownloadTask) {
                status = TaskInfo.TaskStatus.ERROR
            }
        }
    }

    private fun onStop() {
        listener?.onStop(this)
    }

    private fun onFinish() {
        synchronized(this@DownloadTask) {
            status = TaskInfo.TaskStatus.FINISH
        }
        listener?.onFinish(this)
    }

    private fun onError(errorCode: Int) {
        synchronized(this@DownloadTask) {
            if (status == TaskInfo.TaskStatus.DELETING_RECORD || status == TaskInfo.TaskStatus.DELETING_WITH_FILE) {
                onDelete(status == TaskInfo.TaskStatus.DELETING_WITH_FILE, getTempFile())
            } else {
                status = TaskInfo.TaskStatus.ERROR
                listener?.onError(this, errorCode)
            }
        }
    }

    private fun onDelete(deleteFile: Boolean, tmpFile: File?) {
        tmpFile?.delete()
        var clearUp = tmpFile?.exists() != true
        if (deleteFile) {
            val file = File(taskInfo.filePath)
            file.delete()
            if (clearUp) {
                clearUp = !file.exists()
            }
        }
        listener?.onDelete(this, clearUp)
    }

    private fun onPending() {
        synchronized(this@DownloadTask) {
            status = TaskInfo.TaskStatus.PENDING
        }
        listener?.onPending(this)
    }

}