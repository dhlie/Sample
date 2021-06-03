package cn.dhl.sample.base

import android.view.View
import android.view.ViewGroup
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

abstract class HeaderFooterAdapter<T: ViewBinding, K>(val orientation: Int = LinearLayout.VERTICAL) : ViewBindingAdapter<ViewBinding, K>() {

    companion object {
        const val TYPE_HEADER = 10001
        const val TYPE_ITEM = 10002
        const val TYPE_FOOTER = 10003
    }

    private var headerLayout: LinearLayout? = null
    private var footerLayout: LinearLayout? = null

    override fun getItemCount(): Int {
        var count = dataList?.size ?: 0
        if (hasHeaderView()) {
            count++
        }
        if (hasFooterView()) {
            count++
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        val hasHeader = hasHeaderView()
        val hasFooter = hasFooterView()
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
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewBinding> {
        return when (viewType) {
            TYPE_HEADER -> HeaderFooterViewHolder(headerLayout!!)
            TYPE_FOOTER -> HeaderFooterViewHolder(footerLayout!!)
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

    abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<T>
    abstract fun onBindItemViewHolder(holder: BindingViewHolder<T>, position: Int)

    private fun hasHeaderView(): Boolean {
        val count = headerLayout?.childCount ?: 0
        return count > 0
    }

    private fun hasFooterView(): Boolean {
        val count = footerLayout?.childCount ?:0
        return count > 0
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
            }
        }
    }

    fun removeHeaderView(header: View) {
        headerLayout?.run {
            removeView(header)
            if (childCount == 0) {
                notifyItemRemoved(0)
            }
        }
    }

    fun removeAllHeaderView() {
        headerLayout?.run {
            removeAllViews()
            notifyItemRemoved(0)
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
            removeView(footer)
            if (childCount == 0) {
                notifyItemRemoved(getFooterViewPosition())
            }
        }
    }

    fun removeAllFooterView() {
        footerLayout?.run {
            removeAllViews()
            notifyItemRemoved(getFooterViewPosition())
        }
    }

    private fun getFooterViewPosition(): Int {
        var pos = dataList?.size ?: 0
        if (hasHeaderView()) {
            pos++
        }
        return pos
    }
}