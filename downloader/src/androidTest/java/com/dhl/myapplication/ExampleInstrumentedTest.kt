package com.dhl.myapplication

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dhl.base.ContextHolder
import com.dhl.base.downloader.DownloadManager
import com.dhl.base.downloader.TaskInfo
import com.dhl.base.downloader.db.DownloadDatabase
import com.dhl.base.downloader.db.TaskDao
import com.dhl.base.log
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var userDao: TaskDao
    private lateinit var db: DownloadDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        ContextHolder.appContext = context.applicationContext as Application
        db = Room.inMemoryDatabaseBuilder(
            context, DownloadDatabase::class.java).build()
        userDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testDB() {
        val taskInfo: TaskInfo = TaskInfo.Builder()
            .url("url")
            .addHeader("token", "token")
            .addHeader("header", "value")
            .build()
        var id = userDao.insertTask(taskInfo)
        taskInfo.id = id
        id = userDao.insertTask(taskInfo)

        var rows = DownloadManager.instance.pause(taskInfo)
        val ti = userDao.queryById(taskInfo.id)
        val ts = userDao.queryAll()
        val rows1 = userDao.delete(taskInfo)

        val ts1 = userDao.queryAll()

        log { "id:$id" }
    }
}