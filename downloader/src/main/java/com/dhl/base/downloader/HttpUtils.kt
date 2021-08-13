package com.dhl.base.downloader

import android.os.Environment
import android.webkit.MimeTypeMap
import com.dhl.base.ContextHolder
import com.dhl.base.downloader.db.DownloadDatabase
import com.dhl.base.log
import com.dhl.base.utils.FileUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import java.io.File
import java.nio.file.Paths


/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/9 14:38
 * Description:
 *
 */
class HttpUtils private constructor() {


    //https://download.jetbrains.com.cn/python/pycharm-community-2021.1.1.exe
    companion object {



        fun download(url: String) {

            val fileDir = ContextHolder.appContext.getExternalFilesDir(null)
            val fileDirDown = ContextHolder.appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val fileDirPic = ContextHolder.appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            //File(fileDir, "root.txt").createNewFile()
            //File(fileDirDown, "down.txt").createNewFile()
            //File(fileDirPic, "pic.txt").createNewFile()
            //File(fileDirDown, "doc/").mkdirs()
            //File(fileDirDown, "doc/doc.txt").createNewFile()




            val userDao = DownloadDatabase.DAO

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
            var rows1 = DownloadManager.instance.pause(taskInfo)
            val ti2 = userDao.queryById(taskInfo.id)
            var rows2 = DownloadManager.instance.resume(taskInfo)
            val ti3 = userDao.queryById(taskInfo.id)
            var rows3 = DownloadManager.instance.resume(taskInfo)
            val ti4 = userDao.queryById(taskInfo.id)
            var rows4 = DownloadManager.instance.delete(taskInfo)
            val ti5 = userDao.queryById(taskInfo.id)
            var rows5 = DownloadManager.instance.delete(taskInfo)
            val ti6 = userDao.queryById(taskInfo.id)
            //val rows6 = userDao.update(taskInfo.apply { status = TaskInfo.TaskStatus.RUNNING })
            val ti7 = userDao.queryById(taskInfo.id)

            val ts = userDao.queryAll()
        }
    }
}