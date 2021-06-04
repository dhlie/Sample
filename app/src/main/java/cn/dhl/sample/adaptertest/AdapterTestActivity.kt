package cn.dhl.sample.adaptertest

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.dhl.sample.base.BaseLoadMoreView
import cn.dhl.sample.base.CommonLoadMoreView
import cn.dhl.sample.databinding.ActivityAdapterTestBinding
import cn.dhl.sample.dp
import kotlin.random.Random

/**
 *
 * Author: duanhl
 * Create: 6/3/21 8:50 PM
 * Description:
 *
 */
class AdapterTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdapterTestBinding
    private lateinit var adapter: AdapterTestAdapter
    private var loadMoreView: BaseLoadMoreView? = null

    private var toast: Toast? = null
    private fun toast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdapterTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvList.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        adapter = AdapterTestAdapter()

        val header1 = TextView(this).apply {
            text = "Header 1"
            gravity = Gravity.CENTER
            textSize = 20f
            val padding = 12.dp
            setPadding(padding, padding, padding, padding)
            setTextColor(0xff000000.toInt())
            setBackgroundColor(0xffffff00.toInt())
            setOnClickListener {
                toast(text.toString())
            }
        }
        val header2 = TextView(this).apply {
            text = "Header 2"
            gravity = Gravity.CENTER
            textSize = 20f
            val padding = 12.dp
            setPadding(padding, padding, padding, padding)
            setTextColor(0xff000000.toInt())
            setBackgroundColor(0xffff00ff.toInt())
            setOnClickListener {
                toast(text.toString())
            }
        }
        adapter.addHeaderView(header1)
        adapter.addHeaderView(header2)

        val footer1 = TextView(this).apply {
            text = "Footer 1"
            gravity = Gravity.CENTER
            textSize = 20f
            val padding = 22.dp
            setPadding(padding, padding, padding, padding)
            setTextColor(0xff000000.toInt())
            setBackgroundColor(0xffff00ff.toInt())
            setOnClickListener {
                toast(text.toString())
            }
        }
        val footer2 = TextView(this).apply {
            text = "Footer 2"
            gravity = Gravity.CENTER
            textSize = 20f
            val padding = 22.dp
            setPadding(padding, padding, padding, padding)
            setTextColor(0xff000000.toInt())
            setBackgroundColor(0xffff00ff.toInt())
            setOnClickListener {
                toast(text.toString())
            }
        }
        adapter.addFooterView(footer1)
        adapter.addFooterView(footer2)
        loadMoreView = CommonLoadMoreView(this).apply {
            setLoadMoreCallback { loadMore() }
        }
        adapter.loadMoreView = loadMoreView
        adapter.setClickListener { view, data ->
            toast(data.text)
        }

        binding.rvList.adapter = adapter
        adapter.changeData(getDataList())

    }

    private var count = 0
    private fun getDataList(): List<ItemInfo> {
        val list = mutableListOf<ItemInfo>()
        for (i in 0 until 5) {
            ItemInfo().apply {
                color = Random.nextInt(0xffffff) or 0xff000000.toInt()
                text = "Position: ${count++}"
                height = Random.nextInt(200) + 300
                list.add(this)
            }
        }
        return list
    }

    private fun loadMore() {
        binding.root.postDelayed({
            val data = getDataList()
            adapter.addData(data)
            loadMoreView?.finishLoading()
        }, 1000)
    }
}