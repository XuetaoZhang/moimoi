package com.example.myapplication.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.myapplication.widget.PetWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

object WidgetHelper {

    private const val PREFS_NAME = "pet_widget_prefs"
    private const val KEY_CREATED_AT = "created_at"
    private const val AVATAR_FILE_NAME = "pet_avatar.jpg"

    suspend fun saveAvatarAndUpdateWidget(context: Context, imageUrl: String) {
        Log.d("WidgetHelper", "开始保存头像: $imageUrl")
        withContext(Dispatchers.IO) {
            try {
                // 下载图片
                Log.d("WidgetHelper", "正在下载图片...")
                val bitmap = downloadBitmap(imageUrl)
                if (bitmap == null) {
                    Log.e("WidgetHelper", "下载图片失败: bitmap为null")
                    return@withContext
                }
                Log.d("WidgetHelper", "图片下载成功: ${bitmap.width}x${bitmap.height}")

                // 保存到内部存储
                val file = File(context.filesDir, AVATAR_FILE_NAME)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                Log.d("WidgetHelper", "图片已保存: ${file.absolutePath}, 大小: ${file.length()} bytes")

                // 记录创建时间
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                if (!prefs.contains(KEY_CREATED_AT)) {
                    prefs.edit().putLong(KEY_CREATED_AT, System.currentTimeMillis()).apply()
                    Log.d("WidgetHelper", "已记录创建时间")
                }

                // 触发 Widget 更新
                updateWidget(context)

            } catch (e: Exception) {
                Log.e("WidgetHelper", "保存头像失败", e)
            }
        }
    }

    fun updateWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(context, PetWidgetProvider::class.java)
        )
        if (ids.isNotEmpty()) {
            PetWidgetProvider.updateWidgets(context, appWidgetManager, ids)
            Log.d("WidgetHelper", "Updated ${ids.size} widgets")
        }
    }

    fun getAvatarBitmap(context: Context): Bitmap? {
        return try {
            val file = File(context.filesDir, AVATAR_FILE_NAME)
            if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
        } catch (e: Exception) {
            Log.e("WidgetHelper", "Failed to load avatar", e)
            null
        }
    }

    fun getDaysCount(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val createdAt = prefs.getLong(KEY_CREATED_AT, 0L)
        if (createdAt == 0L) return 1
        val daysPassed = ((System.currentTimeMillis() - createdAt) / (1000 * 60 * 60 * 24)).toInt()
        return if (daysPassed < 1) 1 else daysPassed
    }

    fun getMoodText(): String {
        val moods = listOf(
            "今日心情：晒太阳",
            "今日心情：想主人了",
            "今日心情：打瞌睡",
            "今日心情：想吃零食",
            "今日心情：探险中",
            "今日心情：慵懒午后",
            "今日心情：期待陪伴"
        )
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 6..9 -> "今日心情：精神满满"
            in 10..14 -> moods.random()
            in 15..18 -> "今日心情：慵懒午后"
            in 19..22 -> "今日心情：想主人了"
            else -> "今日心情：困了想睡"
        }
    }

    private fun downloadBitmap(url: String): Bitmap? {
        return try {
            Log.d("WidgetHelper", "Downloading image from: $url")
            val conn = URL(url).openConnection()
            conn.connectTimeout = 30_000
            conn.readTimeout = 30_000
            val originalBitmap = BitmapFactory.decodeStream(conn.getInputStream())

            if (originalBitmap == null) {
                Log.e("WidgetHelper", "Failed to decode bitmap")
                return null
            }

            Log.d("WidgetHelper", "Original bitmap size: ${originalBitmap.width}x${originalBitmap.height}")

            // 小组件压缩到更小尺寸 (600px宽度足够)
            val maxWidth = 600
            if (originalBitmap.width > maxWidth) {
                val scale = maxWidth.toFloat() / originalBitmap.width
                val newHeight = (originalBitmap.height * scale).toInt()
                val scaled = Bitmap.createScaledBitmap(originalBitmap, maxWidth, newHeight, true)
                originalBitmap.recycle()
                Log.d("WidgetHelper", "Compressed bitmap to: ${scaled.width}x${scaled.height}")
                scaled
            } else {
                originalBitmap
            }
        } catch (e: Exception) {
            Log.e("WidgetHelper", "Failed to download bitmap", e)
            null
        }
    }
}
