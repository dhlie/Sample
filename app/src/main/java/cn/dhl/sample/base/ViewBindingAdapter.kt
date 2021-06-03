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

    open fun addData(data: List<K>?) {
        if (data.isNullOrEmpty()) {
            return
        }
        val startPos = this.data?.size ?: 0
        val mutableList = this.data as? MutableList ?: return
        mutableList.addAll(data)
        notifyItemRangeInserted(startPos, data.size)
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

}