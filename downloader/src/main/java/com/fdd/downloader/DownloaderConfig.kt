package com.fdd.downloader

import android.content.Context
import okhttp3.OkHttpClient

/**
 *
 * Author: duanhl
 * Create: 2021/8/17 10:54 下午
 * Description:
 *
 */
class DownloaderConfig(val context: Context) {

    private var httpClient: OkHttpClient? = null
    /**最大同时下载个数*/
    var maxRunningTaskCount = 2
    /**进度变化回调时间间隔*/
    var progressNotifyInterval = 100

    fun httpClient(client: OkHttpClient) {
        httpClient = client
    }

    fun getOkHttpClient(): OkHttpClient {
        if (httpClient == null) {
            synchronized(DownloadManager::class.java) {
                if (httpClient == null) {
                    val builder = OkHttpClient.Builder()
                    httpClient = builder.build()
                }
            }
        }
        return httpClient!!
    }

    /**
     * 设置最大同时下载个数
     * */
    fun maxRunningTaskCount(maxCount: Int) {
        maxRunningTaskCount = maxCount
    }

    /**
     * 设置进度变化回调时间间隔
     * */
    fun progressNotifyInterval(interval: Int) {
        progressNotifyInterval = interval
    }

}