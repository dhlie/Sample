package cn.dhl.sample.di

import cn.dhl.sample.dagger.Dagger02Activity
import cn.dhl.sample.dagger.DaggerActivity
import dagger.Component
import javax.inject.Singleton

/**
 *
 * Author: duanhl
 * Create: 2021/6/27 2:59 下午
 * Description:
 *
 */
@Component(modules = [AppModule::class, ActivityModule::class])
@Singleton
interface AppComponent {

    fun inject(daggerActivity: DaggerActivity)
    fun inject(dagger02Activity: Dagger02Activity)
}