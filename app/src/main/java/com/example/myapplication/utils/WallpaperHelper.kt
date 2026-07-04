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
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.doInput = true
            connection.connect()
            val input = connection.getInputStream()
            BitmapFactory.decodeStream(input)
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
