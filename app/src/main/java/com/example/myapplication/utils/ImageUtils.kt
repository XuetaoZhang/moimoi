package com.example.myapplication.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return null

            // 压缩图片尺寸到512px以内（AI只需要识别特征，不需要高清）
            val maxSize = 512
            val scaledBitmap = if (originalBitmap.width > maxSize || originalBitmap.height > maxSize) {
                val scale = maxSize.toFloat() / maxOf(originalBitmap.width, originalBitmap.height)
                val newWidth = (originalBitmap.width * scale).toInt()
                val newHeight = (originalBitmap.height * scale).toInt()
                val scaled = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                originalBitmap.recycle()
                scaled
            } else {
                originalBitmap
            }

            val outputStream = ByteArrayOutputStream()
            // 进一步降低质量到60（AI识别不需要高质量）
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val byteArray = outputStream.toByteArray()
            scaledBitmap.recycle()

            android.util.Log.d("ImageUtils", "Base64 size: ${byteArray.size / 1024}KB")

            "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun extractImageUrls(content: String): List<String> {
        val urlPattern = Regex("""https?://[^\s)]+\.(?:jpg|jpeg|png|gif|webp)""")
        return urlPattern.findAll(content).map { it.value }.toList()
    }
}
