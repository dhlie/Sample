package cn.dhl.sample.popup

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.dhl.sample.databinding.ActivityPopupBinding
import cn.dhl.sample.databinding.PopWindowLayoutBinding
import com.dhl.base.dp

/**
 *
 * Author: duanhl
 * Create: 5/9/21 10:59 PM
 * Description:
 *
 */
class PopupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPopupBinding
    private var popupWindow: ArrowPopupWindow? = null

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
                showMenuPopup(e.rawX.toInt(), e.rawY.toInt())
                return true
            }
        }
        val gestureDetector = GestureDetector(applicationContext, gestureListener)
        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun showPopup(anchorView: View) {
        popupWindow?.dismiss()
        popupWindow = ArrowPopupWindow(this)
        val popBinding = PopWindowLayoutBinding.inflate(layoutInflater)
        popupWindow?.apply {
            contentView = popBinding.root
            isFocusable = true
            showArrow(false)
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            setRoundCornerRadius(4.dp)
            setOnDismissListener {
                popupWindow = null
            }
            showAtViewDown(anchorView, 4.dp)
        }
    }

    private fun showPopup(x: Int, y: Int) {
        popupWindow?.dismiss()
        popupWindow = ArrowPopupWindow(this)
        val popBinding = PopWindowLayoutBinding.inflate(layoutInflater)
        popupWindow?.apply {
            contentView = popBinding.root
            isFocusable = true
            showArrow(false)
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            setRoundCornerRadius(4.dp)
            setOnDismissListener {
                popupWindow = null
            }
            showAtLocationDown(binding.root, x, y)
        }

    }

    private fun showMenuPopup(x: Int, y: Int) {
        popupWindow?.dismiss()
        val pop = MenuPopWindow(this)
        popupWindow = pop

        val menus = mutableListOf<PopMenuItem>()
        menus.add(PopMenuItem(1, "添加"))
        menus.add(PopMenuItem(2, "删除"))
        menus.add(PopMenuItem(3, "重命名"))
        menus.add(PopMenuItem(4, "另存为"))

        pop.setHorMenuItems(menus, MenuClickListener {
            Toast.makeText(applicationContext, "id:$it.id", Toast.LENGTH_SHORT).show()
        })
        pop.showAtLocationDown(binding.root, x, y)

    }
}