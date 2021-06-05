package cn.dhl.sample.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 *
 * Author: duanhaoliang
 * Create: 2021/6/3 10:28
 * Description:
 *
 */

private class HeaderFooterViewHolder(view: View) : BindingViewHolder<ViewBinding>(ViewBinding { view })

abstract class BaseLoadMoreView : FrameLayout {

    companion object {
        const val STATE_IDLE = 0
        const val STATE_LOADING = 1
    }

    private var loadEnable: Boolean = true
    private var state: Int = STATE_IDLE
    private var loadMoreCallback: (() -> Unit)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    abstract fun onLoadingStart()
    abstract fun onLoadingFinish()

    open fun setLoadEnable(enable: Boolean) {
        loadEnable = enable
    }

    fun setLoadMoreCallback(callback: (() -> Unit)?) {
        loadMoreCallback = callback
    }

    fun startLoading() {
        if (!loadEnable) {
            return
        }
        if (state == STATE_LOADING) {
            return
        }
        state = STATE_LOADING
        onLoadingStart()
        loadMoreCallback?.invoke()
    }

    fun finishLoading() {
        if (!loadEnable) {
            return
        }
        state = STATE_IDLE
        onLoadingFinish()
    }
}

abstract class HeaderFooterAdapter<T: ViewBinding, K>(private val orientation: Int = LinearLayout.VERTICAL) : ViewBindingAdapter<ViewBinding, K>() {

    companion object {
        const val TYPE_HEADER = 10001
        const val TYPE_ITEM = 10002
        const val TYPE_FOOTER = 10003
        const val TYPE_LOAD_MORE = 10004
    }

