package com.dhl.base.downloader

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 18:20
 * Description:
 *
 */
class GlobalDownloadListener private constructor(): DownloadListener {

    companion object {
        val instance: GlobalDownloadListener by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GlobalDownloadListener()
        }
    }

    fun onStatusChange() {

    }

    override fun onPending(taskInfo: TaskInfo) {
        TODO("Not yet implemented")
    }

    override fun onStart(taskInfo: TaskInfo) {
        TODO("Not yet implemented")
    }

    override fun onStop(taskInfo: TaskInfo) {
        TODO("Not yet implemented")
    }

    override fun onProgressChanged(taskInfo: TaskInfo, downBytes: Long, totalBytes: Long) {
        TODO("Not yet implemented")
    }

    override fun onFinish(taskInfo: TaskInfo) {
        TODO("Not yet implemented")
    }

    override fun onError(taskInfo: TaskInfo, errorCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onDelete(taskInfo: TaskInfo) {
        TODO("Not yet implemented")
    }

}