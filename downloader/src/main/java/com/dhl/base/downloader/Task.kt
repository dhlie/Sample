package com.dhl.base.downloader

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 17:12
 * Description:
 *
 */
interface Task {

    val taskInfo: TaskInfo

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
     */
    fun delete()
}