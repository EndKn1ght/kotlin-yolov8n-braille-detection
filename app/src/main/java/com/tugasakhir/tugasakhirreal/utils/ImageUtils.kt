package com.tugasakhir.tugasakhirreal.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.tugasakhir.tugasakhirreal.ml.BoundingBox

object ImageUtils {
    fun drawBoundingBoxes(frame: Bitmap, boundingBoxes: List<BoundingBox>): Bitmap {
        val mutableBitmap = frame.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 40f
            isAntiAlias = true
        }
        for (box in boundingBoxes) {
            val left = box.x1 * frame.width
            val top = box.y1 * frame.height
            val right = box.x2 * frame.width
            val bottom = box.y2 * frame.height
            canvas.drawRect(left, top, right, bottom, paint)
            canvas.drawText(box.clsName, left, top - 10, textPaint)
        }
        return mutableBitmap
    }
}
