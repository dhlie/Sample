package cn.dhl.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.dhl.sample.dagger.*
import cn.dhl.sample.databinding.ActivityMainBinding
import cn.dhl.sample.popup.PopupActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn1.setOnClickListener { startActivity(Intent(applicationContext, DaggerActivity::class.java)) }
        binding.btn2.setOnClickListener { startActivity(Intent(applicationContext, PopupActivity::class.java)) }

    }
}