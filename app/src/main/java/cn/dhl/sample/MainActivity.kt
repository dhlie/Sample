package cn.dhl.sample

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.dhl.sample.dagger.DaggerActivity
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
        binding.btn3.setOnClickListener { requestPermission() }

    }

    private fun requestPermission() {
        PermissionHelper.with(this)
            .permission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .rationale { perms, consumer ->
                val ps = StringBuilder()
                for (p in perms) {
                    ps.append("\n").append(PermissionHelper.permissionToText(p))
                }

                AlertDialog.Builder(this)
                    .setMessage("需要以下权限才能执行此操作:$ps")
                    .setPositiveButton("确定") { _, _ ->
                        consumer.accept()
                    }
                    .setNegativeButton("取消") { _, _ ->
                        consumer.deny()
                    }
                    .show()
            }
            .onAllGranted {
            }
            .onDenied { perms ->
            }
            .onNoAskAgain { perms ->
                val ps = StringBuilder()
                for (p in perms) {
                    ps.append("\n").append(PermissionHelper.permissionToText(p))
                }

                AlertDialog.Builder(this)
                    .setMessage("请到设置页面开启如下权限:$ps")
                    .setPositiveButton("确定") { _, _ ->
                        PermissionHelper.toAppSetting(this)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
            .start()
    }

}