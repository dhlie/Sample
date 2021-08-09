package com.dhl.base.ui

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.StyleRes
import com.dhl.base.R
import com.dhl.base.utils.getActivityFromContext
import com.dhl.base.utils.isActivityValid

/**
 *
 * Author: duanhaoliang
 * Create: 2021/6/25 9:35
 * Description:
 *
 */
open class BaseDialog : Dialog {

    constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setWindowAnimations(R.style.CommonDialogAnimation)
    }

    override fun onContentChanged() {
        super.onContentChanged()
        window?.attributes?.let { params ->
            params.width = context.resources.getDimensionPixelSize(R.dimen.dialog_width)
            window?.attributes = params
        }
    }

    override fun show() {
        val activity = getActivityFromContext(context)
        if (!isActivityValid(activity)) {
            return
        }
        super.show()
    }

    /**
     * 设置全屏对话框样式
     */
    fun setFullScreen() {
        val window = window ?: return
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val layoutParams = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layoutParams?.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = layoutParams
    }
}