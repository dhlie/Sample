package cn.dhl.sample.popup

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import cn.dhl.sample.databinding.ActivityPopupBinding
import cn.dhl.sample.databinding.PopWindowLayoutBinding
import cn.dhl.sample.dp

/**
 *
 * Author: duanhl
 * Create: 5/9/21 10:59 PM
 * Description:
 *
 */
class PopupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "PopupActivity"

        binding.btn1.setOnClickListener { showPopup(it) }
        binding.btn2.setOnClickListener { showPopup(it) }
        binding.btn3.setOnClickListener { showPopup(it) }
        binding.btn4.setOnClickListener { showPopup(it) }
        binding.btn5.setOnClickListener { showPopup(it) }
        binding.btn6.setOnClickListener { showPopup(it) }
        binding.btnb1.setOnClickListener { showPopup(it) }
        binding.btnb2.setOnClickListener { showPopup(it) }
        binding.btnb3.setOnClickListener { showPopup(it) }
        binding.btnb4.setOnClickListener { showPopup(it) }
        binding.btnb5.setOnClickListener { showPopup(it) }
        binding.btnb6.setOnClickListener { showPopup(it) }


        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                showPopup(e.rawX.toInt(), e.rawY.toInt())
                return true
            }
        }
        val gestureDetector = GestureDetector(applicationContext, gestureListener)
        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    var popupWindow: ArrowPopupWindow? = null
    private fun showPopup(anchorView: View) {
        popupWindow?.dismiss()
        popupWindow = ArrowPopupWindow()
        val popBinding = PopWindowLayoutBinding.inflate(layoutInflater)
        popupWindow?.contentView = popBinding.root
        popupWindow?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow?.setRoundCornerRadius(4.dp)
        popupWindow?.showAtViewUp(anchorView, 0.dp)
    }

    private fun showPopup(x: Int, y: Int) {
        popupWindow?.dismiss()
        popupWindow = ArrowPopupWindow()
        val popBinding = PopWindowLayoutBinding.inflate(layoutInflater)
        popupWindow?.contentView = popBinding.root
        popupWindow?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow?.setRoundCornerRadius(4.dp)
        popupWindow?.showAtLocationUp(binding.root, x, y)
    }
}