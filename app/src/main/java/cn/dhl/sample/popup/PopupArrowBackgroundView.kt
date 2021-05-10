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

    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xff000000.toInt() }
    private val arrowPath = Path()
    private var arrowAtTop = true
    //箭头离边缘的最小距离
    private val arrowMinMargin: Int get() = roundCornerRadius + 0.dp

    private var arrowWidth = 16.dp
    private var arrowHeight = 8.dp
    private var arrowXOnScreen = 0
    private var arrowYOnScreen = 0
    //背景圆角半径
    var roundCornerRadius: Int = 24
    //左右边距
    var horMargin = 4.dp
        set(value) {
            field = value
            setPadding()
        }

    init {
        setArrowAtTop(true)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setColor(color: Int) {
        arrowPaint.color = color
    }

    fun setArrowSize(width: Int, height: Int) {
        arrowWidth = width
        arrowHeight = height
    }

    fun setArrowPosition(x: Int, y: Int) {
        arrowXOnScreen = x
        arrowYOnScreen = y
        arrowPath.reset()
    }

    fun setArrowAtTop(atTop: Boolean) {
        arrowAtTop = atTop
        setPadding()
    }

    private fun setPadding() {
        if (arrowAtTop) {
            setPadding(horMargin, arrowHeight, horMargin, 0)
        } else {
            setPadding(horMargin, 0, horMargin, arrowHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        getChildAt(0)?.let {
            setMeasuredDimension(measuredWidth, it.measuredHeight + arrowHeight)
        }
    }

    private fun initTrianglePath() {
        if (!arrowPath.isEmpty) {
            return
        }
        val intArr = intArrayOf(0, 0)
        getLocationOnScreen(intArr)
        var anchorCenterX = arrowXOnScreen.toFloat() - intArr[0]
        val widthSegments = 32 //宽度平分32份
        val heightSegments = 16 //高度平分 16份
        val quadSegments = 3 //通过该值调整箭头圆弧大小
        val arrowWidth = arrowWidth.toFloat()
        val arrowHeight = arrowHeight.toFloat()
        val widthSegmentLength = arrowWidth / widthSegments //宽度每份的长度
        val heightSegmentLength = arrowHeight / heightSegments //高度每份的长度
        anchorCenterX = max(paddingStart + arrowMinMargin + quadSegments * widthSegmentLength + arrowWidth / 2, min(anchorCenterX, measuredWidth - paddingEnd - arrowMinMargin.toFloat() - quadSegments * widthSegmentLength - arrowWidth / 2))
        val xStart = anchorCenterX - (arrowWidth / 2 + quadSegments * widthSegmentLength)
        arrowPath.apply {
            reset()
            val quadWidth = widthSegmentLength * quadSegments
            val quadHeight = heightSegmentLength * quadSegments
            if (arrowAtTop) {
                moveTo(xStart, arrowHeight)
                quadTo(xStart + quadWidth, arrowHeight, xStart + quadWidth * 2, arrowHeight - quadHeight)
                lineTo(anchorCenterX - quadWidth, quadHeight)
                quadTo(anchorCenterX, 0f, anchorCenterX + quadWidth, quadHeight)
                lineTo(anchorCenterX + (widthSegments / 2 - quadSegments) * widthSegmentLength, arrowHeight - quadHeight)
                quadTo(anchorCenterX + widthSegments / 2 * widthSegmentLength, arrowHeight, anchorCenterX + (widthSegments / 2 + quadSegments) * widthSegmentLength, arrowHeight)
                close()
            } else {
                moveTo(xStart, measuredHeight - arrowHeight)
                quadTo(xStart + quadWidth, measuredHeight - arrowHeight, xStart + quadWidth * 2, measuredHeight - arrowHeight + quadHeight)
                lineTo(anchorCenterX - quadWidth, measuredHeight - quadHeight)
                quadTo(anchorCenterX, measuredHeight.toFloat(), anchorCenterX + quadWidth, measuredHeight - quadHeight)
                lineTo(anchorCenterX + (widthSegments / 2 - quadSegments) * widthSegmentLength, measuredHeight - arrowHeight + quadHeight)
                quadTo(anchorCenterX + widthSegments / 2 * widthSegmentLength, measuredHeight - arrowHeight, anchorCenterX + (widthSegments / 2 + quadSegments) * widthSegmentLength, measuredHeight - arrowHeight)
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
        initTrianglePath()
        canvas.drawPath(arrowPath, arrowPaint)
    }

    private fun drawBackground(canvas: Canvas) {
        if (arrowAtTop) {
            canvas.drawRoundRect(paddingStart.toFloat(), arrowHeight.toFloat(), (measuredWidth - paddingEnd).toFloat(), measuredHeight.toFloat(), roundCornerRadius.toFloat(), roundCornerRadius.toFloat(), arrowPaint)
        } else {
            canvas.drawRoundRect(paddingStart.toFloat(), 0f, (measuredWidth - paddingEnd).toFloat(), (measuredHeight - arrowHeight).toFloat(), roundCornerRadius.toFloat(), roundCornerRadius.toFloat(), arrowPaint)
        }
    }
}