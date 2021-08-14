package com.dhl.base.downloader

import com.dhl.base.utils.FileUtil
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
open class DownloadTask constructor(override val taskInfo: TaskInfo) : Task {

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
    private var downLength: Long = 0L
    private var totalLength: Long = -1L
    private var tmpFile: File? = null

    var listener: TaskListener? = null

    private fun getTempFile(): File? {
        if (tmpFile == null) {
            //创建临时文件保存目录
            val tmpDir = File(DownloadProvider.instance.tempFileSavedDir(taskInfo.filePath))
            if (!tmpDir.exists()) {
                tmpDir.mkdirs()
                if (!tmpDir.exists()) {
                    return null
                }
            }
            //创建临时文件
            val fileName = FileUtil.getFileName(taskInfo.filePath)
            tmpFile = File(tmpDir, "$fileName.tmp")
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

        downLength = tempFile.length()
        taskInfo.downBytes = downLength
        val request: Request = try {
            val builder = Request.Builder()
                .url(taskInfo.url)
                .header("Range", "bytes=${downLength}-")
            taskInfo.header?.forEach { entry ->
                builder.header(entry.key, entry.value)
            }

            builder.build()
        } catch (e: Exception) {
            onError(TaskInfo.ERROR_URL)
            return
        }
        val okHttpClient = DownloadProvider.instance.provideOkHttpClient()
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

    override fun delete() {
        synchronized(this@DownloadTask) {
            if (status == TaskInfo.TaskStatus.RUNNING) {
                status = TaskInfo.TaskStatus.DELETING
            } else {
                onDelete(getTempFile())
            }
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
            totalLength = if (downLength == 0L) {
                taskInfo.totalBytes = body.contentLength()
                if (listener?.onSaveFileLength(this, taskInfo.totalBytes) == false) {
                    onError(TaskInfo.ERROR_SAVE_LENGTH)
                    return
                }
                taskInfo.totalBytes
            } else {
                taskInfo.totalBytes
            }
            inputStream = body.byteStream()
            val buffer = ByteArray(8192)
            while (true) {

                synchronized(this@DownloadTask) {
                    when (status) {
                        TaskInfo.TaskStatus.PAUSED -> {
                            onStop()
                            return
                        }
                        TaskInfo.TaskStatus.DELETING -> {
                            onDelete(file)
                            return
                        }
                        TaskInfo.TaskStatus.PENDING -> {
                            onPending()
                            return
                        }
                        else -> Unit
                    }
                }

                val len = inputStream.read(buffer, 0, buffer.size)
                if (len == -1) {
                    if (totalLength == -1L/**流大小未知*/ ||
                        (totalLength == downLength && totalLength == file.length())
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

                outFile?.write(buffer, 0, len)
                outFile?.fd?.sync()
                downLength += len
                taskInfo.downBytes = downLength
                listener?.onProgressChanged(this, downLength, taskInfo.totalBytes)
            }
        } catch (e: Exception) {
            onError(TaskInfo.ERROR_IO)
        } finally {
            outFile?.closeQuietly()
            inputStream?.closeQuietly()
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
            if (status == TaskInfo.TaskStatus.DELETING) {
                onDelete(getTempFile())
            } else {
                status = TaskInfo.TaskStatus.ERROR
                listener?.onError(this, errorCode)
            }
        }
    }

    private fun onDelete(file: File?) {
        file?.delete()
        listener?.onDelete(this, file?.exists() != true)
    }

    private fun onPending() {
        synchronized(this@DownloadTask) {
            status = TaskInfo.TaskStatus.PENDING
        }
        listener?.onPending(this)
    }

}