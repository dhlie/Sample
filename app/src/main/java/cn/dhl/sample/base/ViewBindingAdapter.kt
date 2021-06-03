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

abstract class ViewBindingAdapter<T: ViewBinding, K> : RecyclerView.Adapter<BindingViewHolder<T>>() {

    protected var dataList: MutableList<K>? = null
    private var clickListener: ((view: View, data: K) -> Unit)? = null

    fun changeData(data: MutableList<K>?) {
        dataList = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    protected fun bindClick(view: View, data: K) {
        if (!view.hasOnClickListeners()) {
            view.setOnClickListener { view ->
                val tag = view.tag as? K ?: return@setOnClickListener
                clickListener?.invoke(view, tag, )
            }
        }
        view.tag = data
    }

    fun setClickListener(listener: (view: View, data: K) -> Unit) {
        this.clickListener = listener
    }

}