package cn.dhl.sample.dagger

import android.util.Log
import cn.dhl.sample.MainActivity
import dagger.*
import java.lang.annotation.RetentionPolicy
import javax.inject.*


/**
 *
 * Author: duanhl
 * Create: 5/4/21 10:46 PM
 * Description:
 *
 */

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class V8

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class V12

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class Acti1Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class Acti2Scope

class Engine constructor(){

    private var name: String? = null

    @Inject constructor(@Named("v8") name: String): this() {
        this.name = name
    }

    override fun toString(): String {
        return "Engine{ name:$name}"
    }

    fun run() {
        Log.i("tag", "引擎转起来了~~~ ")
    }
}

class Car {

    public constructor() {

    }
}

@Module
class EngineModule {

    @Named("v12")
    @Provides
    fun getEngineV12(): String {
        return "v12"
    }

    @Named("v8")
    @Provides
    fun getEngineV8(): String {
        return "v8"
    }

    @Provides
    fun provideEngine(): Engine {
        return Engine("v3")
    }
}

@Module
class CarModule {

    @Provides
    fun provideCar(): Car {
        return Car()
    }

}

@Subcomponent(modules = [EngineModule::class])
interface EngineSubComponent {

    fun inject(activity: DaggerActivity)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun name(name: String): Builder
        fun build(): EngineSubComponent
    }
}

@Component(modules = [CarModule::class])
interface CarComponent {

    fun engineComponent(): EngineSubComponent.Builder
}




