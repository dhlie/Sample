package cn.dhl.sample.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import cn.dhl.sample.R

/**
 *
 * Author: duanhl
 * Create: 6/3/21 11:24 PM
 * Description:
 *
 */
class CommonLoadMoreView : BaseLoadMoreView {

    private lateinit var pbLoading: ProgressBar
    private lateinit var tvText: TextView

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.load_more_layout, this)
        pbLoading = findViewById(R.id.pbLoading)
        tvText = findViewById(R.id.tvText)

        pbLoading.visibility = View.INVISIBLE
        tvText.visibility = View.INVISIBLE
    }

    override fun setLoadEnable(enable: Boolean) {
        super.setLoadEnable(enable)

        pbLoading.visibility = View.INVISIBLE
        tvText.visibility = View.VISIBLE
        tvText.text = "没有更多"
    }

    override fun onLoadingStart() {
        pbLoading.visibility = View.VISIBLE
        tvText.visibility = View.INVISIBLE
    }

    override fun onLoadingFinish() {
        pbLoading.visibility = View.INVISIBLE
        tvText.visibility = View.INVISIBLE
    }

}