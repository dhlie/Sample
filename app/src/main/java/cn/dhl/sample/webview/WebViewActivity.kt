package cn.dhl.sample.webview

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import cn.dhl.sample.databinding.ActivityWebviewBinding
import com.dhl.base.AppExecutors
import com.dhl.base.ui.BaseActivity
import com.dhl.base.utils.Gsons
import com.dhl.base.utils.ToastUtil


/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/25 15:56
 * Description:
 *
 */
class WebViewActivity : BaseActivity() {

    private lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null) //bug:webview有内容但是不显示,滑动一下才显示
        initWebView()

        Looper.myQueue().addIdleHandler {
            loadUrl()
            false
        }

        binding.btnJs.apply {
            text = "call js"
            setOnClickListener {
                binding.webView.evaluateJavascript("javascript:appendItem('append by app')") {
                    ToastUtil.showToast("${Thread.currentThread().name} thread: $it")
                }
            }
        }
        binding.btnJsWithCallback.apply {
            text = "call js cb"
            setOnClickListener {

            }
        }
    }

    private fun loadUrl() {
        //方式1:加载网络html
        //binding.webView.loadUrl("http://www.zhangyue.com/products/iReader");
        //binding.webView.loadUrl("https://www.taobao.com/")
        //binding.webView.loadUrl("https://sit-uc-m.fadada.com/transfer/test")
        //binding.webView.loadUrl("http://www.runoob.com/try/demo_source/tryhtml5_html_manifest.htm")
        //方式2:加载本地html
        binding.webView.loadUrl("file:///android_asset/test.html");
        //方式3：加载手机本地的html页面(不能关闭文件获取功能,否则无法加载 setAllowFileAccess(true))
        //binding.webView.loadUrl("file:///mnt/sdcard/c51.html");
        //方式3:加载 HTML 页面的一小段内容
        //binding.webView.loadData(html, "text/html; charset=UTF-8", null);
    }

    private fun initWebView() {
        val settings = binding.webView.settings ?: return
        settings.javaScriptEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.allowFileAccess = false
        settings.domStorageEnabled = true

        //设置跨域cookie读取
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webView, true)
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        binding.webView.addJavascriptInterface(JSB(this), "fddApp")

        //binding.webView.registerHandler("showTips", WVJBHandler<Any?, Any?> { data, callback ->
        //    log { "wvjsblog Java Echo called with: " + data.toString() }
        //    callback?.onResult("data222")
        //})
        //
        //binding.webView.callHandler("showTips", null,
        //    WVJBResponseCallback<Any?> { data ->
        //        log { "wvjsblog Java received response: $data" }
        //    }
        //)
    }

    data class JSParams(val module: String, val action: String, val params: String?, val callbackId: String?)

    inner class JSB(val context: Context) {

        @JavascriptInterface
        fun jsExec(data: String) {
            val jsParams = Gsons.fromJson(data, JSParams::class.java) ?: return
            ToastUtil.showToast("module:${jsParams.module}, action:${jsParams.action}, params:${jsParams.params}, cid:${jsParams.callbackId}")


            jsParams.callbackId?.run {
                val jsCode = String.format("window.JSBridge.onAppCall('%s', '%s')", jsParams.callbackId, "pn ${context.packageName}")
                AppExecutors.executeOnDiskIO {
                    AppExecutors.executeOnMain {
                        binding.webView.evaluateJavascript(jsCode, null)
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        //避免 webview 内存泄漏: 1清空页面内容 2从view数中移除 3调用destroy()方法
        binding.webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null) //清除页面内容
        if (binding.webView.parent != null) {
            (binding.webView.parent as ViewGroup).removeViewInLayout(binding.webView)
        }
        binding.webView.destroy() //doc: this method should be called after this WebView has been removed from the view system.
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}