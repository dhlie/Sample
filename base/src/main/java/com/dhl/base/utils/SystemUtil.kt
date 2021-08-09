package com.dhl.base.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 *
 * Author: duanhaoliang
 * Create: 2021/4/13 16:48
 * Description:
 *
 */

fun isActivityValid(context: Context?): Boolean {
    val activity = getActivityFromContext(context) ?: return false
    return !activity.isFinishing && !activity.isDestroyed
}

fun getActivityFromView(view: View?): Activity? {
    return if (view == null || view.context == null) {
        null
    } else {
        getActivityFromContext(view.context)
    }
}

fun getActivityFromContext(context: Context?): Activity? {
    if (context is Activity) {
        return context
    }
    if (context is ContextWrapper) {
        var ctx = context
        while (ctx !is Activity && ctx is ContextWrapper) {
            ctx = ctx.baseContext
        }
        if (ctx is Activity) {
            return ctx
        }
    }
    return null
}

fun showSoftKeyboard(editText: EditText?) {
    val context = editText?.context ?: return
    editText.requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
}

fun hideSoftKeyboard(editText: View?) {
    if (editText == null) {
        return
    }
    editText.clearFocus()
    val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    imm.hideSoftInputFromWindow(editText.windowToken, 0)
}