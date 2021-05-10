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


val Int.dp: Int
    get() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()
    }

val Float.dp: Int
    get() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics).toInt()
    }

fun getScreenWidth(): Int {
    val wm = App.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getRealSize(point)
    return point.x
}

fun getScreenHeight(): Int {
    val wm = App.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getRealSize(point)
    return point.y
}

class BindingViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)