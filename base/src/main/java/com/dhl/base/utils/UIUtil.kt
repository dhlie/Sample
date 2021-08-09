package com.dhl.base.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import com.dhl.base.dp

/**
 *
 * Author: duanhaoliang
 * Create: 2021/7/19 11:32
 * Description:
 *
 */
class UIUtil private constructor(){
    companion object {

        private var statusBarHeight = -1 //状态栏高度

        /**
         * 获取状态栏高度
         *
         * @param context
         * @return
         */
        fun getStatusBarHeight(context: Context): Int {
            if (statusBarHeight == -1) {
                val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
                statusBarHeight = if (resourceId > 0) {
                    context.resources.getDimensionPixelSize(resourceId)
                } else {
                    24.dp
                }
            }
            return statusBarHeight
        }

        /**
         * 获取 view 在其父类中的坐标
         */
        fun getPositionInParent(targetView: View, parent: View, out: Rect) {
            out.set(targetView.left, targetView.top, targetView.right, targetView.bottom)
            var view = targetView.parent as? View
            while (view != null && view !== parent) {
                out.offset(view.left, view.top)
                view = view.parent as? View
            }
        }
    }
}