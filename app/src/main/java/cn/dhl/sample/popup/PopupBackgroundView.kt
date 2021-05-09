package cn.dhl.sample.popup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
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
class PopupBackgroundView : View {

    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arrowPath = Path()
    private var arrowIsTop = true
    private var arrowWidth = 200f
    private var arrowHeight = 100f
    //箭头中点的 x 坐标
    private var arrowCenterX = 400
    //背景圆角半径
    private var roundCornerRadius: Float = 24f
    //箭头离边缘的最小距离
    private val arrowMinMargin: Int get() = (roundCornerRadius + 1.dp).toInt()

    init {
        arrowPaint.color = 0xff000000.toInt()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        initTrianglePath(w, h)
    }
    private fun initTrianglePath(w: Int, h: Int) {
        val widthSegments = 32 //宽度平分32份
        val heightSegments = 16 //高度平分 16份
        val quadSegments = 2 //高度平分 16份
        val widthSegmentLength = arrowWidth / widthSegments //宽度每份的长度
        val heightSegmentLength = arrowHeight / heightSegments //高度每份的长度
        var xStart = arrowCenterX - (widthSegments / 2 + quadSegments) * widthSegmentLength
        xStart = max(arrowMinMargin.toFloat(), min(xStart, measuredWidth - arrowMinMargin.toFloat()))
        arrowPath.apply {
            reset()
            if (arrowIsTop) {
                moveTo(xStart, arrowHeight)
                quadTo(xStart + widthSegmentLength * quadSegments, arrowHeight, xStart + widthSegmentLength * quadSegments * 2, arrowHeight - heightSegmentLength * quadSegments)
                lineTo(arrowCenterX - widthSegmentLength * quadSegments, heightSegmentLength * quadSegments)
                quadTo(arrowCenterX.toFloat(), 0f, arrowCenterX + widthSegmentLength * quadSegments, heightSegmentLength * quadSegments)
                lineTo(arrowCenterX + (widthSegments / 2 - quadSegments) * widthSegmentLength, arrowHeight - heightSegmentLength * quadSegments)
                quadTo(arrowCenterX + widthSegments / 2 * widthSegmentLength, arrowHeight, arrowCenterX + (widthSegments / 2 + quadSegments) * widthSegmentLength, arrowHeight)
                close()
            } else {

            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        drawTriangle(canvas)
        drawBackground(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        if (arrowIsTop) {
            canvas.drawRoundRect(0f, arrowHeight, measuredWidth.toFloat(), measuredHeight.toFloat(), roundCornerRadius, roundCornerRadius, arrowPaint)
        }
    }

    private fun drawTriangle(canvas: Canvas) {
        canvas.drawPath(arrowPath, arrowPaint)
    }
}