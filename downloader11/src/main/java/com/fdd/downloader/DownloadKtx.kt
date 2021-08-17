package com.fdd.downloader

import android.util.Log

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/17 14:31
 * Description:
 *
 */

fun getContext() = DownloadManager.context

inline fun log(tag: String = "Downloader", level: Int = Log.INFO, block: () -> String) {
    if (true) {
        when (level) {
            Log.VERBOSE -> Log.v(tag, block.invoke())
            Log.DEBUG -> Log.d(tag, block.invoke())
            Log.INFO -> Log.i(tag, block.invoke())
            Log.WARN -> Log.w(tag, block.invoke())
            Log.ERROR -> Log.e(tag, block.invoke())
        }
    }
}