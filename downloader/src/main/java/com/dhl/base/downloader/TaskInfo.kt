package com.dhl.base.downloader

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dhl.base.downloader.TaskInfo.Companion.COLUMN_URL
import com.dhl.base.downloader.db.DownloadDatabase

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 15:42
 * Description:
 *
 */
@Entity(tableName = DownloadDatabase.TABLE_TASK, indices = [Index(value = [COLUMN_URL], unique = true)])
class TaskInfo {

    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_URL = "url"
        const val COLUMN_HEADER = "header"
        const val COLUMN_FILE_TYPE = "fileType"
        const val COLUMN_VISIBLE = "visible"
        const val COLUMN_TITLE = "title"
        const val COLUMN_THUMB = "thumb"
        const val COLUMN_CREATED_TIME = "createdTime"
        const val COLUMN_STATUS = "status"
        const val COLUMN_TOTAL_BYTES = "totalBytes"
        const val COLUMN_DOWN_BYTES = "downBytes"
        const val COLUMN_FILE_PATH = "filePath"
        const val COLUMN_ONLY_WIFI = "onlyWifi"
        const val COLUMN_ERROR_CODE = "errorCode"
        const val COLUMN_EXTRAS = "extras"

        const val ERROR_CREATE_OR_OPEN_FILE = 1         //文件创建或打开失败
        const val ERROR_NET_CONNECT = 2                 //网络连接错误
        const val ERROR_IO = 3                          //保存文件时读写错误
        const val ERROR_FILE_LENGTH = 4                 //文件大小和 content-length 不一致
        const val ERROR_MKDIR = 5                       //创建目录失败
        const val ERROR_RENAME = 6                      //下载完重命名失败
        const val ERROR_BUILD_REQUEST = 7               //创建请求失败(e.g. url 不合法)
        const val ERROR_SAVE_LENGTH = 8                 //保存文件大小失败(修改数据库失败)
        const val ERROR_CANNOT_START = 9                //无法开启任务(修改数据库任务状态失败)
    }

    /**
     * 下载任务类型
     */
    enum class FileType(private val extensions: Array<String>) {
        //未知类型
        UNKNOWN(emptyArray()),

        //文档
        DOCUMENT(arrayOf("doc", "docx", "wps", "xls", "xlsx", "pdf")),

        //图片
        IMAGE(arrayOf("png", "gif", "jpg", "jpeg", "webp")),

        //apk
        APK(arrayOf("apk"));

        fun contains(extension: String): Boolean {
            for (ext in extensions) {
                if (extension.equals(ext, true)) {
                    return true
                }
            }
            return false
        }

    }

    /**
     * 下载状态
     */
    enum class TaskStatus {
        PENDING,            //等待下载
        RUNNING,            //下载中
        PAUSED,             //暂停下载
        FINISH,             //下载完成
        ERROR,              //下载失败
        DELETING_RECORD,    //删除下载记录
        DELETING_WITH_FILE, //删除下载记录和文件
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    var id: Long = 0L                               //数据库中的id

    @ColumnInfo(name = COLUMN_URL)
    var url: String = ""                            //下载地址

    @ColumnInfo(name = COLUMN_STATUS)
    var status: TaskStatus = TaskStatus.PENDING     //下载状态

    @ColumnInfo(name = COLUMN_FILE_TYPE)
    var fileType: FileType = FileType.UNKNOWN       //文件类型

    @ColumnInfo(name = COLUMN_VISIBLE)
    var visible: Boolean = true                     //下载任务是否可见(后台静默下载),后台任务不受最大同时下载个数限制, 会立即开始下载

    @ColumnInfo(name = COLUMN_FILE_PATH)
    var filePath: String = ""                       //保存路径

    @ColumnInfo(name = COLUMN_TITLE)
    var title: String? = null                       //标题

    @ColumnInfo(name = COLUMN_THUMB)
    var thumb: String? = null                       //缩略图

    @ColumnInfo(name = COLUMN_CREATED_TIME)
    var createdTime: Long = System.currentTimeMillis()//任务创建时间

    @ColumnInfo(name = COLUMN_TOTAL_BYTES)
    var totalBytes: Long = -1L                      //文件大小

    @ColumnInfo(name = COLUMN_DOWN_BYTES)
    var downBytes: Long = 0L                        //已下载大小

    @ColumnInfo(name = COLUMN_ONLY_WIFI)
    var onlyWifi: Boolean = false                   //是否只在 wifi 网络时下载

    @ColumnInfo(name = COLUMN_ERROR_CODE)
    var errorCode: Int = 0                          //下载失败的错误码

    @ColumnInfo(name = COLUMN_HEADER)
    var header: MutableMap<String, String>? = null  //http header

    @ColumnInfo(name = COLUMN_EXTRAS)
    var extras: String? = null                      //备用

    @Ignore
    internal var fileDir: String? = null            //指定文件保存目录

    internal constructor()

    internal constructor(builder: Builder) : this() {
        url = builder.url
        header = builder.header
        fileType = builder.fileType
        visible = builder.visible

        title = builder.title
        thumb = builder.thumb
        filePath = builder.filePath.orEmpty()
        fileDir = builder.fileDir
        onlyWifi = builder.onlyWifi
    }

    class Builder {
        internal var url: String = ""
        internal var header: MutableMap<String, String>? = null
        internal var fileType: FileType = FileType.UNKNOWN
        internal var visible: Boolean = true
        internal var title: String? = null
        internal var thumb: String? = null
        internal var filePath: String? = null
        internal var fileDir: String? = null
        internal var onlyWifi: Boolean = false

        fun url(url: String) = apply {
            this.url = url
        }

        fun header(header: MutableMap<String, String>) = apply {
            this.header = header
        }

        fun addHeader(key: String, value: String) = apply {
            if (header == null) {
                header = mutableMapOf()
            }
            header?.put(key, value)
        }

        fun visible(visible: Boolean) = apply {
            this.visible = visible
        }

        fun title(title: String?) = apply {
            this.title = title
        }

        fun thumb(thumb: String?) = apply {
            this.thumb = thumb
        }

        fun filePath(filePath: String) = apply {
            this.filePath = filePath
        }

        fun fileDir(fileDir: String) = apply {
            this.fileDir = fileDir
        }

        fun onlyWifi(downloadOnlyWifi: Boolean) = apply {
            this.onlyWifi = downloadOnlyWifi
        }

        fun build(): TaskInfo {
            fileType = PathUtil.getFileType(url)
            return TaskInfo(this)
        }
    }
}