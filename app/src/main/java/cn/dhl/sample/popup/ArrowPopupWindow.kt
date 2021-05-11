package cn.dhl.sample.popup

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import cn.dhl.sample.appWindowHeight
import cn.dhl.sample.statusBarHeight
import java.lang.RuntimeException

/**
 *
 * Author: duanhl
 * Create: 5/10/21 8:18 PM
 * Description:
 *
 */
open class ArrowPopupWindow : PopupWindow() {

    private lateinit var backgroundView: PopupArrowBackgroundView

    override fun setContentView(contentView: View?) {
        if (contentView == null) {
            return
        }
        backgroundView = PopupArrowBackgroundView(contentView.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            addView(contentView)
        }
        super.setContentView(backgroundView)
    }

    private fun checkInvokeOrder() {
        if (::backgroundView.isInitialized) {
            throw RuntimeException("Please invoke this method before setContentView()")
        }
    }

    /**
     * 设置背景色
     */
    fun setBGColor(color: Int) {
        checkInvokeOrder()
        backgroundView.setColor(color)
    }

    /**
     * 设置箭头尺寸
     */
    fun setArrowSize(width: Int, height: Int) {
        checkInvokeOrder()
        backgroundView.setArrowSize(width, height)
    }

    /**
     * 设置圆角半径
     */
    fun setRoundCornerRadius(radius: Int) {
        checkInvokeOrder()
        backgroundView.roundCornerRadius = radius
    }

    /**
     * 设置左右边距
     */
    fun setHorMargin(margin: Int) {
        checkInvokeOrder()
        backgroundView.horMargin = margin
    }

    /**
     * 是否显示箭头
     */
    fun showArrow(show: Boolean) {
        checkInvokeOrder()
        backgroundView.showArrow = show
    }

    /**
     * 显示在坐标点 x, y 的下面, 下面空间不足时显示在上面, 箭头顶点坐标为 (x, y)
     * @param x 箭头顶点的 x 坐标 use view.getLocationOnScreen() 获取
     * @param y 箭头顶点的 y 坐标 use view.getLocationOnScreen() 获取
     */
    fun showAtLocationDown(parent: View, x: Int, y: Int) {
        backgroundView.measure(0, 0)
        val width = backgroundView.measuredWidth
        val height = backgroundView.measuredHeight

        backgroundView.setArrowPosition(x, y)

        if (y + height >= appWindowHeight) {
            backgroundView.setArrowAtTop(false)
            showAtLocation(parent, Gravity.START or Gravity.TOP, x - width / 2 , y - height)
        } else {
            backgroundView.setArrowAtTop(true)
            showAtLocation(parent, Gravity.START or Gravity.TOP, x - width / 2, y)
        }
    }

    /**
     * 显示在坐标点 x, y 的上面, 上面空间不足时显示在下面, 箭头顶点坐标为 (x, y)
     * @param x 箭头顶点的 x 坐标 use view.getLocationOnScreen() 获取
     * @param y 箭头顶点的 y 坐标 use view.getLocationOnScreen() 获取
     */
    fun showAtLocationUp(parent: View, x: Int, y: Int) {
        backgroundView.measure(0, 0)
        val width = backgroundView.measuredWidth
        val height = backgroundView.measuredHeight

        backgroundView.setArrowPosition(x, y)

        if (y < height + statusBarHeight) {
            backgroundView.setArrowAtTop(true)
            showAtLocation(parent, Gravity.START or Gravity.TOP, x - width / 2, y)
        } else {
            backgroundView.setArrowAtTop(false)
            showAtLocation(parent, Gravity.START or Gravity.TOP, x - width / 2 , y - height)
        }
    }

    /**
     * 显示在 anchorView 的下面, 下面空间不足时显示在上面
     * 箭头 x 坐标为 anchorView 中点坐标
     * 箭头 y 坐标为 anchorView 底部 + yOffset  或  anchorView 顶部 - yOffset
     * @param yOffset 离 anchorView 底部的距离
     */
    fun showAtViewDown(anchorView: View, yOffset: Int) {
        backgroundView.measure(0, 0)
        val width = backgroundView.measuredWidth
        val height = backgroundView.measuredHeight
        val anchorPosition = intArrayOf(0, 0)
        anchorView.getLocationOnScreen(anchorPosition)

        val arrowX = anchorPosition[0] + anchorView.measuredWidth / 2
        var arrowY = anchorPosition[1] +anchorView.measuredHeight + yOffset
        if (arrowY + height >= appWindowHeight) {
            arrowY = anchorPosition[1] - yOffset
            backgroundView.setArrowAtTop(false)
            backgroundView.setArrowPosition(arrowX, arrowY)
            showAtLocation(anchorView, Gravity.START or Gravity.TOP, arrowX - width / 2 , arrowY - height)
        } else {
            backgroundView.setArrowAtTop(true)
            backgroundView.setArrowPosition(arrowX, arrowY)
            showAtLocation(anchorView, Gravity.START or Gravity.TOP, arrowX - width / 2 , arrowY)
        }
    }

    /**
     * 显示在 anchorView 的上面, 上面空间不足时显示在下面
     * 箭头 x 坐标为 anchorView 中点坐标
     * 箭头 y 坐标为 anchorView 顶部 - yOffset  或  anchorView 底部 + yOffset
     * @param yOffset 离 anchorView 顶部的距离
     */
    fun showAtViewUp(anchorView: View, yOffset: Int) {
        backgroundView.measure(0, 0)
        val width = backgroundView.measuredWidth
        val height = backgroundView.measuredHeight
        val anchorPosition = intArrayOf(0, 0)
        anchorView.getLocationOnScreen(anchorPosition)

        val arrowX = anchorPosition[0] + anchorView.measuredWidth / 2
        var arrowY = anchorPosition[1] - yOffset
        if (arrowY < height + statusBarHeight) {
            arrowY = anchorPosition[1] + anchorView.measuredHeight + yOffset
            backgroundView.setArrowAtTop(true)
            backgroundView.setArrowPosition(arrowX, arrowY)
            showAtLocation(anchorView, Gravity.START or Gravity.TOP, arrowX - width / 2 , arrowY)
        } else {
            backgroundView.setArrowAtTop(false)
            backgroundView.setArrowPosition(arrowX, arrowY)
            showAtLocation(anchorView, Gravity.START or Gravity.TOP, arrowX - width / 2 , arrowY - height)
        }
    }
}