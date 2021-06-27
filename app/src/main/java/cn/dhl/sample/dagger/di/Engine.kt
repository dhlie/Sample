package cn.dhl.sample.dagger.di

import android.util.Log
import dagger.*
import javax.inject.*


/**
 *
 * Author: duanhl
 * Create: 5/4/21 10:46 PM
 * Description:
 *
 */

class Engine constructor(){

    private var name: String? = null

    constructor(name: String): this() {
        this.name = name
    }

    override fun toString(): String {
        return "Engine[${super.toString()}]{ name:$name}"
    }

}

class Car constructor(private val engine: Engine) {

    override fun toString(): String {
        return "Car[${super.toString()}]: $engine"
    }
}

@Module
class EngineModule {


    @Provides
    @Named("v8")
    fun provideEngineV8(): Engine {
        return Engine("v8")
    }

    @Provides
    @Named("v12")
    fun provideEngineV12(): Engine {
        return Engine("v12")
    }
}

@Module
class CarModule {

    @Provides
    @Named("v8")
    fun provideV8Car(@Named("v8") engine: Engine): Car {
        return Car(engine)
    }

    @Provides
    @Named("v12")
    fun provideV12Car(@Named("v12") engine: Engine): Car {
        return Car(engine)
    }

}

//============= Component dependencies =============
//@Component(modules = [CarModule::class], dependencies = [EngineComponent::class])
//interface CarComponent {
//
//    fun inject(daggerActi: DaggerActivity)
//
//}
//
//@Component(modules = [EngineModule::class])
//interface EngineComponent {
//
//    @Named("v8")
//    fun getEngineV8(): Engine
//
//    @Named("v12")
//    fun getEngineV12(): Engine
//}


//============= Subcomponent =============
//@Component(modules = [EngineModule::class])
//interface EngineComponent {
//
//    fun carComponent(): CarComponent.Builder
//}
//
//@Subcomponent(modules = [CarModule::class])
//interface CarComponent {
//
//    fun inject(da: DaggerActivity)
//
//    @Subcomponent.Builder
//    interface Builder {
//        fun build(): CarComponent
//    }
//
//
//}


