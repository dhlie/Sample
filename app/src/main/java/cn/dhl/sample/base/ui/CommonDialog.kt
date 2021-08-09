package cn.dhl.sample.base.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import cn.dhl.sample.R
import cn.dhl.sample.databinding.DialogCommonBinding
import com.dhl.base.ui.BaseDialog

/**
 *
 * Author: duanhaoliang
 * Create: 2021/7/21 17:38
 * Description:
 *
 */
class CommonDialog(context: Context) : BaseDialog(context, R.style.BaseDialog_Common) {

    private var binding: DialogCommonBinding = DialogCommonBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    /**
     * 设置标题
     * @param text 标题，为空时不显示标题
     */
    fun setTitleText(text: CharSequence?) {
        binding.tvTitle.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.tvTitle.text = text
    }

    /**
     * 设置显示内容
     */
    fun setContentText(text: CharSequence) {
        binding.tvContent.text = text
    }

    /**
     * 设置取消按钮
     * @param text 按钮文字，为空时不显示取消按钮
     * @param autoDismiss 点击后是否关闭对话框
     */
    fun setNegativeButton(text: CharSequence?, autoDismiss: Boolean = true, listener: View.OnClickListener? = null) {
        if (text.isNullOrEmpty()) {
            binding.groupNegBtn.visibility = View.GONE
            return
        }
        binding.groupNegBtn.visibility = View.VISIBLE
        binding.btnNegative.text = text
        binding.btnNegative.setOnClickListener { view ->
            listener?.onClick(view)
            if (autoDismiss) {
                dismiss()
            }
        }
    }

    /**
     * 设置确定按钮
     * @param text 按钮文字，为空时不显示确定按钮
     * @param autoDismiss 点击后是否关闭对话框
     */
    fun setPositiveButton(text: CharSequence?, autoDismiss: Boolean = true, listener: View.OnClickListener? = null) {
        if (text.isNullOrEmpty()) {
            binding.btnPositive.visibility = View.GONE
            return
        }
        binding.btnPositive.visibility = View.VISIBLE
        binding.btnPositive.text = text
        binding.btnPositive.setOnClickListener { view ->
            listener?.onClick(view)
            if (autoDismiss) {
                dismiss()
            }
        }
    }

    companion object {

        fun showTipsDialog(
            context: Context,
            canceledOnTouchOutside: Boolean = false,
            title: CharSequence? = context.getString(R.string.dialog_title),
            content: CharSequence,
            negativeText: CharSequence? = context.getString(R.string.dialog_negative_btn),
            negativeAutoDismiss: Boolean = true,
            negativeClickListener: View.OnClickListener? = null,
            positiveText: CharSequence? = context.getString(R.string.dialog_positive_btn),
            positiveAutoDismiss: Boolean = true,
            positiveClickListener: View.OnClickListener? = null,
        ): CommonDialog {
            return CommonDialog(context).apply {
                setCanceledOnTouchOutside(canceledOnTouchOutside)
                setTitleText(title)
                setContentText(content)
                setNegativeButton(negativeText, negativeAutoDismiss, negativeClickListener)
                setPositiveButton(positiveText, positiveAutoDismiss, positiveClickListener)
                show()
            }
        }

    }

}