package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myapplication.R
import com.example.myapplication.ui.Screen
import com.example.myapplication.utils.WallpaperHelper
import com.example.myapplication.utils.WidgetHelper
import com.example.myapplication.ui.theme.*

@Composable
fun SuccessScreen(
    navController: NavController,
    selectedImageUrl: String = "",
    generatedImages: List<String> = emptyList()
) {
    val context = LocalContext.current

    var isApplying by remember { mutableStateOf(false) }
    var applyResult by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(generatedImages) {
        if (generatedImages.size >= 3 && applyResult == null) {
            isApplying = true
            val homeImg = generatedImages[0]
            val lockImg = generatedImages[1]
            val widgetImg = generatedImages[2]

            val homeOk = WallpaperHelper.setWallpaperFromUrl(context, homeImg)
            val lockOk = WallpaperHelper.setLockScreenWallpaper(context, lockImg)
            WidgetHelper.saveAvatarAndUpdateWidget(context, widgetImg)

            applyResult = if (homeOk && lockOk) "桌面、锁屏、小组件已同步" else "部分设置失败"
            isApplying = false
        }
    }

    SuccessScreenContent(
        isApplying = isApplying,
        applyResult = applyResult,
        generatedImageUrl = generatedImages.getOrNull(2),
        onBackToHome = {
            navController.navigate(Screen.Companion.route) {
                popUpTo(Screen.Upload.route) { inclusive = true }
            }
        }
    )
}

@Composable
private fun SuccessScreenContent(
    isApplying: Boolean,
    applyResult: String?,
    generatedImageUrl: String?,
    onBackToHome: () -> Unit,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部 logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "moimoi",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (isApplying) "正在同步到系统..." else "设置成功！",
                        fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark
                    )
                    if (!isApplying) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_heart),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = PrimaryCoral
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    if (isApplying) "桌面、锁屏、小组件设置中..." else "Ta 会一直陪伴着你哦~",
                    fontSize = 14.sp,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 圆形装饰
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .background(BgCard),
                    contentAlignment = Alignment.Center
                ) {
                    if (isApplying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(72.dp),
                            color = PrimaryCoral,
                            strokeWidth = 5.dp
                        )
                    } else if (generatedImageUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(generatedImageUrl)
                                    .crossfade(true)
                                    .build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (!isApplying) {
                // 小贴士卡片
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(BgCard)
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bulb),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "小贴士",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "长按桌面空白处 → 小组件 → 找到 moimoi → 拖到桌面即可添加陪伴小组件哦~",
                            fontSize = 12.sp,
                            color = TextGray,
                            lineHeight = 18.sp
                        )
                    }
                }

                if (applyResult != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "✓ $applyResult",
                        fontSize = 12.sp,
                        color = PrimaryCoral,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBackToHome,
                enabled = !isApplying,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryCoral,
                    disabledContainerColor = Color.LightGray.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = if (isApplying) "同步中..." else "去看看",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun SuccessScreenPreview() {
    SuccessScreenContent(
        isApplying = false,
        applyResult = "桌面、锁屏、小组件已同步",
        generatedImageUrl = "https://placeholder.com/200x200",
        onBackToHome = {}
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun SuccessScreenApplyingPreview() {
    SuccessScreenContent(
        isApplying = true,
        applyResult = null,
        generatedImageUrl = null,
        onBackToHome = {}
    )
}
