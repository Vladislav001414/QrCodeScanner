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

    // Кисть для затемнения экрана вокруг области сканирования
    private val scrimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#00000032".toColorInt()
    }

    // Кисть для рисования стильных уголков сканера
    private val cornerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#8B5CF6".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val cornerPath = Path()

    // Публичное поле для получения координат рамки в Activity/Fragment
    val boxRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // 1. Рассчитываем размер центрального квадрата (55% от ширины экрана)
        val boxSize = Math.min(width, height) * 0.55f

        val left = (width - boxSize) / 2
        val top = (height - boxSize) / 2
        val right = left + boxSize
        val bottom = top + boxSize

        // Сохраняем актуальные координаты рамки
        boxRect.set(left, top, right, bottom)

        // 2. Оптимизированное затемнение экрана вокруг рамки (4 шторки)
        val path = Path().apply {
            // Задаем правило заполнения: внутренность окна останется прозрачной
            fillType = Path.FillType.WINDING

            // Добавляем весь экран (внешний контур)
            addRect(0f, 0f, width, height, Path.Direction.CW)

            // Добавляем центральное окошко со скруглением 24px (внутренний контур в обратном направлении)
            addRoundRect(
                left+3, top+3, right-3, bottom-3,
                12f, 12f,
                Path.Direction.CCW
            )
        }

// 2. В методе onDraw просто рисуем этот путь одной командой
        canvas.drawPath(path, scrimPaint)
        // Право

        // 3. Настройка геометрии уголков
        val cornerLength = boxSize * 0.12f // Длина «усиков» (12% от размера квадрата)
        val cornerRadius = 24f // Радиус скругления самого угла

        cornerPath.reset()

        // ВЕРХНИЙ ЛЕВЫЙ УГОЛ
        cornerPath.moveTo(left, top + cornerLength)
        cornerPath.lineTo(left, top + cornerRadius)
        cornerPath.quadTo(left, top, left + cornerRadius, top)
        cornerPath.lineTo(left + cornerLength, top)

        // ВЕРХНИЙ ПРАВЫЙ УГОЛ
        cornerPath.moveTo(right - cornerLength, top)
        cornerPath.lineTo(right - cornerRadius, top)
        cornerPath.quadTo(right, top, right, top + cornerRadius)
        cornerPath.lineTo(right, top + cornerLength)

        // НИЖНИЙ ЛЕВЫЙ УГОЛ
        cornerPath.moveTo(left, bottom - cornerLength)
        cornerPath.lineTo(left, bottom - cornerRadius)
        cornerPath.quadTo(left, bottom, left + cornerRadius, bottom)
        cornerPath.lineTo(left + cornerLength, bottom)

        // НИЖНИЙ ПРАВЫЙ УГОЛ
        cornerPath.moveTo(right - cornerLength, bottom)
        cornerPath.lineTo(right - cornerRadius, bottom)
        cornerPath.quadTo(right, bottom, right, bottom - cornerRadius)
        cornerPath.lineTo(right, bottom - cornerLength)

        // Отрисовываем уголки на холсте
        canvas.drawPath(cornerPath, cornerPaint)
    }

    fun updateScrimColor(colorHex: String) {
        scrimPaint.color = colorHex.toColorInt()
        invalidate() // ⚠️ КРИТИЧЕСКИ ВАЖНО: заставляет Android перерисовать View с новым цветом!
    }
}