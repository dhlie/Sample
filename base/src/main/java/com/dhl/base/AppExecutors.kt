package com.dhl.base

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * Author: duanhaoliang
 * Create: 2021/7/6 9:27
 * Description:
 *
 */
object AppExecutors {

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    private val diskIO = Executors.newFixedThreadPool(2, object : ThreadFactory {
        private val THREAD_NAME_STEM = "fdd_disk_io_%d"
        private val threadId = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
            val t = Thread(r)
            t.name = String.format(THREAD_NAME_STEM, threadId.getAndIncrement())
            return t
        }
    })

    private val netIO = Executors.newFixedThreadPool(4, object : ThreadFactory {
        private val THREAD_NAME_STEM = "fdd_net_io_%d"
        private val threadId = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
            val t = Thread(r)
            t.name = String.format(THREAD_NAME_STEM, threadId.getAndIncrement())
            return t
        }
    })

    fun executeOnMain(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            mainHandler.post(runnable)
        }
    }

    fun postOnMain(runnable: Runnable, delay: Long = 0L) {
        mainHandler.postDelayed(runnable, delay)
    }

    fun executeOnDiskIO(runnable: Runnable) {
        diskIO.execute(runnable)
    }

    fun executeOnNetIO(runnable: Runnable) {
        netIO.execute(runnable)
    }
}