package com.dhl.base.downloader

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 17:52
 * Description:
 *
 */
interface DownloadListener {

    /**
     * 等待下载开始
     */
    fun onPending(taskInfo: TaskInfo)

    /**
     * 下载开始
     */
    fun onStart(taskInfo: TaskInfo)

    /**
     * 下载停止
     */
    fun onStop(taskInfo: TaskInfo)

    /**
     * 下载进度更新
     */
    fun onProgressChanged(taskInfo: TaskInfo, downBytes: Long, totalBytes: Long)

    /**
     * 下载完成
     */
    fun onFinish(taskInfo: TaskInfo)

    /**
     * 下载失败
     */
    fun onError(taskInfo: TaskInfo, errorCode: Int)

    /**
     * 任务被删除
     */
    fun onDelete(taskInfo: TaskInfo)

}