package com.fdd.downloader

import android.os.Environment
import com.fdd.downloader.db.DownloadDatabase
import java.io.File

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/11 13:50
 * Description:
 *
 */
internal class PathUtil private constructor(){
    companion object {

        /**
         * 下载默认存储路径 /sdcard/Android/data/packagename/files/downloads
         */
        private var defaultDownloadDir: String = DownloadManager.config.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath ?: DownloadManager.config.context.getExternalFilesDir(null)!!.absolutePath

        /**
         * 根据 url或path 获取文件类型
         */
        fun getFileType(uri: String): TaskInfo.FileType {
            val ext = FileUtil.getFileExtension(uri)
            return when {
                TaskInfo.FileType.DOCUMENT.contains(ext) -> TaskInfo.FileType.DOCUMENT
                TaskInfo.FileType.IMAGE.contains(ext) -> TaskInfo.FileType.IMAGE
                TaskInfo.FileType.APK.contains(ext) -> TaskInfo.FileType.APK
                else -> TaskInfo.FileType.UNKNOWN
            }
        }

        /**
         * 获取文件保存位置
         * @param fullFileName 带后缀的文件名
         */
        fun getFilePath(fullFileName: String, dir: String? = null): String {
            val name = FileUtil.getFileNameWithoutExtension(fullFileName)
            val ext = FileUtil.getFileExtension(fullFileName)
            //替换掉名字中的非法字符, 限制名字长度
            val legalFileName = FileSystem.LINUX.toLegalFileName(name, '_').run {
                val maxLength = 200/**max 255*/
                if (length + ext.length >= maxLength) {
                    substring(0, maxLength - ext.length)
                } else {
                    this
                }
            }

            val dirPath = if (dir.isNullOrBlank()) {
                defaultDownloadDir
            } else {
                dir
            }
            val extWithDot = if (ext.isEmpty()) "" else ".$ext"
            var path = FileUtil.concat(dirPath, "$legalFileName$extWithDot")
            if (!DownloadDatabase.DAO.isPathExist(path) && !File(path).exists()) {
                return path
            }

            for (i in 1..1000) {
                path = FileUtil.concat(dirPath, "${legalFileName}_$i$extWithDot")
                if (!DownloadDatabase.DAO.isPathExist(path) && !File(path).exists()) {
                    break
                }
            }
            return path
        }
    }
}