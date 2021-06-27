package cn.dhl.sample.di

import android.annotation.SuppressLint
import android.content.Context

/**
 *
 * Author: duanhl
 * Create: 2021/6/27 3:54 下午
 * Description:
 *
 */
class DaggerService {

    companion object {

        const val SERVICE_NAME = "dagger"

        @SuppressLint("WrongConstant")
        fun get(context: Context): AppComponent {
            return context.getSystemService(SERVICE_NAME) as AppComponent
        }
    }
}