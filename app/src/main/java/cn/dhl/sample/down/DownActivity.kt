package cn.dhl.sample.down

import android.os.Bundle
import cn.dhl.sample.databinding.ActivityDownBinding
import com.dhl.base.downloader.HttpUtils
import com.dhl.base.ui.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/10 13:41
 * Description:
 *
 */
class DownActivity : BaseActivity() {

    lateinit var binding: ActivityDownBinding
    private val maniScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn1.apply {
            text = "download"
            setOnClickListener {
                maniScope.launch(Dispatchers.IO) {
                    HttpUtils.download("https://download.jetbrains.com.cn/python/pycharm-community-2021.1.1.exe")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        maniScope.cancel()
    }
}