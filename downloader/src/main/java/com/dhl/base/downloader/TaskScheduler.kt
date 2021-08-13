package com.dhl.base.downloader

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.PowerManager
import com.dhl.base.ContextHolder
import com.dhl.base.downloader.db.DownloadDatabase

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/12 20:57
 * Description:
 *
 */
@SuppressLint("InvalidWakeLockTag")
class TaskScheduler : Handler.Callback {

    companion object {
        const val MSG_SCHEDULE = 0x1001

        private val scheduleStatus = listOf(
            TaskInfo.TaskStatus.PENDING,
            TaskInfo.TaskStatus.RUNNING,
            TaskInfo.TaskStatus.DELETING
        )
    }

    private val threadHandler: Handler
    private val wakeLock: PowerManager.WakeLock
    private val wifiLock: WifiManager.WifiLock
    private val runningTasks = mutableListOf<Task>()
    private val connectManager: ConnectivityManager

    private val downloadListener = object : DownloadListener {
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

    init {
        val context = ContextHolder.appContext
        connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val thread = HandlerThread("TaskScheduler-HandlerThread")
        thread.start()
        threadHandler = Handler(thread.looper, this)

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TaskScheduler-WakeLock")
        val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "TaskScheduler-WifiLock")
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_SCHEDULE -> {
                schedule()
            }
        }
        return true
    }

    fun triggerSchedule() {
        threadHandler.removeMessages(MSG_SCHEDULE)
        threadHandler.sendEmptyMessage(MSG_SCHEDULE)
    }

    fun postAction(action: () -> Unit) {
        threadHandler.post { action.invoke() }
    }

    private fun schedule() {
        threadHandler.removeMessages(MSG_SCHEDULE)

        val tasks = DownloadDatabase.DAO.queryStatusTasks(scheduleStatus) ?: ArrayList(0)

        tasks.forEach { taskInfo ->
            if (taskInfo.status != TaskInfo.TaskStatus.DELETING) {
                return@forEach
            }

            val task: Task? = runningTasks.find { it.taskInfo.id == taskInfo.id }?.apply {
                runningTasks.remove(this)
            }
            deleteTask(taskInfo, task)
        }

        val networkCapabilities: NetworkCapabilities? = connectManager.getNetworkCapabilities(connectManager.activeNetwork)
        val isWifi = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val isCellular = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        var runningTaskCount = 0

        val iterator = runningTasks.iterator()
        while (iterator.hasNext()) {
            val task = iterator.next()
            val runningTaskInfo = tasks.find { it.status == TaskInfo.TaskStatus.RUNNING && it.id == task.taskInfo.id }

            if (runningTaskInfo == null) {
                stopTask(task)
                iterator.remove()
            } else {
                tasks.remove(runningTaskInfo)
                if (task.taskInfo.visible) {
                    runningTaskCount++
                }
            }
        }

        tasks.forEach { taskInfo ->
            if (taskInfo.status != TaskInfo.TaskStatus.RUNNING) {
                return@forEach
            }
            val task = startTask(taskInfo)
            if (task.taskInfo.visible) {
                runningTaskCount++
            }
        }


        if (runningTaskCount < DownloadProvider.instance.maxRunningTaskCount()) {
            for (taskInfo in tasks) {
                if (taskInfo.status != TaskInfo.TaskStatus.PENDING) {
                    continue
                }

                val task = startTask(taskInfo)
                DownloadDatabase.DAO.updateStatus(task.taskInfo.id, TaskInfo.TaskStatus.RUNNING)
                if (task.taskInfo.visible) {
                    runningTaskCount++
                }

                if (runningTaskCount >= DownloadProvider.instance.maxRunningTaskCount()) {
                    break
                }
            }
        }

        if (runningTasks.size > 0) {
            acquireLocks()
        } else {
            releaseLocks()
        }
    }

    private fun createTask(taskInfo: TaskInfo): Task {
        return DownloadTask(taskInfo).apply {
            listener = downloadListener
        }
    }

    private fun startTask(taskInfo: TaskInfo): Task {
        val task = createTask(taskInfo)
        task.start()
        runningTasks.add(task)
        return task
    }

    private fun stopTask(task: Task) {
        task.stop()
    }

    /**
     * 删除任务: 1. 清理临时文件
     */
    private fun deleteTask(taskInfo: TaskInfo, runningTask: Task? = null) {
        val task = runningTask ?: createTask(taskInfo)
        task.delete()
    }

    private fun acquireLocks() {
        if (!wakeLock.isHeld) {
            try {
                wakeLock.acquire(60 * 60 * 1000L /*60 minutes*/)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (!wifiLock.isHeld) {
            try {
                wifiLock.acquire()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun releaseLocks() {
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        if (wifiLock.isHeld) {
            wifiLock.release()
        }
    }
}