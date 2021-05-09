package cn.dhl.sample

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 *
 * Author: duanhl
 * Create: 5/9/21 11:37 PM
 * Description:
 *
 */
class App : Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: Context

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}