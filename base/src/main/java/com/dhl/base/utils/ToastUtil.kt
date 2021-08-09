package com.dhl.base.utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.dhl.base.AppExecutors
import com.dhl.base.ContextHolder
import com.dhl.base.R

class ToastUtil private constructor(){
    companion object {
        fun showToast(@StringRes resId: Int, context: Context = ContextHolder.appContext, duration: Int = Toast.LENGTH_SHORT) {
            showToast(context.getText(resId), context, duration)
        }

        fun showToast(message: CharSequence, context: Context = ContextHolder.appContext, duration: Int = Toast.LENGTH_SHORT) {
            if (message.isBlank()) return
            AppExecutors.executeOnMain {
                doShowToast(context, message, duration)
            }
        }

        private fun doShowToast(context: Context, message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
            val textView = TextView(context).apply {
                text = message
                setPadding(36, 36, 36, 36)
                compoundDrawablePadding = 30
                textSize = 14f
                setTextColor(Color.WHITE)
                setBackgroundResource(R.drawable.black_alpha_bg)
            }

            Toast(context).apply {
                view = textView
                setGravity(Gravity.CENTER, 0, 0)
                this.duration = duration
                show()
            }
        }

    }
}