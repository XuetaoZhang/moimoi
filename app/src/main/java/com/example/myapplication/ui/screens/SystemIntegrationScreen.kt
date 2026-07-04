package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.utils.IconHelper
import com.example.myapplication.utils.LockScreenHelper
import com.example.myapplication.utils.WallpaperHelper
import kotlinx.coroutines.launch

@Composable
fun SystemIntegrationScreen(
    navController: NavController,
    selectedImageUrl: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var wallpaperStatus by remember { mutableStateOf<String?>(null) }
    var lockScreenStatus by remember { mutableStateOf<String?>(null) }
    var shortcutStatus by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "系统集成设置",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B4513)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 壁纸设置
        IntegrationCard(
            icon = "🖼️",
            title = "主屏幕壁纸",
            description = "将生成的形象设置为主屏幕壁纸",
            buttonText = "设置壁纸",
            status = wallpaperStatus,
            isProcessing = isProcessing,
            onClick = {
                scope.launch {
                    isProcessing = true
                    val success = WallpaperHelper.setWallpaperFromUrl(context, selectedImageUrl)
                    wallpaperStatus = if (success) "✓ 壁纸设置成功" else "✗ 设置失败"
                    isProcessing = false
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 锁屏壁纸
        IntegrationCard(
            icon = "🔒",
            title = "锁屏壁纸",
            description = "将形象设置为锁屏壁纸",
            buttonText = "设置锁屏",
            status = lockScreenStatus,
            isProcessing = isProcessing,
            onClick = {
                scope.launch {
                    isProcessing = true
                    val success = LockScreenHelper.setLockScreenWallpaper(context, selectedImageUrl)
                    lockScreenStatus = if (success) "✓ 锁屏壁纸设置成功" else "✗ 设置失败"
                    isProcessing = false
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 应用图标
        IntegrationCard(
            icon = "📱",
            title = "应用图标替换",
            description = "为常用应用创建带专属形象图标的桌面快捷方式",
            buttonText = "创建快捷方式",
            status = shortcutStatus,
            isProcessing = isProcessing,
            onClick = {
                scope.launch {
                    isProcessing = true
                    shortcutStatus = null
                    val created = IconHelper.createCommonAppShortcuts(context, selectedImageUrl)
                    shortcutStatus = when {
                        created.isEmpty() -> "✗ 未找到可用应用，或桌面不支持固定快捷方式"
                        else -> "✓ 已为 ${created.joinToString("、")} 创建快捷方式，请在系统弹窗中确认添加"
                    }
                    isProcessing = false
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 高级选项
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF0E6)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "💡 高级选项",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "• 安装自定义启动器以支持图标包",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                TextButton(onClick = {
                    IconHelper.openLauncherInPlayStore(context)
                }) {
                    Text("下载 Nova Launcher")
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "• 使用自定义锁屏应用",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                TextButton(onClick = {
                    LockScreenHelper.openLockScreenAppInStore(context)
                }) {
                    Text("下载锁屏应用")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("完成", fontSize = 16.sp)
        }
    }
}

@Composable
fun IntegrationCard(
    icon: String,
    title: String,
    description: String,
    buttonText: String,
    status: String?,
    isProcessing: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(buttonText)
                }
            }

            if (status != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = status,
                    fontSize = 12.sp,
                    color = if (status.contains("✓")) Color(0xFF4CAF50) else Color.Red
                )
            }
        }
    }
}
