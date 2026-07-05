package com.example.myapplication.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object WallpaperHelper {

    suspend fun setWallpaperFromUrl(context: Context, imageUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = downloadBitmap(imageUrl)
                if (bitmap != null) {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    wallpaperManager.setBitmap(bitmap)
                    Log.d("WallpaperHelper", "Wallpaper set successfully")
                    true
                } else {
                    Log.e("WallpaperHelper", "Failed to download bitmap")
                    false
                }
            } catch (e: Exception) {
                Log.e("WallpaperHelper", "Error setting wallpaper", e)
                false
            }
        }
    }

    suspend fun setLockScreenWallpaper(context: Context, imageUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = downloadBitmap(imageUrl)
                if (bitmap != null) {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    wallpaperManager.setBitmap(
                        bitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_LOCK
                    )
                    Log.d("WallpaperHelper", "Lock screen wallpaper set successfully")
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("WallpaperHelper", "Error setting lock screen wallpaper", e)
                false
            }
        }
    }

    suspend fun setBothWallpapers(context: Context, imageUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = downloadBitmap(imageUrl)
                if (bitmap != null) {
                    val wallpaperManager = WallpaperManager.getInstance(context)

                    // Set home screen
                    wallpaperManager.setBitmap(
                        bitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_SYSTEM
                    )

                    // Set lock screen
                    wallpaperManager.setBitmap(
                        bitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_LOCK
                    )

                    Log.d("WallpaperHelper", "Both wallpapers set successfully")
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("WallpaperHelper", "Error setting both wallpapers", e)
                false
            }
        }
    }

    private fun downloadBitmap(urlString: String): Bitmap? {
        return try {
            Log.d("WallpaperHelper", "Downloading image from: $urlString")
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.connectTimeout = 30_000
            connection.readTimeout = 30_000
            connection.doInput = true
            connection.connect()
            val input = connection.getInputStream()
            val originalBitmap = BitmapFactory.decodeStream(input)

            if (originalBitmap == null) {
                Log.e("WallpaperHelper", "Failed to decode bitmap")
                return null
            }

            Log.d("WallpaperHelper", "Original bitmap size: ${originalBitmap.width}x${originalBitmap.height}")

            // 压缩到1080p以内（大部分手机分辨率）
            val maxWidth = 1080
            val maxHeight = 1920

            if (originalBitmap.width > maxWidth || originalBitmap.height > maxHeight) {
                val widthRatio = maxWidth.toFloat() / originalBitmap.width
                val heightRatio = maxHeight.toFloat() / originalBitmap.height
                val ratio = minOf(widthRatio, heightRatio)

                val newWidth = (originalBitmap.width * ratio).toInt()
                val newHeight = (originalBitmap.height * ratio).toInt()
                val scaled = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                originalBitmap.recycle()
                Log.d("WallpaperHelper", "Compressed bitmap to: ${scaled.width}x${scaled.height}")
                scaled
            } else {
                originalBitmap
            }
        } catch (e: Exception) {
            Log.e("WallpaperHelper", "Error downloading bitmap", e)
            null
        }
    }
}

// Usage example in SuccessScreen.kt or ThemeGalleryScreen.kt:
/*
val scope = rememberCoroutineScope()
val context = LocalContext.current

Button(onClick = {
    scope.launch {
        val success = WallpaperHelper.setBothWallpapers(context, selectedImageUrl)
        if (success) {
            // Show success message
        } else {
            // Show error message
        }
    }
}) {
    Text("设置为壁纸")
}
*/