    private var headerLayout: LinearLayout? = null
    private var footerLayout: LinearLayout? = null
    var loadMoreView: BaseLoadMoreView? = null
        set(value) {
            field = value?.apply {
                layoutParams = if (orientation == LinearLayout.VERTICAL) {
                    RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                } else {
                    RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            }
            notifyItemChanged(getFooterViewPosition())
        }

    override fun getItemViewType(position: Int): Int {
        val hasHeader = hasHeaderView()
        val hasFooter = hasFooterView()
        val hasLoadMore = hasLoadMoreView()

        val itemCount = itemCount
        if (!hasLoadMore) {
            return if (!hasHeader && !hasFooter) {
                TYPE_ITEM
            } else if (hasHeader && !hasFooter) {
                if (position == 0) TYPE_HEADER else TYPE_ITEM
            } else if (!hasHeader && hasFooter) {
                if (position == itemCount - 1) TYPE_FOOTER else TYPE_ITEM
            } else {
                when (position) {
                    0 -> TYPE_HEADER
                    itemCount - 1 -> TYPE_FOOTER
                    else -> TYPE_ITEM
                }
            }
        } else {
            return if (!hasHeader && !hasFooter) {
                if (position == itemCount - 1) TYPE_LOAD_MORE else TYPE_ITEM
            } else if (hasHeader && !hasFooter) {
                when (position) {
                    0 -> {
                        TYPE_HEADER
                    }
                    itemCount - 1 -> {
                        TYPE_LOAD_MORE
                    }
                    else -> {
                        TYPE_ITEM
                    }
                }
            } else if (!hasHeader && hasFooter) {
                when (position) {
                    itemCount - 1 -> TYPE_LOAD_MORE
                    itemCount - 2 -> TYPE_FOOTER
                    else -> TYPE_ITEM
                }
            } else {
                when (position) {
                    0 -> TYPE_HEADER
                    itemCount - 1 -> TYPE_LOAD_MORE
                    itemCount - 2 -> TYPE_FOOTER
                    else -> TYPE_ITEM
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewBinding> {
        return when (viewType) {
            TYPE_HEADER -> HeaderFooterViewHolder(headerLayout!!).apply {
                (headerLayout?.parent as? ViewGroup)?.run { removeView(headerLayout) }
            }
            TYPE_FOOTER -> HeaderFooterViewHolder(footerLayout!!).apply {
                (footerLayout?.parent as? ViewGroup)?.run { removeView(footerLayout) }
            }
            TYPE_LOAD_MORE -> HeaderFooterViewHolder(loadMoreView!!).apply {
                (loadMoreView?.parent as? ViewGroup)?.run { removeView(loadMoreView) }
            }
            TYPE_ITEM -> onCreateItemViewHolder(parent, viewType)
            else -> BindingViewHolder(ViewBinding { View(parent.context) })
        }
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewBinding>, position: Int) {
        if (holder is HeaderFooterViewHolder) {
            return
        }
        var realPos = position
        if (hasHeaderView()) {
            realPos--
        }
        onBindItemViewHolder(holder as BindingViewHolder<T>, realPos)
    }

    override fun onViewAttachedToWindow(holder: BindingViewHolder<ViewBinding>) {
        super.onViewAttachedToWindow(holder)
        if (holder is HeaderFooterViewHolder && holder.binding.root == loadMoreView) {
            loadMoreView?.startLoading()
        }
    }

    abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<T>
    abstract fun onBindItemViewHolder(holder: BindingViewHolder<T>, position: Int)

    override fun hasHeaderView(): Boolean {
        val count = headerLayout?.childCount ?: 0
        return count > 0
    }

    override fun hasFooterView(): Boolean {
        val count = footerLayout?.childCount ?: 0
        return count > 0
    }

    override fun hasLoadMoreView(): Boolean {
        return loadMoreView != null
    }

    fun addHeaderView(view: View, index: Int = -1) {
        if (headerLayout == null) {
            headerLayout = LinearLayout(view.context).apply {
                orientation = this@HeaderFooterAdapter.orientation
                layoutParams = if (orientation == LinearLayout.VERTICAL) {
                    RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                } else {
                    RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            }
        }

        headerLayout?.run {
            if (index > childCount) {
                addView(view, childCount)
            } else {
                addView(view, index)
            }
            if (childCount == 1) {
                notifyItemInserted(0)
                val count = getDataSize()
                if (count > 0) {
                    notifyItemRangeChanged(0, count)
                }
            }
        }
    }

    fun removeHeaderView(header: View) {
        headerLayout?.run {
            if (childCount == 0) {
                return@run
            }
            removeView(header)
            if (childCount == 0) {
                notifyItemRemoved(0)
                val count = getDataSize()
                if (count > 0) {
                    notifyItemRangeChanged(0, count)
                }
            }
        }
    }

    fun removeAllHeaderView() {
        headerLayout?.run {
            if (childCount == 0) {
                return@run
            }
            removeAllViews()
            notifyItemRemoved(0)
            val count = getDataSize()
            if (count > 0) {
                notifyItemRangeChanged(0, count)
            }
        }
    }

    fun addFooterView(view: View, index: Int = -1) {
        if (footerLayout == null) {
            footerLayout = LinearLayout(view.context).apply {
                orientation = this@HeaderFooterAdapter.orientation
                layoutParams = if (orientation == LinearLayout.VERTICAL) {
                    RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                } else {
                    RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            }
        }

        footerLayout?.run {
            if (index > childCount) {
                addView(view, childCount)
            } else {
                addView(view, index)
            }
            if (childCount == 1) {
                notifyItemInserted(getFooterViewPosition())
            }
        }
    }

    fun removeFooterView(footer: View) {
        footerLayout?.run {
            if (childCount == 0) {
                return@run
            }
            removeView(footer)
            if (childCount == 0) {
                notifyItemRemoved(getFooterViewPosition())
            }
        }
    }

    fun removeAllFooterView() {
        footerLayout?.run {
            if (childCount == 0) {
                return@run
            }
            removeAllViews()
            notifyItemRemoved(getFooterViewPosition())
        }
    }

    private fun getFooterViewPosition(): Int {
        var pos = data?.size ?: 0
        if (hasHeaderView()) {
            pos++
        }
        return pos
    }
}