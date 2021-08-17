package com.fdd.downloader

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 17:52
 * Description:
 *
 */
interface DownloadListener {

    /**
     * 下载状态发生变化
     */
    fun onStatusChanged(taskInfo: TaskInfo)

    /**
     * 下载进度更新
     */
    fun onProgressChanged(taskInfo: TaskInfo, downBytes: Long, totalBytes: Long)

}