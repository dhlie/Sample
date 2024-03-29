package com.fdd.downloader

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 17:12
 * Description:
 *
 */
internal interface Task {

    val taskInfo: TaskInfo

    /**
     * 任务转为 pending 状态
     */
    fun pending()

    /**
     * 开始任务
     */
    fun start()

    /**
     * 停止任务,任务停止会回收所有资源
     */
    fun stop()

    /**
     * 删除任务
     * @param deleteFile 是否删除已下载的文件
     */
    fun delete(deleteFile: Boolean)
}