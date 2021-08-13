package com.dhl.base.downloader.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dhl.base.downloader.TaskInfo

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/11 16:32
 * Description:
 *
 */
@Dao
interface TaskDao {

    /**
     * Returns: The SQLite row id or -1 if no row is inserted
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTask(taskInfo: TaskInfo): Long

    /**
     * Returns: The number of affected rows
     */
    @Query("update ${DownloadDatabase.TABLE_TASK} set ${TaskInfo.COLUMN_STATUS} = :status where id = :id")
    fun updateStatus(id: Long, status: TaskInfo.TaskStatus): Int

    /**
     * 根据状态查询任务
     */
    @Query("select * from ${DownloadDatabase.TABLE_TASK} where ${TaskInfo.COLUMN_STATUS} in (:status)")
    fun queryStatusTasks(status: List<TaskInfo.TaskStatus>): ArrayList<TaskInfo>?

    /**
     * Returns: The number of affected rows
     */
    @Delete
    fun delete(taskInfo: TaskInfo): Int









    @Query("select * from ${DownloadDatabase.TABLE_TASK} where ${TaskInfo.COLUMN_ID} = :id")
    fun queryById(id: Long): TaskInfo

    @Query("select * from ${DownloadDatabase.TABLE_TASK}")
    fun queryAll(): List<TaskInfo>

    @Query("select * from ${DownloadDatabase.TABLE_TASK} where url = :url")
    fun getUserByUrl(url: String): TaskInfo?

    /**
     * Returns:The number of affected rows
     * */
    //@Query("update ${DownloadDatabase.TABLE_TASK} set title = :title where id =  :id")
    //fun update(id: Long, title: String): Int

    @Query("select count(*) from ${DownloadDatabase.TABLE_TASK} where ${TaskInfo.COLUMN_FILE_PATH} = :path")
    fun queryPathCount(path: String): Int

    fun isPathExist(path: String) = queryPathCount(path) > 0
}