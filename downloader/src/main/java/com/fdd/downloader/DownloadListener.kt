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
     * @param taskInfo 状态发生变化的 TaskInfo, 为 null 时是批量变化(批量暂停/恢复,批量删除等操作)
     */
    fun onStatusChanged(taskInfo: TaskInfo?)

    /**
     * 下载进度更新
     */
    fun onProgressChanged(taskInfo: TaskInfo, downBytes: Long, totalBytes: Long)

}