package com.dhl.base.downloader.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dhl.base.downloader.TaskInfo

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/11 16:32
 * Description:
 *
 */

class QueryConst {
    companion object {
        val scheduleStatus = listOf(
            TaskInfo.TaskStatus.PENDING,
            TaskInfo.TaskStatus.RUNNING,
            TaskInfo.TaskStatus.DELETING_RECORD,
            TaskInfo.TaskStatus.DELETING_WITH_FILE,
        )

        val unFinishStatus = listOf(
            TaskInfo.TaskStatus.PENDING,
            TaskInfo.TaskStatus.RUNNING,
            TaskInfo.TaskStatus.PAUSED,
            TaskInfo.TaskStatus.ERROR,
        )

        val allStatus = listOf(
            TaskInfo.TaskStatus.PENDING,
            TaskInfo.TaskStatus.RUNNING,
            TaskInfo.TaskStatus.PAUSED,
            TaskInfo.TaskStatus.ERROR,
            TaskInfo.TaskStatus.FINISH,
        )
    }
}

@Dao
interface TaskDao {

    /**
     * 插入下载任务
     * Returns: The SQLite row id or -1 if no row is inserted
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTask(taskInfo: TaskInfo): Long

    /**
     * 更新下载状态
     * Returns: The number of affected rows
     */
    @Query("update ${DownloadDatabase.TABLE_TASK} set ${TaskInfo.COLUMN_STATUS} = :status where ${TaskInfo.COLUMN_ID} = :id")
    fun updateStatus(id: Long, status: TaskInfo.TaskStatus): Int

    /**
     * 更新下载状态和错误码
     * Returns: The number of affected rows
     */
    @Query("update ${DownloadDatabase.TABLE_TASK} set ${TaskInfo.COLUMN_STATUS} = :status, ${TaskInfo.COLUMN_ERROR_CODE} = :errorCode where ${TaskInfo.COLUMN_ID} = :id")
    fun updateStatusAndErrorCode(id: Long, status: TaskInfo.TaskStatus, errorCode: Int): Int

    /**
     * 根据当前状态更新下载状态
     */
    @Query("update ${DownloadDatabase.TABLE_TASK} set ${TaskInfo.COLUMN_STATUS} = :update where ${TaskInfo.COLUMN_URL} = :url and ${TaskInfo.COLUMN_STATUS} in (:expect)")
    fun compareAndUpdateStatusByUrl(url: String, update: TaskInfo.TaskStatus, expect: List<TaskInfo.TaskStatus>): Int

    /**
     * 根据状态查询任务
     */
    @Query("select * from ${DownloadDatabase.TABLE_TASK} where ${TaskInfo.COLUMN_STATUS} in (:status) order by ${TaskInfo.COLUMN_CREATED_TIME} desc")
    fun queryStatusTasks(status: List<TaskInfo.TaskStatus>): MutableList<TaskInfo>?

    /**
     * 删除下载任务
     * Returns: The number of affected rows
     */
    @Delete
    fun delete(taskInfo: TaskInfo): Int

    /**
     * 更新下载进度
     */
    @Query("update ${DownloadDatabase.TABLE_TASK} set ${TaskInfo.COLUMN_DOWN_BYTES} = :downBytes, ${TaskInfo.COLUMN_TOTAL_BYTES} = :totalBytes where ${TaskInfo.COLUMN_ID} = :id")
    fun updateProgress(id: Long, downBytes: Long, totalBytes: Long): Int

    /**
     * 文件路径是否已存在
     */
    @Query("select count(*) from ${DownloadDatabase.TABLE_TASK} where ${TaskInfo.COLUMN_FILE_PATH} = :path")
    fun isPathExist(path: String): Boolean

}