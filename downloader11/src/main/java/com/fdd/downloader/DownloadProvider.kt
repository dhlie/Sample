package com.fdd.downloader

import okhttp3.OkHttpClient
import java.io.File

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 17:17
 * Description:
 *
 */
class DownloadProvider private constructor(){

    companion object {
        val instance: DownloadProvider by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { DownloadProvider() }

        /**最大同时下载个数*/
        private const val MAX_RUNNING_COUNT = 2
    }

    private var httpClient: OkHttpClient? = null

    fun setHttpClient(client: OkHttpClient) {
        httpClient = client
    }

    fun provideOkHttpClient(): OkHttpClient {
        if (httpClient == null) {
            synchronized(DownloadProvider::class.java) {
                if (httpClient == null) {
                    httpClient = newHttpClient()
                }
            }
        }
        return httpClient!!
    }

    fun tempFileSavedDir(filePath: String): String {
        val file = File(filePath)
        return FileUtil.concat(file.parent, ".tmp")
    }

    fun maxRunningTaskCount() = MAX_RUNNING_COUNT

    private fun newHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        return builder.build()
    }

}