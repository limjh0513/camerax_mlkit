package kr.hs.dgsw.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

class Draw(context: Context?, var rect: Rect, var text: String) : View(context) {

    lateinit var paint: Paint
    lateinit var textPaint: Paint

    init {
        init()
    }

    private fun init() {
        paint = Paint()
        paint.color = Color.RED
        paint.strokeWidth = 20f
        paint.style = Paint.Style.STROKE

        textPaint = Paint()
        textPaint.color = Color.RED
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 80f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(text, rect.centerX().toFloat(), rect.centerY().toFloat(), textPaint)
        canvas.drawRect(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), paint)
    }
}