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

    private var status = TaskInfo.TaskStatus.PENDING
    private var downLength: Long = 0L
    private var totalLength: Long = -1L

    var listener: DownloadListener? = null

    private fun createTempFile(): File? {
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
        return File(tmpDir, "$fileName.tmp")
    }

    override fun start() {
        onStart()

        val tempFile = createTempFile()
        if (tempFile == null) {
            onError(TaskInfo.ERROR_MKDIR)
            return
        }
        downLength = tempFile.length()

        val request: Request = Request.Builder()
            .url(taskInfo.url)
            .header("Range", "bytes=$downLength-")
            .build()
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
            status = TaskInfo.TaskStatus.PAUSED
        }
    }

    override fun delete() {
        synchronized(this@DownloadTask) {
            status = TaskInfo.TaskStatus.DELETING
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
                body.contentLength()
            } else {
                taskInfo.totalBytes
            }
            inputStream = body.byteStream()
            val buffer = ByteArray(8192)
            while (true) {

                synchronized(this@DownloadTask) {
                    if (status == TaskInfo.TaskStatus.PAUSED) {
                        onStop()
                        return
                    } else if (status == TaskInfo.TaskStatus.DELETING) {
                        onDelete(file)
                        return
                    }
                }

                val len = inputStream.read(buffer, 0, buffer.size)
                if (len == -1) {
                    if (taskInfo.totalBytes != -1L && taskInfo.totalBytes == outFile?.length()) {
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
                listener?.onProgressChanged(taskInfo, downLength, taskInfo.totalBytes)
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
        listener?.onStart(taskInfo)
    }

    private fun onStop() {
        listener?.onStop(taskInfo)
    }

    private fun onFinish() {
        synchronized(this@DownloadTask) {
            status = TaskInfo.TaskStatus.FINISH
        }
        listener?.onFinish(taskInfo)
    }

    private fun onError(errorCode: Int) {
        synchronized(this@DownloadTask) {
            status = TaskInfo.TaskStatus.ERROR
        }
        listener?.onError(taskInfo, errorCode)
    }

    private fun onDelete(file: File) {
        file.delete()
        if (!file.exists()) {
            listener?.onDelete(taskInfo)
        }
    }

}