package com.example.myapplication.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object LockScreenHelper {

    /**
     * Android系统限制：
     * 普通应用无法直接替换系统锁屏界面
     *
     * 可实现的替代方案：
     * 1. 设置锁屏壁纸
     * 2. 创建自定义锁屏应用（需要用户手动启用）
     * 3. 使用Always On Display (AOD) - 仅限特定设备
     */

    /**
     * 设置锁屏壁纸（已在WallpaperHelper中实现）
     */
    suspend fun setLockScreenWallpaper(context: Context, imageUrl: String): Boolean {
        return WallpaperHelper.setLockScreenWallpaper(context, imageUrl)
    }

    /**
     * 保存图片到本地，用于自定义锁屏应用
     */
    suspend fun saveAvatarForLockScreen(context: Context, imageUrl: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = downloadBitmap(imageUrl)
                if (bitmap != null) {
                    val file = File(context.filesDir, "lockscreen_avatar.png")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    file.absolutePath
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("LockScreenHelper", "Error saving avatar", e)
                null
            }
        }
    }

    private fun downloadBitmap(urlString: String): Bitmap? {
        return try {
            val url = java.net.URL(urlString)
            val connection = url.openConnection()
            connection.doInput = true
            connection.connect()
            val input = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("LockScreenHelper", "Error downloading bitmap", e)
            null
        }
    }

    /**
     * 引导用户安装自定义锁屏应用
     * 推荐应用：
     * 1. Next Lock Screen
     * 2. AcDisplay
     * 3. CM Locker
     */
    fun openLockScreenAppInStore(context: Context, packageName: String = "com.microsoft.next") {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse("market://details?id=$packageName")
                setPackage("com.android.vending")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            }
            context.startActivity(intent)
        }
    }

    /**
     * 检查设备是否支持Always On Display
     */
    fun supportsAOD(context: Context): Boolean {
        // 这个功能主要在三星、小米等设备上可用
        val manufacturer = android.os.Build.MANUFACTURER.lowercase()
        return manufacturer in listOf("samsung", "xiaomi", "oneplus", "oppo", "vivo")
    }

    /**
     * 打开AOD设置（如果支持）
     */
    fun openAODSettings(context: Context) {
        try {
            val intent = android.content.Intent().apply {
                action = android.provider.Settings.ACTION_SETTINGS
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("LockScreenHelper", "Cannot open AOD settings", e)
        }
    }
}
