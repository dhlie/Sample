package cn.dhl.sample

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 *
 * Author: duanhl
 * Create: 2021/4/11 5:15 PM
 * Description:
 *
 */

/**
 * 屏幕宽度
 */
val screenWidth: Int
    get() {
        val wm = App.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getRealSize(point)
        return point.x
    }

/**
 * 屏幕高度
 */
val screenHeight: Int
    get() {
        val wm = App.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getRealSize(point)
        return point.y
    }

/**
 * app 显示宽度
 */
val appWindowWidth: Int
    get() {
        val wm = App.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return point.x
    }

/**
 * app 显示高度
 */
val appWindowHeight: Int
    get() {
        val wm = App.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return point.y
    }

/**
 * @see android.util.TypedValue.complexToDimensionPixelSize
 */
val Int.dp: Int
    get() {
        val f = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            App.instance.resources.displayMetrics
        )
        val res = if (f >= 0) f + 0.5f else f - 0.5f
        if (res != 0f) return res.toInt()
        if (this == 0) return 0
        if (this > 0) return 1
        return -1
    }

/**
 * @see android.util.TypedValue.complexToDimensionPixelSize
 */
val Float.dp: Int
    get() {
        val f = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            App.instance.resources.displayMetrics
        )
        val res = if (f >= 0) f + 0.5f else f - 0.5f
        if (res != 0f) return res.toInt()
        if (this == 0f) return 0
        if (this > 0f) return 1
        return -1
    }

/**
 * @see android.util.TypedValue.complexToDimensionPixelSize
 */
val Int.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        App.instance.resources.displayMetrics
    )

/**
 * @see android.util.TypedValue.complexToDimensionPixelSize
 */
val Float.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        App.instance.resources.displayMetrics
    )

class BindingViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)