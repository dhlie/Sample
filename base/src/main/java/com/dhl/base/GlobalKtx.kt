package com.dhl.base

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Point
import android.util.Log
import android.util.TypedValue
import android.view.WindowManager
import com.dhl.base.utils.Logger
import java.io.Serializable

/**
 *
 * Author: duanhaoliang
 * Create: 2021/4/9 9:08
 * Description:
 *
 */

val screenWidth: Int
    get() {
        val wm = ContextHolder.appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getRealSize(point)
        return point.x
    }

val screenHeight: Int
    get() {
        val wm = ContextHolder.appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getRealSize(point)
        return point.y
    }

/**
 * app 显示宽度
 */
val appWindowWidth: Int
    get() {
        val wm = ContextHolder.appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return point.x
    }

/**
 * app 显示高度
 */
val appWindowHeight: Int
    get() {
        val wm = ContextHolder.appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return point.y
    }

val statusBarHeight: Int
    get() {
        val resourceId: Int = ContextHolder.appContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        return ContextHolder.appContext.resources.getDimensionPixelSize(resourceId)
    }

/**
 * @see android.util.TypedValue.complexToDimensionPixelSize
 */
val Int.dp: Int
    get() {
        val f = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            ContextHolder.appContext.resources.displayMetrics
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
            ContextHolder.appContext.resources.displayMetrics
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
        ContextHolder.appContext.resources.displayMetrics
    )

/**
 * @see android.util.TypedValue.complexToDimensionPixelSize
 */
val Float.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        ContextHolder.appContext.resources.displayMetrics
    )

internal inline fun SharedPreferences.commit(crossinline exec: SharedPreferences.Editor.() -> Unit) {
    val editor = this.edit()
    editor.exec()
    editor.apply()
}

data class MutablePair<A, B>(var first: A, var second: B) : Serializable {
    override fun toString(): String = "MutablePair($first, $second)"
}

fun <K> MutableList<K>.addList(list: List<K>?) {
    if (list.isNullOrEmpty()) {
        return
    }
    addAll(list)
}

inline fun log(level: Int = Log.INFO, block: () -> String?) {
    if (Logger.PRINT_LOG) {
        when (level) {
            Log.VERBOSE -> Logger.v(block.invoke())
            Log.DEBUG -> Logger.d(block.invoke())
            Log.INFO -> Logger.i(block.invoke())
            Log.WARN -> Logger.w(block.invoke())
            Log.ERROR -> Logger.e(block.invoke())
        }
    }
}

inline fun logJson(block: () -> Any?) {
    if (Logger.PRINT_LOG) {
        Logger.json(block.invoke())
    }
}