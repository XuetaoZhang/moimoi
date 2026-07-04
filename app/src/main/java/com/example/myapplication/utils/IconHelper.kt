package com.example.myapplication.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object IconHelper {

    // 常用应用包名（含国产手机各品牌变体）
    private val COMMON_APPS = listOf(
        // 电话
        listOf("com.miui.phone", "com.huawei.phone", "com.coloros.phonemanager",
               "com.vivo.phone", "com.samsung.android.dialer", "com.android.dialer") to "电话",
        // 短信
        listOf("com.android.mms", "com.miui.mms", "com.huawei.message",
               "com.coloros.mms", "com.vivo.message", "com.samsung.android.messaging") to "短信",
        // 相机
        listOf("com.android.camera", "com.android.camera2", "com.miui.camera",
               "com.huawei.camera", "com.oppo.camera", "com.vivo.camera",
               "com.samsung.android.camera") to "相机",
        // 设置
        listOf("com.android.settings", "com.miui.settings") to "设置",
        // 浏览器
        listOf("com.android.browser", "com.miui.browser", "com.huawei.browser",
               "com.android.chrome") to "浏览器"
    )

    suspend fun createCommonAppShortcuts(context: Context, iconUrl: String): List<String> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return emptyList()

        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            ?: return emptyList()

        if (!shortcutManager.isRequestPinShortcutSupported) {
            Log.w("IconHelper", "Launcher does not support pinned shortcuts")
            return emptyList()
        }

        val iconBitmap = downloadBitmap(iconUrl)
        val created = mutableListOf<String>()

        for ((packages, appName) in COMMON_APPS) {
            val pkg = packages.firstOrNull { pkgExists(context, it) } ?: continue
            val intent = context.packageManager.getLaunchIntentForPackage(pkg) ?: continue
            intent.action = Intent.ACTION_MAIN

            val builder = ShortcutInfo.Builder(context, "shortcut_${pkg}")
                .setShortLabel(appName)
                .setIntent(intent)

            if (iconBitmap != null) {
                builder.setIcon(Icon.createWithBitmap(iconBitmap))
            }

            try {
                shortcutManager.requestPinShortcut(builder.build(), null)
                created.add(appName)
            } catch (e: Exception) {
                Log.e("IconHelper", "Failed to pin shortcut for $appName", e)
            }
        }

        return created
    }

    private fun pkgExists(context: Context, pkg: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(pkg, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private suspend fun downloadBitmap(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (url.startsWith("data:image")) {
                // base64 data URI
                val base64 = url.substringAfter("base64,")
                val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } else {
                val connection = URL(url).openConnection()
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000
                BitmapFactory.decodeStream(connection.getInputStream())
            }
        } catch (e: Exception) {
            Log.e("IconHelper", "Failed to download icon bitmap", e)
            null
        }
    }

    fun openLauncherInPlayStore(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=com.teslacoilsw.launcher")
                setPackage("com.android.vending")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.teslacoilsw.launcher")
            }
            context.startActivity(intent)
        }
    }

    fun openLauncherSettings(context: Context) {
        try {
            context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
        } catch (e: Exception) {
            Log.e("IconHelper", "Cannot open launcher settings", e)
        }
    }
}
