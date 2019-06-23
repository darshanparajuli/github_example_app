package com.dp.githubexample.util

import android.graphics.*
import com.squareup.picasso.Transformation

class CircularImageTransformation(private val key: String) : Transformation {

    override fun key() = key

    override fun transform(source: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(result)

        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        canvas.drawCircle(
            source.width.toFloat() / 2.0f,
            source.height.toFloat() / 2.0f,
            source.width.toFloat() / 2.0f,
            paint
        )

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(source, 0.0f, 0.0f, paint)

        source.recycle()

        return result
    }
}
