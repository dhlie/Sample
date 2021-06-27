package cn.dhl.sample.di

import cn.dhl.sample.dagger.di.CarModule
import cn.dhl.sample.dagger.di.EngineModule
import dagger.Module

/**
 *
 * Author: duanhl
 * Create: 2021/6/27 3:26 下午
 * Description:
 *
 */
@Module(includes = [CarModule::class, EngineModule::class])
class ActivityModule {

    //@Provides
    //@Named("v8")
    //fun provideEngineV8(): Engine {
    //    return Engine("v8")
    //}
    //
    //@Provides
    //@Named("v12")
    //fun provideEngineV12(): Engine {
    //    return Engine("v12")
    //}
    //
    //@Named("v8")
    //@Provides
    //fun provideV8Car(@Named("v8") engine: Engine): Car {
    //    return Car(engine)
    //}
    //
    //@Named("v12")
    //@Provides
    //fun provideV12Car(@Named("v12") engine: Engine): Car {
    //    return Car(engine)
    //}
}