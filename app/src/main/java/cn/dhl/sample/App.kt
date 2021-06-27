package cn.dhl.sample

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cn.dhl.sample.di.AppComponent
import cn.dhl.sample.di.DaggerAppComponent
import cn.dhl.sample.di.DaggerService

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

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        initDagger()
    }

    override fun getSystemService(name: String): Any? {
        if (name == DaggerService.SERVICE_NAME) {
            return appComponent
        }
        return super.getSystemService(name)
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder().build()
    }
}