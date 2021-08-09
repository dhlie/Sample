package com.dhl.base.ui.recyclerview

import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.dhl.base.ContextHolder
import com.dhl.base.R
import com.dhl.base.dp

/**
 *
 * Author: duanhaoliang
 * Create: 2021/4/12 10:08
 * Description:
 *
 */
class RecyclerViewDividerDecoration(
    private val horPadding: Int = 16.dp,
    private val height: Int = 0.5f.dp,
    color: Int = ContextHolder.appContext.getColor(R.color.line_bg)
) : RecyclerView.ItemDecoration() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = color
        paint.style = Paint.Style.FILL
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager ?: return
        val left = horPadding
        val right = parent.width - horPadding
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val cy = layoutManager.getDecoratedBottom(child)
            val top = (cy - height / 2f).toInt()
            val bottom = top + height
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }

    }
}