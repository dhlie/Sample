package cn.dhl.sample.base

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

open class BindingViewHolder<out T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

abstract class ViewBindingAdapter<T : ViewBinding, K> : RecyclerView.Adapter<BindingViewHolder<T>>() {

    protected var data: List<K>? = null
    private var clickListener: ((view: View, data: K) -> Unit)? = null

    fun changeData(data: List<K>?) {
        this.data = data
        notifyDataSetChanged()
    }

    open fun addData(list: List<K>?, index: Int = data?.size ?: 0) {
        if (list.isNullOrEmpty()) {
            return
        }
        val oldCount = data?.size ?: 0

        if (index < 0 || index > oldCount) {
            throw IndexOutOfBoundsException("index:$index, size:$oldCount")
        }

        if (data == null) {
            data = mutableListOf()
        }
        val mutableList = data as? MutableList ?: return
        var startInsertPos = index
        if (hasHeaderView()) {
            startInsertPos++
        }

        mutableList.addAll(index, list)
        notifyItemRangeInserted(startInsertPos, list.size)

        val changeStartPos = startInsertPos + list.size
        val changeCount = itemCount - changeStartPos
        if (changeCount > 0) {
            notifyItemRangeChanged(changeStartPos, changeCount)
        }
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    protected fun bindClick(view: View, data: K) {
        if (!view.hasOnClickListeners()) {
            view.setOnClickListener { view ->
                val tag = view.tag as? K ?: return@setOnClickListener
                clickListener?.invoke(view, tag)
            }
        }
        view.tag = data
    }

    fun setClickListener(listener: (view: View, data: K) -> Unit) {
        this.clickListener = listener
    }

    open fun hasHeaderView(): Boolean = false

    open fun hasFooterView(): Boolean = false

    open fun hasLoadMoreView(): Boolean = false

}