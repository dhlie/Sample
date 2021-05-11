package cn.dhl.sample.popup

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.dhl.sample.dp
import java.lang.RuntimeException

/**
 *
 * Author: duanhl
 * Create: 5/11/21 10:21 PM
 * Description:
 *
 */

class PopMenuItem(val id: Int = -1, val text: String = "", val icon: Int = 0, val data: Any? = null)

fun interface MenuClickListener {
    fun onMenuClick(id: Int, data: Any?)
}

class MenuPopWindow(private val context: Context) : ArrowPopupWindow() {

    private var menuLayout: LinearLayout? = null
    private var horEdgePadding = 16.dp
    private var verEdgePadding = 8.dp
    private var itemPadding = 32.dp
    private var dividerSize = 0.5f.dp
    private var dividerHorPadding = 0
    private var dividerVerPadding = 0
    private var bgColor = 0xFF000000.toInt()
    private var textColor = 0xFFFFFFFF.toInt()
    private var dividerColor = 0x99FFFFFF.toInt()
    private var textSize = 14f //sp

    init {
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = true
    }

    /**
     * 设置间距
     * @param horEdgePadding 左右两边边距
     * @param verEdgePadding 上下边距
     * @param itemPadding item 间距
     */
    fun setupItemPadding(horEdgePadding: Int = this.horEdgePadding, verEdgePadding: Int = this.verEdgePadding, itemPadding: Int = this.itemPadding) {
        checkInvokeOrder()
        this.horEdgePadding = horEdgePadding
        this.verEdgePadding = verEdgePadding
        this.itemPadding = itemPadding
    }

    fun setupDivider(dividerColor: Int = this.dividerColor, dividerSize: Int = this.dividerSize, dividerHorPadding: Int = this.dividerHorPadding, dividerVerPadding: Int = this.dividerVerPadding) {
        checkInvokeOrder()
        this.dividerColor = dividerColor
        this.dividerSize = dividerSize
        this.dividerHorPadding = dividerHorPadding
        this.dividerVerPadding = dividerVerPadding
    }

    fun setupColor(bgColor: Int = this.bgColor, textColor: Int = this.textColor) {
        checkInvokeOrder()
        this.bgColor = bgColor
        this.textColor = textColor
    }

    fun setupTextSize(textSize: Float) {
        checkInvokeOrder()
        this.textSize = textSize
    }

    private fun checkInvokeOrder() {
        if (menuLayout != null) {
            throw RuntimeException("Please invoke setup method before setHorMenuItems()")
        }
    }

    /**
     * 水平 menu 不支持图标
     */
    fun setHorMenuItems(menus: List<PopMenuItem>, menuClickListener: MenuClickListener) {
        val menuLayout = LinearLayout(context)
        menuLayout.orientation = LinearLayout.HORIZONTAL
        for (index in menus.indices) {
            val item = menus[index]
            TextView(context).apply {
                menuLayout.addView(this)
                text = item.text
                setTextColor(this@MenuPopWindow.textColor)
                textSize = this@MenuPopWindow.textSize
                gravity = Gravity.CENTER
                val dividerView = View(context).apply {
                    layoutParams = LinearLayout.LayoutParams(dividerSize, LinearLayout.LayoutParams.MATCH_PARENT)
                    setPadding(0, dividerVerPadding, 0, dividerVerPadding)
                    setBackgroundColor(dividerColor)
                }

                if (index == 0) {
                    setPadding(horEdgePadding, verEdgePadding, itemPadding / 2, verEdgePadding)
                    menuLayout.addView(dividerView)
                } else if (index == menus.size - 1) {
                    setPadding(itemPadding / 2, verEdgePadding, horEdgePadding, verEdgePadding)
                } else {
                    setPadding(itemPadding / 2, verEdgePadding, itemPadding / 2, verEdgePadding)
                    menuLayout.addView(dividerView)
                }
//                setBackgroundResource(R.drawable.)//TODO
                setOnClickListener {
                    menuClickListener.onMenuClick(item.id, item.data)
                }
            }
        }
        this.menuLayout = menuLayout
        contentView = menuLayout
        setRoundCornerRadius(8.dp)
        setBGColor(bgColor)
    }

}