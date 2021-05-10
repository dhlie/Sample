package cn.dhl.sample.popup

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import cn.dhl.sample.getScreenHeight

/**
 *
 * Author: duanhl
 * Create: 5/10/21 8:18 PM
 * Description:
 *
 */
class ArrowPopupWindow : PopupWindow() {

    private lateinit var arrowBGView: PopupArrowBackgroundView

    override fun setContentView(contentView: View?) {
        if (contentView == null) {
            return
        }
        arrowBGView = PopupArrowBackgroundView(contentView.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            addView(contentView)
            measure(0, 0)
        }
        super.setContentView(arrowBGView)
    }

    /**
     * 设置背景色
     */
    fun setBGColor(color: Int) {
        arrowBGView.setColor(color)
    }

    /**
     * 设置箭头尺寸
     */
    fun setArrowSize(width: Int, height: Int) {
        arrowBGView.setArrowSize(width, height)
    }

    /**
     * 设置圆角半径
     */
    fun setRoundCornerRadius(radius: Int) {
        arrowBGView.roundCornerRadius = radius
    }

    /**
     * 设置左右边距
     */
    fun setHorMargin(margin: Int) {
        arrowBGView.horMargin = margin
    }

    /**
     * 显示在坐标点 x, y 的下面, 下面空间不足时显示在上面
     * @param x 箭头顶点的 x 坐标 use view.getLocationOnScreen() 获取
     * @param y 箭头顶点的 y 坐标 use view.getLocationOnScreen() 获取
     */
    fun showAtLocationDown(parent: View, x: Int, y: Int) {
        val width = arrowBGView.measuredWidth
        val height = arrowBGView.measuredHeight

        arrowBGView.setArrowPosition(x, y)

        if (y + height >= getScreenHeight()) {
            arrowBGView.setArrowAtTop(false)
            showAtLocation(parent, Gravity.START or Gravity.TOP, x - width / 2 , y - height)
        } else {
            arrowBGView.setArrowAtTop(true)
            showAtLocation(parent, Gravity.START or Gravity.TOP, x - width / 2, y)
        }
    }

    /**
     * 显示在坐标点 x, y 的上面, 上面空间不足时显示在下面
     * @param x 箭头顶点的 x 坐标 use view.getLocationOnScreen() 获取
     * @param y 箭头顶点的 y 坐标 use view.getLocationOnScreen() 获取
     */
    fun showAtLocationUp(parent: View, x: Int, y: Int) {
        val width = arrowBGView.measuredWidth
        val height = arrowBGView.measuredHeight

        arrowBGView.setArrowPosition(x, y)

        if (y <= height) {
            arrowBGView.setArrowAtTop(true)
            showAtLocation(parent, Gravity.START or Gravity.TOP, x - width / 2, y)
        } else {
            arrowBGView.setArrowAtTop(false)
            showAtLocation(parent, Gravity.START or Gravity.TOP, x - width / 2 , y - height)
        }
    }
}