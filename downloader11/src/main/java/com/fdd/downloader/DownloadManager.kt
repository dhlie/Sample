package com.fdd.downloader

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.fdd.downloader.db.DownloadDatabase
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
        lateinit var context: Context

        val instance: DownloadManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DownloadManager()
        }

        fun init(ctx: Context) {
            context = ctx.applicationContext
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
                    MSG_TASK_STATUS_CHANGED -> notifyTaskStatusChanged(msg.obj as TaskInfo)
                    MSG_TASK_PROGRESS_CHANGED -> notifyTaskProgressChanged(msg.obj as TaskInfo)
                }
            }
        }
        downloadListener = object : DownloadListener {
            override fun onStatusChanged(taskInfo: TaskInfo) {
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
            schedule()
        } else {
            //任务已存在, 重新开启任务
            val affectedRows = DownloadDatabase.DAO.compareAndUpdateStatusByFileId(taskInfo.fileId,
                TaskInfo.TaskStatus.PENDING, listOf(TaskInfo.TaskStatus.PAUSED, TaskInfo.TaskStatus.ERROR))
            if (affectedRows > 0) {
                schedule()
            }
        }
    }

    /**
     * 暂停任务下载
     */
    fun pause(taskInfo: TaskInfo) = taskScheduler.postAction {
        val affectedRows = DownloadDatabase.DAO.compareAndUpdateStatusByFileId(taskInfo.fileId,
            TaskInfo.TaskStatus.PAUSED, listOf(TaskInfo.TaskStatus.RUNNING))
        if (affectedRows > 0) {
            schedule()
        }
    }

    /**
     * 暂停后重新开始下载
     */
    fun resume(taskInfo: TaskInfo) = taskScheduler.postAction {
        val affectedRows = DownloadDatabase.DAO.compareAndUpdateStatusByFileId(taskInfo.fileId,
            TaskInfo.TaskStatus.PENDING, listOf(TaskInfo.TaskStatus.PAUSED, TaskInfo.TaskStatus.ERROR))
        if (affectedRows > 0) {
            schedule()
        }
    }

    /**
     * 删除任务
     * @param deleteFile 是否删除已下载的文件
     */
    fun delete(taskInfo: TaskInfo, deleteFile: Boolean) = taskScheduler.postAction {
        val status = if (deleteFile) TaskInfo.TaskStatus.DELETING_WITH_FILE else TaskInfo.TaskStatus.DELETING_RECORD
        val affectedRows = DownloadDatabase.DAO.updateStatus(taskInfo.id, status)
        if (affectedRows > 0) {
            schedule()
        }
    }

    /**
     * 删除任务
     * @param deleteFile 是否删除已下载的文件
     */
    fun deleteByFileId(fileId: String, deleteFile: Boolean, callback: ((Int) -> Unit)?) = taskScheduler.postAction {
        val status = if (deleteFile) TaskInfo.TaskStatus.DELETING_WITH_FILE else TaskInfo.TaskStatus.DELETING_RECORD
        val affectedRows = DownloadDatabase.DAO.updateStatusByFileId(fileId, status)
        if (affectedRows > 0) {
            schedule()
        }
        callback?.invoke(affectedRows)
    }

    private fun notifyTaskStatusChanged(taskInfo: TaskInfo) {
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