package com.example.qrcodescanner.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt

class ScannerOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val scrimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#00000032".toColorInt()
    }


    private val cornerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#8B5CF6".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val cornerPath = Path()


    val boxRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()


        val boxSize = Math.min(width, height) * 0.55f

        val left = (width - boxSize) / 2
        val top = (height - boxSize) / 2
        val right = left + boxSize
        val bottom = top + boxSize


        boxRect.set(left, top, right, bottom)


        val path = Path().apply {

            fillType = Path.FillType.WINDING


            addRect(0f, 0f, width, height, Path.Direction.CW)


            addRoundRect(
                left+3, top+3, right-3, bottom-3,
                12f, 12f,
                Path.Direction.CCW
            )
        }


        canvas.drawPath(path, scrimPaint)



        val cornerLength = boxSize * 0.12f
        val cornerRadius = 24f

        cornerPath.reset()


        cornerPath.moveTo(left, top + cornerLength)
        cornerPath.lineTo(left, top + cornerRadius)
        cornerPath.quadTo(left, top, left + cornerRadius, top)
        cornerPath.lineTo(left + cornerLength, top)


        cornerPath.moveTo(right - cornerLength, top)
        cornerPath.lineTo(right - cornerRadius, top)
        cornerPath.quadTo(right, top, right, top + cornerRadius)
        cornerPath.lineTo(right, top + cornerLength)


        cornerPath.moveTo(left, bottom - cornerLength)
        cornerPath.lineTo(left, bottom - cornerRadius)
        cornerPath.quadTo(left, bottom, left + cornerRadius, bottom)
        cornerPath.lineTo(left + cornerLength, bottom)


        cornerPath.moveTo(right - cornerLength, bottom)
        cornerPath.lineTo(right - cornerRadius, bottom)
        cornerPath.quadTo(right, bottom, right, bottom - cornerRadius)
        cornerPath.lineTo(right, bottom - cornerLength)


        canvas.drawPath(cornerPath, cornerPaint)
    }

    fun updateScrimColor(colorHex: String) {
        scrimPaint.color = colorHex.toColorInt()
        invalidate()
    }
}