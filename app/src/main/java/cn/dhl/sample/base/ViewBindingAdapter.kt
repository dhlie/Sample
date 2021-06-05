package cn.dhl.sample.base

import android.os.Build
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 *
 * Author: duanhaoliang
 * Create: 2021/6/3 10:28
 * Description:
 *
 */

data class TagHolder<K>(val pos: Int, val data: K?)

open class BindingViewHolder<out T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

abstract class ViewBindingAdapter<T : ViewBinding, K> : RecyclerView.Adapter<BindingViewHolder<T>>() {

    protected var data: List<K>? = null
    private var clickListener: ((view: View, pos: Int, data: K?) -> Unit)? = null
    private var longClickListener: ((view: View, pos: Int, data: K?) -> Unit)? = null

    override fun getItemCount(): Int {
        var count = getDataSize()
        if (hasHeaderView()) {
            count++
        }
        if (hasFooterView()) {
            count++
        }
        if (hasLoadMoreView()) {
            count++
        }
        return count
    }

    fun getDataSize(): Int {
        return data?.size ?: 0
    }

    protected fun bindClick(view: View, pos: Int, data: K?) {
        if (!view.hasOnClickListeners()) {
            view.setOnClickListener { target ->
                val tag = target.tag as? TagHolder<K> ?: return@setOnClickListener
                clickListener?.invoke(target, tag.pos, tag.data)
            }
        }
        view.tag = TagHolder(pos, data)
    }

    protected fun bindLongClick(view: View, pos: Int, data: K?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || !view.hasOnLongClickListeners()) {
            view.setOnLongClickListener { target ->
                val tag = target.tag as? TagHolder<K> ?: return@setOnLongClickListener false
                longClickListener?.invoke(target, tag.pos, tag.data)
                true
            }
        }
        view.tag = TagHolder(pos, data)
    }

    fun setClickListener(listener: (view: View, pos: Int, data: K?) -> Unit) {
        clickListener = listener
    }

    fun setLongClickListener(listener: (view: View, pos: Int, data: K?) -> Unit) {
        longClickListener = listener
    }

    open fun hasHeaderView(): Boolean = false

    open fun hasFooterView(): Boolean = false

    open fun hasLoadMoreView(): Boolean = false

    fun changeData(data: List<K>?) {
        this.data = data
        notifyDataSetChanged()
    }

    /**
     * 添加 item
     *
     * @param index 添加的位置, 范围: [0, getDataSize()]
     *
     */
    open fun addData(item: K, index: Int = getDataSize()) {
        if (index < 0 || index > getDataSize()) {
            Log.e("ViewBindingAdapter", "param index is invalid, range:[0, ${getDataSize()}]")
            return
        }

        if (data == null) {
            data = mutableListOf()
        }

        val mutableList = data as? MutableList ?: throw RuntimeException("data is not mutable, class:${data?.javaClass}")
        var startInsertPos = index
        if (hasHeaderView()) {
            startInsertPos++
        }

        mutableList.add(index, item)
        notifyItemInserted(startInsertPos)

        val changeStartPos = startInsertPos + 1
        val changeCount = mutableList.size - (index + 1)
        if (changeCount > 0) {
            notifyItemRangeChanged(changeStartPos, changeCount)
        }
    }

    /**
     * 添加 list
     *
     * @param index 添加的位置, 范围: [0, getDataSize()]
     */
    open fun addData(list: List<K>?, index: Int = getDataSize()) {
        if (list.isNullOrEmpty()) {
            return
        }

        if (index < 0 || index > getDataSize()) {
            Log.e("ViewBindingAdapter", "param index is invalid, range:[0, ${getDataSize()}]")
            return
        }

        if (data == null) {
            data = mutableListOf()
        }
        val mutableList = data as? MutableList ?: throw RuntimeException("data is not mutable, class:${data?.javaClass}")
        var startInsertPos = index
        if (hasHeaderView()) {
            startInsertPos++
        }

        mutableList.addAll(index, list)
        notifyItemRangeInserted(startInsertPos, list.size)

        val changeStartPos = startInsertPos + list.size
        val changeCount = mutableList.size - (index + list.size)
        if (changeCount > 0) {
            notifyItemRangeChanged(changeStartPos, changeCount)
        }
    }

    /**
     * 移除 item
     */
    fun remove(item: K) {
        val index = data?.indexOf(item) ?: -1
        if (index == -1) {
            return
        }
        removeData(index)
    }

    /**
     * 移除指定位置的 item
     *
     * @param index 移除的位置, 范围: [0, getDataSize()]
     */
    fun removeData(index: Int) {
        if (index < 0 || index >= getDataSize()) {
            Log.e("ViewBindingAdapter", "param index is invalid, range:[0, ${getDataSize()}]")
            return
        }

        val mutableList = data as? MutableList ?: throw RuntimeException("data is not mutable, class:${data?.javaClass}")
        var startRemovePos = index
        if (hasHeaderView()) {
            startRemovePos++
        }

        mutableList.removeAt(index)
        notifyItemRemoved(startRemovePos)

        val changeStartPos = startRemovePos
        val changeCount = mutableList.size - index
        if (changeCount > 0) {
            notifyItemRangeChanged(changeStartPos, changeCount)
        }
    }

}