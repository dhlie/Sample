package cn.dhl.sample.di

import android.content.Context
import cn.dhl.sample.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 *
 * Author: duanhl
 * Create: 2021/6/27 2:59 下午
 * Description:
 *
 */
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return App.instance
    }

}