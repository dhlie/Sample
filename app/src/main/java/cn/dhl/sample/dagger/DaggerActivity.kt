package cn.dhl.sample.dagger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.dhl.sample.R
import javax.inject.Inject
import javax.inject.Named

/**
 *
 * Author: duanhl
 * Create: 5/9/21 10:34 PM
 * Description:
 *
 */
class DaggerActivity : AppCompatActivity() {

    @Inject
    lateinit var engine: Engine
    @Inject
    lateinit var engine2: Engine

    @Named("v12")
    @Inject
    lateinit var eName: String

    private lateinit var car: Car
    @Inject
    lateinit var car2: Car

    @Inject
    fun setCar(car: Car) {
        this.car = car
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "DaggerActivity"

        DaggerCarComponent.create().engineComponent().name("aaa").build().inject(this)
        println("engine:${engine.hashCode()} engine2:${engine2.hashCode()} name:$eName  car:$car  car2:$car2")
    }
}