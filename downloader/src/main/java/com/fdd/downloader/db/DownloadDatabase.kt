package com.fdd.downloader.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fdd.downloader.DownloadManager
import com.fdd.downloader.TaskInfo

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/11 16:38
 * Description:
 *
 */
@Database(entities = [TaskInfo::class], version = 1, exportSchema = true)
@TypeConverters(value = [Converters::class])
internal abstract class DownloadDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        private const val DB_NAME = "download.db"
        const val TABLE_TASK = "task"

        val INSTANCE: DownloadDatabase by lazy {
            Room.databaseBuilder(
                DownloadManager.config.context,
                DownloadDatabase::class.java,
                DB_NAME
            ).build()
        }

        val DAO: TaskDao by lazy {
            INSTANCE.taskDao()
        }

    }
}