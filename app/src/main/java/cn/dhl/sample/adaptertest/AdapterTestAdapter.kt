package cn.dhl.sample.adaptertest

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.dhl.sample.base.BindingViewHolder
import cn.dhl.sample.base.HeaderFooterAdapter
import cn.dhl.sample.databinding.AdapterTestItemLayoutBinding
import cn.dhl.sample.dp

/**
 *
 * Author: duanhl
 * Create: 6/3/21 8:58 PM
 * Description:
 *
 */

class ItemInfo {
    var color = 0
    var text = ""
    var height = 100
}

class AdapterTestAdapter : HeaderFooterAdapter<AdapterTestItemLayoutBinding, ItemInfo>() {

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<AdapterTestItemLayoutBinding> {
        val binding = AdapterTestItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BindingViewHolder(binding)
    }

    override fun onBindItemViewHolder(holder: BindingViewHolder<AdapterTestItemLayoutBinding>, position: Int) {
        val item = data?.get(position) ?: return
        val binding = holder.binding
        binding.ivBG.layoutParams.height = item.height.dp
        binding.ivBG.setBackgroundColor(item.color)
        binding.tvText.text = item.text
        bindClick(binding.tvText, item)
    }

}