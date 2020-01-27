package com.camtittle.photosharing.engine.image

import android.graphics.Bitmap
import android.graphics.Matrix

object ImageUtils {

    // adapted from
    // https://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android
    fun scaleBitmapToMaxSize(bm: Bitmap, maxSize: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scalingWidth = width > height

        val scaleFactor = if (scalingWidth) {
            maxSize / width.toFloat()
        } else {
            maxSize / height.toFloat()
        }

        val matrix = Matrix()
        matrix.postScale(scaleFactor, scaleFactor)

        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()

        return resizedBitmap
    }
}