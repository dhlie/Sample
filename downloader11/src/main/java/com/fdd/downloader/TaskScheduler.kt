package com.fdd.downloader

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.PowerManager
import com.fdd.downloader.db.DownloadDatabase
import com.fdd.downloader.db.QueryConst
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

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
        const val MSG_STATUS_CHANGE = 0x1002

        private fun TaskInfo.toDebugString(): String {
            return "$title | $status | $downBytes/$totalBytes"
        }
    }

    private val threadHandler: Handler
    private val wakeLock: PowerManager.WakeLock
    private val wifiLock: WifiManager.WifiLock
    private val runningTasks = LinkedList<Task>()
    private val connectManager: ConnectivityManager

    var downloadListener: DownloadListener? = null

    private var taskListener = object : DownloadTask.TaskListener {
        override fun onPending(task: DownloadTask) {
            val affectedRows = DownloadDatabase.DAO.updateStatus(task.taskInfo.id, TaskInfo.TaskStatus.PENDING)
            if (affectedRows > 0) {
                task.taskInfo.status = TaskInfo.TaskStatus.PENDING
                triggerStatusChange(task.taskInfo)
                triggerSchedule()
            }
            log { "onPending--${task.taskInfo.toDebugString()}" }
        }

        override fun onStart(task: DownloadTask): Boolean {
            val affectedRows = DownloadDatabase.DAO.updateStatus(task.taskInfo.id, TaskInfo.TaskStatus.RUNNING)
            if (affectedRows > 0) {
                task.taskInfo.status = TaskInfo.TaskStatus.RUNNING
                triggerStatusChange(task.taskInfo)
                return true
            }
            log { "onStart--${task.taskInfo.toDebugString()}" }
            return false
        }

        override fun onStop(task: DownloadTask) {
            val affectedRows = DownloadDatabase.DAO.updateStatus(task.taskInfo.id, TaskInfo.TaskStatus.PAUSED)
            if (affectedRows > 0) {
                task.taskInfo.status = TaskInfo.TaskStatus.PAUSED
                triggerStatusChange(task.taskInfo)
            }
            log { "onStop--${task.taskInfo.toDebugString()}" }
        }

        override fun onProgressChanged(task: DownloadTask, downBytes: Long, totalBytes: Long) {
            val affectedRows = DownloadDatabase.DAO.updateProgress(task.taskInfo.id, downBytes, totalBytes)
            if (affectedRows > 0) {
                downloadListener?.onProgressChanged(task.taskInfo, downBytes, totalBytes)
            }
            log { "onProgressChanged--${task.taskInfo.toDebugString()}" }
        }

        override fun onSaveFileLength(task: DownloadTask, length: Long): Boolean {
            val affectedRows = DownloadDatabase.DAO.updateProgress(task.taskInfo.id, task.taskInfo.downBytes, task.taskInfo.totalBytes)
            log { "onSaveFileLength--${task.taskInfo.toDebugString()}" }
            return affectedRows > 0
        }

        override fun onFinish(task: DownloadTask) {
            val affectedRows = DownloadDatabase.DAO.updateStatus(task.taskInfo.id, TaskInfo.TaskStatus.FINISH)
            if (affectedRows > 0) {
                task.taskInfo.status = TaskInfo.TaskStatus.FINISH
                triggerStatusChange(task.taskInfo)
                triggerSchedule()
            }
            log { "onFinish--${task.taskInfo.toDebugString()}" }
        }

        override fun onError(task: DownloadTask, errorCode: Int) {
            val affectedRows = DownloadDatabase.DAO.updateStatusAndErrorCode(task.taskInfo.id, TaskInfo.TaskStatus.ERROR, errorCode)
            if (affectedRows > 0) {
                task.taskInfo.status = TaskInfo.TaskStatus.ERROR
                task.taskInfo.errorCode = errorCode
                triggerStatusChange(task.taskInfo)
                triggerSchedule()
            }
            log { "onError--${task.taskInfo.toDebugString()} error:$errorCode" }
        }

        override fun onDelete(task: DownloadTask, hasClearUp: Boolean) {
            if (hasClearUp) {
                DownloadDatabase.DAO.delete(task.taskInfo)
            }
            task.taskInfo.status = TaskInfo.TaskStatus.DELETING_RECORD
            triggerStatusChange(task.taskInfo)
            log { "onDelete--${task.taskInfo.toDebugString()}" }
        }

    }

    init {
        val context = getContext()
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
            MSG_SCHEDULE -> schedule()
            MSG_STATUS_CHANGE -> statusChanged(msg.obj as TaskInfo)
        }
        return true
    }

    fun triggerStatusChange(taskInfo: TaskInfo) {
        threadHandler.obtainMessage(MSG_STATUS_CHANGE, taskInfo).sendToTarget()
    }

    fun triggerSchedule() {
        threadHandler.sendEmptyMessage(MSG_SCHEDULE)
    }

    fun postAction(action: () -> Unit) {
        threadHandler.post(action)
    }

    private fun schedule() {
        threadHandler.removeMessages(MSG_SCHEDULE)

        val tasks = DownloadDatabase.DAO.queryStatusTasks(QueryConst.scheduleStatus) ?: ArrayList(0)

        tasks.forEach { taskInfo ->
            if (taskInfo.status != TaskInfo.TaskStatus.DELETING_RECORD && taskInfo.status != TaskInfo.TaskStatus.DELETING_WITH_FILE) {
                return@forEach
            }

            val task: Task? = runningTasks.find { it.taskInfo.id == taskInfo.id }?.apply {
                runningTasks.remove(this)
            }
            deleteTask(taskInfo, task)
        }

        //val networkCapabilities: NetworkCapabilities? = connectManager.getNetworkCapabilities(connectManager.activeNetwork)
        // TODO() 判断网络是否连接,其它网络类型 bluetooth .etc
        //val isWifi = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        //val isCellular = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        var runningTaskCount = 0

        val iterator = runningTasks.iterator()
        while (iterator.hasNext()) {
            val task = iterator.next()
            val runningTaskInfo = tasks.find { it.id == task.taskInfo.id }

            if (runningTaskInfo == null) {
                stopTask(task)
                iterator.remove()
            } else {
                if (runningTaskInfo.status == TaskInfo.TaskStatus.RUNNING) {
                    if (task.taskInfo.visible) {
                        runningTaskCount++
                    }
                } else if (runningTaskInfo.status == TaskInfo.TaskStatus.PENDING) {
                    pendingTask(task.taskInfo, task)
                    iterator.remove()
                }
            }
        }

        tasks.forEach { taskInfo ->
            if (taskInfo.status == TaskInfo.TaskStatus.RUNNING && runningTasks.find { taskInfo.id == it.taskInfo.id } == null) {
                val task = startTask(taskInfo)
                if (task.taskInfo.visible) {
                    runningTaskCount++
                }
            }
        }

        for (taskInfo in tasks) {
            if (taskInfo.status != TaskInfo.TaskStatus.PENDING) {
                continue
            }

            //开启后台下载任务,不受下载任务个数限制
            if (!taskInfo.visible) {
                startTask(taskInfo)
                continue
            }

            if (runningTaskCount < DownloadProvider.instance.maxRunningTaskCount()) {
                startTask(taskInfo)
                runningTaskCount++
            }
        }

        if (runningTasks.size > 0) {
            acquireLocks()
        } else {
            releaseLocks()
        }

        log {
            val builder = StringBuilder("  \nTaskList")
            builder.append("[")
            runningTasks.forEach { task ->
                builder.append("\n\t").append(task.taskInfo.toDebugString())
            }
            if (runningTasks.size > 0) {
                builder.append("\n")
            }
            builder.append("]")
            builder.toString()
        }
    }

    private fun statusChanged(taskInfo: TaskInfo) {
        threadHandler.removeMessages(MSG_STATUS_CHANGE, taskInfo)
        downloadListener?.onStatusChanged(taskInfo)
    }

    private fun createTask(taskInfo: TaskInfo): Task {
        return DownloadTask(taskInfo).apply {
            listener = taskListener
        }
    }

    private fun startTask(taskInfo: TaskInfo): Task {
        val task = createTask(taskInfo)
        task.start()
        runningTasks.add(task)
        return task
    }

    private fun pendingTask(taskInfo: TaskInfo, runningTask: Task?) {
        val task = runningTask ?: createTask(taskInfo)
        task.pending()
    }

    private fun stopTask(task: Task) {
        task.stop()
    }

    /**
     * 删除任务: 清理临时文件
     */
    private fun deleteTask(taskInfo: TaskInfo, runningTask: Task? = null) {
        val task = runningTask ?: createTask(taskInfo)
        task.delete(taskInfo.status == TaskInfo.TaskStatus.DELETING_WITH_FILE)
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