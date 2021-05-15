package cn.dhl.sample.popup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.widget.FrameLayout
import cn.dhl.sample.dp
import kotlin.math.max
import kotlin.math.min

/**
 *
 * Author: duanhl
 * Create: 5/9/21 11:07 PM
 * Description:
 *
 */
class PopupArrowBackgroundView : FrameLayout {

    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xff333333.toInt() }
    private val arrowPath = Path()

    //箭头在上边还是在下边
    private var arrowAtTop = true

    //箭头离边缘的最小距离, 防止和圆角重合
    private val arrowMinMargin: Int get() = roundCornerRadius + 2.dp

    private var arrowWidth = 16.dp
    private var arrowHeight = 8.dp
        get() = if (showArrow) field else 0

    //箭头顶点 x 坐标
    private var arrowXOnScreen = 0

    //箭头顶点 y 坐标
    private var arrowYOnScreen = 0

    //背景圆角半径
    var roundCornerRadius: Int = 4.dp

    //左右边距
    var horMargin = 4.dp

    //是否显示箭头
    var showArrow = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setColor(color: Int) {
        arrowPaint.color = color
    }

    /**
     * 设置箭头尺寸
     */
    fun setArrowSize(width: Int, height: Int) {
        arrowWidth = width
        arrowHeight = height
    }

    /**
     * 设置箭头顶点在屏幕中的坐标
     */
    fun setArrowPosition(x: Int, y: Int) {
        arrowXOnScreen = x
        arrowYOnScreen = y
        arrowPath.reset()
    }

    fun setArrowAtTop(atTop: Boolean) {
        arrowAtTop = atTop
    }

    private fun setPadding() {
        if (arrowAtTop) {
            setPadding(horMargin, arrowHeight, horMargin, 0)
        } else {
            setPadding(horMargin, 0, horMargin, arrowHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setPadding()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (showArrow) {
            getChildAt(0)?.let {
                setMeasuredDimension(measuredWidth, it.measuredHeight + arrowHeight)
            }
        }
    }

    private fun initTrianglePath() {
        if (!showArrow || !arrowPath.isEmpty) {
            return
        }
        val screenLocation = intArrayOf(0, 0)
        getLocationOnScreen(screenLocation)
        var arrowCenterX = arrowXOnScreen.toFloat() - screenLocation[0]
        val widthSegments = 32 //宽度平分32份
        val heightSegments = 16 //高度平分 16份
        val quadSegments = 3 //通过该值调整箭头圆弧大小
        val arrowWidth = arrowWidth.toFloat()
        val arrowHeight = arrowHeight.toFloat()
        val widthSegmentLength = arrowWidth / widthSegments //宽度每份的长度
        val heightSegmentLength = arrowHeight / heightSegments //高度每份的长度
        arrowCenterX = max(
            paddingStart + arrowMinMargin + quadSegments * widthSegmentLength + arrowWidth / 2,
            min(arrowCenterX, measuredWidth - paddingEnd - arrowMinMargin.toFloat() - quadSegments * widthSegmentLength - arrowWidth / 2)
        )
        val xStart = arrowCenterX - (arrowWidth / 2 + quadSegments * widthSegmentLength)
        arrowPath.apply {
            reset()
            val quadWidth = widthSegmentLength * quadSegments
            val quadHeight = heightSegmentLength * quadSegments
            if (arrowAtTop) {
                moveTo(xStart, arrowHeight)
                quadTo(xStart + quadWidth, arrowHeight, xStart + quadWidth * 2, arrowHeight - quadHeight)
                lineTo(arrowCenterX - quadWidth, quadHeight)
                quadTo(arrowCenterX, 0f, arrowCenterX + quadWidth, quadHeight)
                lineTo(arrowCenterX + (widthSegments / 2 - quadSegments) * widthSegmentLength, arrowHeight - quadHeight)
                quadTo(
                    arrowCenterX + widthSegments / 2 * widthSegmentLength,
                    arrowHeight,
                    arrowCenterX + (widthSegments / 2 + quadSegments) * widthSegmentLength,
                    arrowHeight
                )
                close()
            } else {
                moveTo(xStart, measuredHeight - arrowHeight)
                quadTo(xStart + quadWidth, measuredHeight - arrowHeight, xStart + quadWidth * 2, measuredHeight - arrowHeight + quadHeight)
                lineTo(arrowCenterX - quadWidth, measuredHeight - quadHeight)
                quadTo(arrowCenterX, measuredHeight.toFloat(), arrowCenterX + quadWidth, measuredHeight - quadHeight)
                lineTo(arrowCenterX + (widthSegments / 2 - quadSegments) * widthSegmentLength, measuredHeight - arrowHeight + quadHeight)
                quadTo(
                    arrowCenterX + widthSegments / 2 * widthSegmentLength,
                    measuredHeight - arrowHeight,
                    arrowCenterX + (widthSegments / 2 + quadSegments) * widthSegmentLength,
                    measuredHeight - arrowHeight
                )
                close()
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawTriangle(canvas)
        drawBackground(canvas)
        super.dispatchDraw(canvas)
    }

    private fun drawTriangle(canvas: Canvas) {
        if (!showArrow) {
            return
        }
        initTrianglePath()
        canvas.drawPath(arrowPath, arrowPaint)
    }

    private fun drawBackground(canvas: Canvas) {
        if (arrowAtTop) {
            canvas.drawRoundRect(
                paddingStart.toFloat(),
                arrowHeight.toFloat(),
                (measuredWidth - paddingEnd).toFloat(),
                measuredHeight.toFloat(),
                roundCornerRadius.toFloat(),
                roundCornerRadius.toFloat(),
                arrowPaint
            )
        } else {
            canvas.drawRoundRect(
                paddingStart.toFloat(),
                0f,
                (measuredWidth - paddingEnd).toFloat(),
                (measuredHeight - arrowHeight).toFloat(),
                roundCornerRadius.toFloat(),
                roundCornerRadius.toFloat(),
                arrowPaint
            )
        }
    }
}