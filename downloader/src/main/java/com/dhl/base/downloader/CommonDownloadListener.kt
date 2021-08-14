package com.dhl.base.downloader

/**
 *
 * Author: duanhl
 * Create: 2021/8/15 12:41 下午
 * Description:
 *
 */
abstract class CommonDownloadListener : DownloadListener {
    override fun onPending(taskInfo: TaskInfo) {
        onStatusChanged(taskInfo)
    }

    override fun onStart(taskInfo: TaskInfo) {
        onStatusChanged(taskInfo)
    }

    override fun onStop(taskInfo: TaskInfo) {
        onStatusChanged(taskInfo)
    }

    override fun onFinish(taskInfo: TaskInfo) {
        onStatusChanged(taskInfo)
    }

    override fun onError(taskInfo: TaskInfo, errorCode: Int) {
        onStatusChanged(taskInfo)
    }

    override fun onDelete(taskInfo: TaskInfo) {
        onStatusChanged(taskInfo)
    }

    abstract fun onStatusChanged(taskInfo: TaskInfo)
}