package cn.dhl.sample.dagger

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.dhl.sample.App
import cn.dhl.sample.dagger.di.Car
import cn.dhl.sample.dagger.di.Engine
import cn.dhl.sample.databinding.ActivityMainBinding
import cn.dhl.sample.di.DaggerService
import javax.inject.Inject
import javax.inject.Named

/**
 *
 * Author: duanhl
 * Create: 5/9/21 10:34 PM
 * Description:
 *
 */
class Dagger02Activity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var appContext: Context

    private lateinit var car: Car
    @Inject
    @Named("v8")
    lateinit var car2: Car

    @Inject
    @Named("v8")
    lateinit var car3: Car

    @Inject
    fun setCar(@Named("v12") car: Car) {
        this.car = car
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Dagger02Activity"

        DaggerService.get(App.instance).inject(this)
        println("Dagger02Activity  car:$car  car2:$car2  car3:$car3  ctx:$appContext")
    }
}