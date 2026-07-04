package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.Screen
import com.example.myapplication.utils.IconHelper
import com.example.myapplication.utils.WallpaperHelper
import com.example.myapplication.utils.WidgetHelper
import kotlinx.coroutines.launch

@Composable
fun SuccessScreen(
    navController: NavController,
    selectedImageUrl: String = "",
    generatedImages: List<String> = emptyList()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isApplying by remember { mutableStateOf(false) }
    var applyResult by remember { mutableStateOf<String?>(null) }

    // 进入即自动分配3张图：图1桌面/图2锁屏/图3小组件
    LaunchedEffect(generatedImages) {
        if (generatedImages.size >= 3 && applyResult == null) {
            android.util.Log.d("SuccessScreen", "开始应用3张图，数量: ${generatedImages.size}")
            isApplying = true
            val homeImg = generatedImages[0]   // 竖图1 → 桌面
            val lockImg = generatedImages[1]   // 竖图2 → 锁屏
            val widgetImg = generatedImages[2] // 方图 → 小组件

            android.util.Log.d("SuccessScreen", "Widget图片URL: $widgetImg")

            val homeOk = WallpaperHelper.setWallpaperFromUrl(context, homeImg)
            val lockOk = WallpaperHelper.setLockScreenWallpaper(context, lockImg)
            WidgetHelper.saveAvatarAndUpdateWidget(context, widgetImg)

            applyResult = if (homeOk && lockOk) "✓ 桌面、锁屏、小组件已同步" else "部分设置失败"
            isApplying = false
        } else if (generatedImages.isNotEmpty()) {
            android.util.Log.w("SuccessScreen", "图片数量不足: ${generatedImages.size}")
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFFF5F5)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape).background(Color(0xFFFFB4A2)),
                contentAlignment = Alignment.Center
            ) {
                if (isApplying) {
                    CircularProgressIndicator(Modifier.size(60.dp), color = Color.White, strokeWidth = 5.dp)
                } else {
                    Text("✓", fontSize = 64.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = if (isApplying) "正在同步到系统..." else "同步成功！",
                fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B4513)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = if (isApplying) "桌面、锁屏、小组件设置中..." else "他已经住进你的手机里啦~",
                fontSize = 15.sp, color = Color.Gray
            )

            if (applyResult != null) {
                Spacer(Modifier.height(10.dp))
                Text(applyResult!!, fontSize = 13.sp, color = Color(0xFF4CAF50))
            }

            Spacer(Modifier.height(16.dp))

            if (!isApplying) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0E6))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "📌 添加桌面小组件",
                            fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF8B4513)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "长按桌面空白处 → 小组件 → 找到 moimoi → 拖到桌面",
                            fontSize = 13.sp, color = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                var shortcutDone by remember { mutableStateOf(false) }
                var shortcutRunning by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = {
                        if (!shortcutDone) scope.launch {
                            shortcutRunning = true
                            IconHelper.createCommonAppShortcuts(context, selectedImageUrl)
                            shortcutDone = true
                            shortcutRunning = false
                        }
                    },
                    enabled = !shortcutRunning && !shortcutDone,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF8B4513))
                ) {
                    if (shortcutRunning) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFF8B4513))
                    } else {
                        Text(
                            if (shortcutDone) "✓ 快捷方式已创建" else "创建桌面图标快捷方式（可选）",
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513))
            ) {
                Text("返回首页", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
