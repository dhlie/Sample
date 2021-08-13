package com.dhl.base.downloader

import com.dhl.base.downloader.db.DownloadDatabase

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 18:53
 * Description:
 *
 */
class DownloadManager private constructor() {

    companion object {
        val instance: DownloadManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DownloadManager()
        }
    }

    private val taskScheduler = TaskScheduler()

    private fun schedule() {
        taskScheduler.triggerSchedule()
    }

    /**
     * 下载, 如果任务已存在不会有任何操作
     */
    fun download(taskInfo: TaskInfo) {
        val id = DownloadDatabase.DAO.insertTask(taskInfo)
        if (id != -1L) {
            schedule()
        }
    }

    /**
     * 暂停任务下载
     */
    fun pause(taskInfo: TaskInfo) {
        val affectedRows = DownloadDatabase.DAO.updateStatus(taskInfo.id, TaskInfo.TaskStatus.PAUSED)
        if (affectedRows > 0) {
            schedule()
        }
    }

    /**
     * 暂停后重新开始下载
     */
    fun resume(taskInfo: TaskInfo) {
        val affectedRows = DownloadDatabase.DAO.updateStatus(taskInfo.id, TaskInfo.TaskStatus.PENDING)
        if (affectedRows > 0) {
            schedule()
        }
    }

    /**
     * 删除任务
     */
    fun delete(taskInfo: TaskInfo) {
        val affectedRows = DownloadDatabase.DAO.delete(taskInfo)
        if (affectedRows > 0) {
            schedule()
        }
    }

}