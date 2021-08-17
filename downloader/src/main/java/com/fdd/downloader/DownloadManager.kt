package com.fdd.downloader

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.fdd.downloader.db.DownloadDatabase
import com.fdd.downloader.db.QueryConst
import java.io.File
import java.util.LinkedList

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 18:53
 * Description:
 *
 */
class DownloadManager private constructor() {

    companion object {
        const val MSG_TASK_STATUS_CHANGED   = 0x2021
        const val MSG_TASK_PROGRESS_CHANGED = 0x2022

        @SuppressLint("StaticFieldLeak")
        lateinit var config: DownloaderConfig

        lateinit var instance: DownloadManager

        fun init(downloaderConfig: DownloaderConfig) {
            config = downloaderConfig
            instance = DownloadManager()
            instance.schedule()
        }
    }

    private val taskScheduler: TaskScheduler
    private val handler: Handler

    private val downloadListener: DownloadListener

    private val listeners: LinkedList<DownloadListener> = LinkedList()

    init {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_TASK_STATUS_CHANGED -> notifyTaskStatusChanged(msg.obj as? TaskInfo)
                    MSG_TASK_PROGRESS_CHANGED -> notifyTaskProgressChanged(msg.obj as TaskInfo)
                }
            }
        }
        downloadListener = object : DownloadListener {
            override fun onStatusChanged(taskInfo: TaskInfo?) {
                handler.obtainMessage(MSG_TASK_STATUS_CHANGED, taskInfo).sendToTarget()
            }

            override fun onProgressChanged(taskInfo: TaskInfo, downBytes: Long, totalBytes: Long) {
                taskInfo.downBytes = downBytes
                taskInfo.totalBytes = totalBytes
                handler.obtainMessage(MSG_TASK_PROGRESS_CHANGED, taskInfo).sendToTarget()
            }
        }

        taskScheduler = TaskScheduler()
        taskScheduler.downloadListener = downloadListener
    }

    /**
     * 注册下载 listener, 使用完要调用 unregisterListener
     */
    fun registerListener(listener: DownloadListener) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (!listeners.contains(listener)) {
                listeners.add(listener)
            }
        } else {
            handler.post {
                if (!listeners.contains(listener)) {
                    listeners.add(listener)
                }
            }
        }
    }

    /**
     * 删除下载 listener
     */
    fun unregisterListener(listener: DownloadListener) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            listeners.remove(listener)
        } else {
            handler.post {
                listeners.remove(listener)
            }
        }
    }

    private fun schedule() {
        taskScheduler.triggerSchedule()
    }

    /**
     * 下载, 如果相同下载任务已存在不会有任何操作
     */
    fun download(taskInfo: TaskInfo) = taskScheduler.postAction {
        if (taskInfo.filePath.isEmpty()) {
            val fileName = taskInfo.title ?: FileUtil.getFileName(taskInfo.url)
            taskInfo.filePath = PathUtil.getFilePath(fileName, taskInfo.fileDir)
            if (taskInfo.title.isNullOrEmpty()) {
                taskInfo.title = FileUtil.getFileName(taskInfo.filePath)
            }
        }

        val id = DownloadDatabase.DAO.insertTask(taskInfo)
        if (id != -1L) {
            taskInfo.id = id
            downloadListener.onStatusChanged(taskInfo)
            schedule()
        } else {
            //任务已存在, 重新开启任务
            resume(taskInfo)
        }
    }

    /**
     * 暂停任务下载
     */
    fun pause(taskInfo: TaskInfo) = taskScheduler.postAction {
        val affectedRows = DownloadDatabase.DAO.compareAndUpdateStatusByIdentity(taskInfo.identity,
            TaskInfo.TaskStatus.PAUSED, listOf(TaskInfo.TaskStatus.RUNNING, TaskInfo.TaskStatus.PENDING))
        if (affectedRows > 0) {
            schedule()
        }
    }

    /**
     * 暂停后重新开始下载
     */
    fun resume(taskInfo: TaskInfo) = taskScheduler.postAction {
        val affectedRows = DownloadDatabase.DAO.compareAndUpdateStatusByIdentity(taskInfo.identity,
            TaskInfo.TaskStatus.PENDING, listOf(TaskInfo.TaskStatus.PAUSED, TaskInfo.TaskStatus.ERROR))
        if (affectedRows > 0) {
            downloadListener.onStatusChanged(DownloadDatabase.DAO.queryByIdentity(taskInfo.identity))
            schedule()
        }
    }

    /**
     * 删除任务
     * @param deleteFile 是否删除已下载的文件
     */
    fun deleteByIdentity(identity: String, deleteFile: Boolean) = taskScheduler.postAction {
        val status = if (deleteFile) TaskInfo.TaskStatus.DELETING_WITH_FILE else TaskInfo.TaskStatus.DELETING_RECORD
        val delIdentity = "identity-del"
        val affectedRows = DownloadDatabase.DAO.updateDeletingStatusByIdentity(identity, status, delIdentity)
        if (affectedRows > 0) {
            val taskInfo = DownloadDatabase.DAO.queryByIdentity(delIdentity)
            taskInfo?.let {
                File(taskInfo.filePath).delete()
                downloadListener.onStatusChanged(taskInfo)
            }
            schedule()
        }
    }

    /**
     * 先删除,再下载
     */
    fun deleteAndDownload(taskInfo: TaskInfo, deleteFile: Boolean) = taskScheduler.postAction {
        val status = if (deleteFile) TaskInfo.TaskStatus.DELETING_WITH_FILE else TaskInfo.TaskStatus.DELETING_RECORD
        val delIdentity = "identity-del"
        val affectedRows = DownloadDatabase.DAO.updateDeletingStatusByIdentity(taskInfo.identity, status, delIdentity)
        if (affectedRows > 0) {
            schedule()
        }
        download(taskInfo)
    }

    /**
     * 查询所有下载任务
     */
    fun queryAllTask(): MutableList<TaskInfo>? {
        return DownloadDatabase.DAO.queryStatusTasks(QueryConst.allStatus)
    }

    private fun notifyTaskStatusChanged(taskInfo: TaskInfo?) {
        for (listener in listeners) {
            listener.onStatusChanged(taskInfo)
        }
    }

    private fun notifyTaskProgressChanged(taskInfo: TaskInfo) {
        for (listener in listeners) {
            listener.onProgressChanged(taskInfo, taskInfo.downBytes, taskInfo.totalBytes)
        }
    }

}