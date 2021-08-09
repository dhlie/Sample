package cn.dhl.sample.adaptertest

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.dhl.sample.databinding.ActivityAdapterTestBinding
import com.dhl.base.dp
import com.dhl.base.ui.recyclerview.BaseLoadMoreView
import com.dhl.base.ui.recyclerview.CommonLoadMoreView
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

        initRecyclerView()

        binding.rvList.adapter = adapter
        adapter.changeData(getDataList())

        binding.btn1.apply {
            text = "Insert item"
            setOnClickListener {
                ItemInfo().apply {
                    color = Random.nextInt(0xffffff) or 0xff000000.toInt()
                    text = "Position:"
                    height = Random.nextInt(200) + 300

                    val pos = if (adapter.getDataSize() == 0) 0 else Random.nextInt(adapter.getDataSize())
                    adapter.addData(this)
                    toast("insert:$pos count:1")
                }
            }
        }
        binding.btn2.apply {
            text = "Insert items"
            setOnClickListener {
                val list = mutableListOf<ItemInfo>()
                ItemInfo().apply {
                    color = Random.nextInt(0xffffff) or 0xff000000.toInt()
                    text = "Position:"
                    height = Random.nextInt(200) + 300

                    list.add(this)
                }

                ItemInfo().apply {
                    color = Random.nextInt(0xffffff) or 0xff000000.toInt()
                    text = "Position:"
                    height = Random.nextInt(200) + 300

                    list.add(this)
                }


                val pos = if (adapter.getDataSize() == 0) 0 else Random.nextInt(adapter.getDataSize())
                adapter.addData(list, pos)
                toast("insert:$pos count:${list.size}")
            }
        }
        binding.btn3.apply {
            text = "Remove"
            setOnClickListener {
                val pos = if (adapter.getDataSize() == 0) 0 else Random.nextInt(adapter.getDataSize())
                adapter.removeData(pos)
                toast("remove:$pos")
            }
        }

        binding.btn4.apply {
            text = "set data"
            setOnClickListener {
                adapter.changeData(getDataList())
            }
        }

        binding.btn5.apply {
            text = "add header"
            setOnClickListener {
                val header = TextView(this@AdapterTestActivity).apply {
                    text = "Header"
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
                adapter.addHeaderView(header)
            }
        }

        binding.btn6.apply {
            text = "remove header"
            setOnClickListener {
                adapter.removeAllHeaderView()
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvList.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        adapter = AdapterTestAdapter().apply {
            loadMoreView = CommonLoadMoreView(this@AdapterTestActivity).apply {
                setLoadMoreCallback { loadMore() }
            }
        }

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
            val padding = 12.dp
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
            val padding = 12.dp
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
        adapter.setClickListener { view, pos, data ->
            toast("click pos:$pos")
        }
    }

    private var count = 0
    private fun getDataList(): List<ItemInfo> {
        val list = mutableListOf<ItemInfo>()
        for (i in 0 until 5) {
            count++
            ItemInfo().apply {
                color = Random.nextInt(0xffffff) or 0xff000000.toInt()
                text = "Position:"
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
            adapter.finishLoadingMore()

            if (count >= 20) {
                adapter.setLoadMoreEnable(false)
            }
        }, 1000)
    }
}