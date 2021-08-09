package com.dhl.base.ui.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.viewbinding.ViewBinding
import com.dhl.base.R

/**
 *
 * Author: duanhaoliang
 * Create: 2021/7/1 13:42
 * Description:
 *
 */
abstract class ExpandableAdapter<PB : ViewBinding, CB : ViewBinding, P, C> : HeaderFooterAdapter<ViewBinding, P>() {

    private var childClickListener: ((view: View, posInAdapter: Int, data: C?) -> Unit)? = null

    fun setChildClickListener(listener: (view: View, posInAdapter: Int, data: C?) -> Unit) {
        childClickListener = listener
    }

    override fun getItemCount(): Int {
        var count = super.getItemCount()
        for (pos in 0 until getDataSize()) {
            if (isExpanded(pos)) {
                count += getChildCount(pos)
            }
        }
        return count
    }

    /**
     * view 类型
     * @param posInAdapter
     */
    override fun getItemType(posInAdapter: Int): Int {
        val pos = convertPosition(posInAdapter)
        return when {
            pos == Long.MIN_VALUE -> {
                TYPE_UNKNOWN
            }
            pos ushr 32 == 0L -> {
                TYPE_ITEM_PARENT
            }
            else -> {
                TYPE_ITEM_CHILD
            }
        }
    }

    /**
     * 坐标转换成数据集中的坐标, 通过返回值可以判断该位置的 ViewType， 也可以根据位置获取数据模型
     * 返回值 Long：
     *          低 32 位为在 data 中的位置，范围 [0, data.size - 1]， 高 32 位为子 item 在 parent 中的位置，范围 [1, data.size]
     *          高 32 位为 0 时是 parent 类型， 否则是 child 类型
     *
     * @param posInAdapter
     *
     */
    private fun convertPosition(posInAdapter: Int): Long {
        var expandedSize = 0
        for (pos in 0 until getDataSize()) {
            if (posInAdapter == expandedSize + pos) {
                return pos.toLong()
            }

            var childCount = 0
            if (isExpanded(pos)) {
                childCount = getChildCount(pos)
            }

            if (posInAdapter <= expandedSize + pos + childCount) {
                //child 在 parent 中的 pos
                var childPos = posInAdapter - expandedSize - pos - 1
                //+1 的目的是让高 32 位不为 0，可以根据高 32 位是否为 0 来判断改位置是 parent 还是 child
                childPos += 1
                return pos.toLong() or (childPos.toLong() shl 32)
            }

            expandedSize += childCount
        }
        return Long.MIN_VALUE
    }

    private fun getPositionInAdapter(posInData: Int): Int {
        var expandedSize = 0
        for (p in 0 until posInData) {
            if (isExpanded(p)) {
                expandedSize += getChildCount(p)
            }
        }
        return posInData + expandedSize
    }

    final override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewBinding> {
        return when (viewType) {
            TYPE_ITEM_PARENT -> onCreateParentViewHolder(parent, viewType)
            TYPE_ITEM_CHILD -> onCreateChildViewHolder(parent, viewType)
            else -> BindingViewHolder(ViewBinding { View(parent.context) })
        }
    }

    override fun onBindItemViewHolder(holder: BindingViewHolder<ViewBinding>, posInAdapter: Int) {
        val pos = convertPosition(posInAdapter)
        when {
            pos == Long.MIN_VALUE -> {
                //do nothing
            }
            pos ushr 32 == 0L -> {
                val data = getData(pos.toInt())
                onBindParentViewHolder(holder as BindingViewHolder<PB>, posInAdapter, data)
            }
            else -> {
                val childPos = (pos ushr 32) - 1
                val data = getChild(pos.toInt(), childPos.toInt())
                onBindChildViewHolder(holder as BindingViewHolder<CB>, posInAdapter, data)
            }
        }
    }

    /**
     * 折叠
     */
    fun collapse(@IntRange(from = 0) posInAdapter: Int) {
        val pos = convertPosition(posInAdapter)
        val posInData = pos.toInt()
        if (!isExpanded(posInData)) {
            return
        }
        val childCount = getChildCount(posInData)

        collapsePosition(posInData)
        if (childCount == 0) {
            notifyItemChanged(posInAdapter)
        } else {
            notifyItemRangeRemoved(posInAdapter + 1, childCount)
            notifyItemRangeChanged(posInAdapter, itemCount - posInAdapter)
        }
    }

    /**
     * 展开
     */
    fun expand(@IntRange(from = 0) posInAdapter: Int) {
        val pos = convertPosition(posInAdapter)
        val posInData = pos.toInt()
        if (isExpanded(posInData)) {
            return
        }
        val childCount = getChildCount(posInData)

        expandPosition(posInData)
        if (childCount == 0) {
            notifyItemChanged(posInAdapter)
        } else {
            notifyItemRangeInserted(posInAdapter + 1, childCount)
            notifyItemRangeChanged(posInAdapter, itemCount - posInAdapter)
        }
    }

    /**
     * 展开并折叠其它位置
     */
    fun expandAndCollapseOther(@IntRange(from = 0) posInAdapter: Int) {
        val pos = convertPosition(posInAdapter)
        val posInData = pos.toInt()
        expand(posInAdapter)

        for (p in 0 until getDataSize()) {
            if (p == posInData) continue
            collapse(getPositionInAdapter(p))
        }
    }


    protected fun bindChildClick(view: View, posInAdapter: Int, data: C?) {
        if (!view.hasOnClickListeners()) {
            view.setOnClickListener { target ->
                val posInAdapter = target.getTag(R.id.tag_pos) as? Int ?: return@setOnClickListener
                val data = target.getTag(R.id.tag_data) as? C
                childClickListener?.invoke(target, posInAdapter, data)
            }
        }
        view.setTag(R.id.tag_pos, posInAdapter)
        view.setTag(R.id.tag_data, data)
    }

    /**
     * 折叠指定位置，只修改折叠标记
     */
    protected abstract fun collapsePosition(posInData: Int)

    /**
     * 展开指定位置，只修改折叠标记
     */
    protected abstract fun expandPosition(posInData: Int)

    /**
     * 该位置是否是展开的
     * @param posInData data 中的位置 [0, data.size - 1]
     */
    protected abstract fun isExpanded(posInData: Int): Boolean

    /**
     * 展开后子 item 个数
     * @param posInData data 中的位置 [0, data.size - 1]
     */
    protected abstract fun getChildCount(posInData: Int): Int

    /**
     * 获取子 item
     *
     * @param posInData data 中的位置 [0 , data.size - 1]
     * @param posInParent 子 item 在 parent 中的位置 [0, size - 1]
     */
    protected abstract fun getChild(posInData: Int, posInParent: Int): C?


    abstract fun onCreateParentViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<PB>
    abstract fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<CB>
    abstract fun onBindParentViewHolder(holder: BindingViewHolder<PB>, posInAdapter: Int, data: P?)
    abstract fun onBindChildViewHolder(holder: BindingViewHolder<CB>, posInAdapter: Int, data: C?)
}