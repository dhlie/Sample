package cn.dhl.sample

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import cn.dhl.sample.adaptertest.AdapterTestActivity
import cn.dhl.sample.dagger.DaggerActivity
import cn.dhl.sample.databinding.ActivityMainBinding
import cn.dhl.sample.down.DownActivity
import cn.dhl.sample.popup.PopupActivity
import com.dhl.base.ui.BaseActivity
import com.dhl.base.utils.PermissionHelper

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        titleBar?.leftView = null
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn1.apply {
            text = "Dagger"
            setOnClickListener { startActivity(Intent(applicationContext, DaggerActivity::class.java)) }
        }
        binding.btn2.apply {
            text = "Popup"
            setOnClickListener { startActivity(Intent(applicationContext, PopupActivity::class.java)) }
        }
        binding.btn3.apply {
            text = "Permission"
            setOnClickListener { requestPermission() }
        }
        binding.btn4.apply {
            text = "PickImage"
            setOnClickListener { openFileExplorer() }
        }
        binding.btn5.apply {
            text = "adapter"
            setOnClickListener { startActivity(Intent(applicationContext, AdapterTestActivity::class.java)) }
        }
        binding.btn6.apply {
            text = "download"
            setOnClickListener { startActivity(Intent(applicationContext, DownActivity::class.java)) }
        }
    }


    private fun openFileExplorer() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "*/*"
//        }
//
//        startActivityForResult(intent, REQUEST_CODE_OPEN_FILE)

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, REQUEST_CODE_OPEN_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_OPEN_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                try {
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        val name = getFileDisplayNameFromUri(applicationContext, uri)
                        val length = inputStream.available()
                        if (length > 0) {
                            val byte = inputStream.read()
                            Log.i(
                                TAG, "pppick  first byte:$byte  inputStream length: $length  fileName:${name}"
                            )
                        } else {
                            Log.i(TAG, "pppick length =====0 inputStream length: $length  fileName:${name}")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Returns a file's display name from its [android.content.ContentResolver.SCHEME_FILE]
     * or [android.content.ContentResolver.SCHEME_CONTENT] Uri. The display name of a file
     * includes its extension.
     *
     * @param context Context trying to resolve the file's display name.
     * @param uri Uri of the file.
     * @return the file's display name, or the uri's string if something fails or the uri isn't in
     * the schemes specified above.
     */
    private fun getFileDisplayNameFromUri(context: Context, uri: Uri): String? {
        val scheme = uri.scheme
        if (ContentResolver.SCHEME_FILE == scheme) {
            return uri.lastPathSegment
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        return cursor.getString(nameIndex)
                    }
                    val dataIndex = cursor.getColumnIndex("_data")
                    if (dataIndex != -1) {
                        cursor.getString(dataIndex)?.let { path ->
                            val index = path.lastIndexOf("/")
                            return if (index == -1) path else path.substring(index + 1)
                        }
                    }
                }
                return null
            }
        }

        // This will only happen if the Uri isn't either SCHEME_CONTENT or SCHEME_FILE, so we assume
        // it already represents the file's name.
        return uri.toString()
    }

    private fun requestPermission() {
        PermissionHelper.with(this)
            .intentOrNull {
                if (Settings.canDrawOverlays(this)) {
                    null
                } else {
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                }
            }
            .onIntentResult {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(applicationContext, "window permission ok", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "window permission fail", Toast.LENGTH_SHORT).show()
                }
            }
            .start()


//        PermissionHelper.with(this)
//            .permission(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//            .rationale { perms, consumer ->
//                val ps = StringBuilder()
//                for (p in perms) {
//                    ps.append("\n").append(PermissionHelper.permissionToText(p))
//                }
//
//                AlertDialog.Builder(this)
//                    .setMessage("需要以下权限才能执行此操作:$ps")
//                    .setPositiveButton("确定") { _, _ ->
//                        consumer.accept()
//                    }
//                    .setNegativeButton("取消") { _, _ ->
//                        consumer.deny()
//                    }
//                    .show()
//            }
//            .onGranted {
//                Toast.makeText(applicationContext, "permission ok", Toast.LENGTH_SHORT).show()
//            }
//            .onDenied { perms, noAskAgainPerms ->
//                noAskAgainPerms?.let { perms ->
//                    val ps = StringBuilder()
//                    for (p in perms) {
//                        ps.append("\n").append(PermissionHelper.permissionToText(p))
//                    }
//
//                    AlertDialog.Builder(this)
//                        .setMessage("请到设置页面开启如下权限:$ps")
//                        .setPositiveButton("确定") { _, _ ->
//                            PermissionHelper.toAppSetting(this)
//                        }
//                        .setNegativeButton("取消", null)
//                        .show()
//                }
//            }
//            .start()
    }

    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_CODE_OPEN_FILE = 100
    }

}