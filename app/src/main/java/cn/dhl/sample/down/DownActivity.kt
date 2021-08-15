package cn.dhl.sample.down

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.dhl.sample.App
import cn.dhl.sample.R
import cn.dhl.sample.base.ui.CommonDialog
import cn.dhl.sample.databinding.ActivityDownBinding
import cn.dhl.sample.databinding.ActivityDownItemBinding
import com.dhl.base.downloader.CommonDownloadListener
import com.dhl.base.downloader.DownloadManager
import com.dhl.base.downloader.TaskInfo
import com.dhl.base.downloader.db.DownloadDatabase
import com.dhl.base.downloader.db.QueryConst
import com.dhl.base.ui.BaseActivity
import com.dhl.base.ui.recyclerview.BindingViewHolder
import com.dhl.base.ui.recyclerview.ViewBindingAdapter
import com.dhl.base.utils.FileUtil
import com.dhl.base.utils.ToastUtil
import kotlinx.coroutines.*

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 13:41
 * Description:
 *
 * todo: wifi
 *
 */
class DownActivity : BaseActivity() {

    companion object {
        fun getProgressText(taskInfo: TaskInfo): String {
            return String.format("%s %d%%", FileUtil.convertFileSize(taskInfo.totalBytes), if (taskInfo.totalBytes <= 0) 0 else taskInfo.downBytes * 100 / taskInfo.totalBytes)
        }
    }

    private lateinit var binding: ActivityDownBinding
    private val maniScope = MainScope()
    private val adapter = DownloadListAdapter().apply {
        setClickListener { view, pos, taskInfo ->
            taskInfo ?: return@setClickListener
            when (taskInfo.status) {
                TaskInfo.TaskStatus.PENDING -> {
                }
                TaskInfo.TaskStatus.RUNNING -> {
                    DownloadManager.instance.pause(taskInfo)
                }
                TaskInfo.TaskStatus.PAUSED -> {
                    DownloadManager.instance.resume(taskInfo)
                }
                TaskInfo.TaskStatus.FINISH -> {
                }
                TaskInfo.TaskStatus.ERROR -> {
                    DownloadManager.instance.resume(taskInfo)
                }
                TaskInfo.TaskStatus.DELETING_RECORD, TaskInfo.TaskStatus.DELETING_WITH_FILE -> {
                }
            }
        }
        setLongClickListener { view, pos, taskInfo ->
            taskInfo ?: return@setLongClickListener
            CommonDialog.showTipsDialog(this@DownActivity, content = "删除该任务和文件?", positiveClickListener = View.OnClickListener {
                DownloadManager.instance.delete(taskInfo, true)
            })
        }
    }

    private val downloadListener = object : CommonDownloadListener() {
        override fun onStatusChanged(taskInfo: TaskInfo) {
            taskStatusChanged()
        }

        override fun onProgressChanged(taskInfo: TaskInfo, downBytes: Long, totalBytes: Long) {
            for (index in 0 until binding.rvList.childCount) {
                val view = binding.rvList.getChildAt(index)
                val info = view.tag as? TaskInfo ?: return
                if (taskInfo.id == info.id) {
                    val holder = binding.rvList.getChildViewHolder(view) as BindingViewHolder<*>
                    val itemBinding = holder.binding as ActivityDownItemBinding
                    itemBinding.tvProgress.text = getProgressText(taskInfo)
                    break
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etUrl.setText("http://192.168.43.209:8080/examples/1.dmg")
        binding.btnDown.apply {
            text = "Download"
            setOnClickListener {
                val url = binding.etUrl.text.toString()
                if (url.isBlank()) {
                    ToastUtil.showToast("请输入下载链接")
                    return@setOnClickListener
                }
                val taskInfo: TaskInfo = TaskInfo.Builder()
                    .url(url)
                    .addHeader("token", "token")
                    .addHeader("header", "value")
                    .build()
                DownloadManager.instance.download(taskInfo)
            }
        }
        binding.btnClear.apply {
            text = "Clear"
            setOnClickListener {
                binding.etUrl.setText("")
            }
        }

        binding.rvList.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        binding.rvList.adapter = adapter

        DownloadManager.instance.registerListener(downloadListener)

        taskStatusChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        maniScope.cancel()
        DownloadManager.instance.unregisterListener(downloadListener)
    }

    private fun taskStatusChanged() {
        maniScope.launch(Dispatchers.IO) {
            val tasks = DownloadDatabase.DAO.queryStatusTasks(QueryConst.allStatus)
            withContext(Dispatchers.Main) {
                adapter.changeData(tasks)
            }
        }
    }
}

private class DownloadListAdapter : ViewBindingAdapter<ActivityDownItemBinding, TaskInfo>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ActivityDownItemBinding> {
        val binding = ActivityDownItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ActivityDownItemBinding>, position: Int) {
        val taskInfo = getData(position) ?: return
        val binding = holder.binding

        binding.tvTitle.text = taskInfo.title
        binding.tvUrl.text = taskInfo.url
        binding.tvProgress.text = DownActivity.getProgressText(taskInfo)
        when (taskInfo.status) {
            TaskInfo.TaskStatus.PENDING -> {
                binding.tvStatus.text = "等待中"
                binding.tvStatus.setTextColor(App.instance.getColor(R.color.color_grey))
            }
            TaskInfo.TaskStatus.RUNNING -> {
                binding.tvStatus.text = "下载中"
                binding.tvStatus.setTextColor(App.instance.getColor(R.color.colorPrimary))
            }
            TaskInfo.TaskStatus.PAUSED -> {
                binding.tvStatus.text = "暂停中"
                binding.tvStatus.setTextColor(App.instance.getColor(R.color.design_default_color_secondary))
            }
            TaskInfo.TaskStatus.FINISH -> {
                binding.tvStatus.text = "下载完成"
                binding.tvStatus.setTextColor(0x9900ff00.toInt())
            }
            TaskInfo.TaskStatus.ERROR -> {
                binding.tvStatus.text = "下载失败 ${taskInfo.errorCode}"
                binding.tvStatus.setTextColor(App.instance.getColor(R.color.design_default_color_error))
            }
            TaskInfo.TaskStatus.DELETING_RECORD, TaskInfo.TaskStatus.DELETING_WITH_FILE -> {
                binding.tvStatus.text = "删除"
                binding.tvStatus.setTextColor(App.instance.getColor(R.color.design_default_color_secondary_variant))
            }
        }

        binding.root.tag = taskInfo

        bindClick(binding.root, position, taskInfo)
        bindLongClick(binding.root, position, taskInfo)
    }

}