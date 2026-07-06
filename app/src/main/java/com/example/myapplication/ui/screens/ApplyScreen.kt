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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myapplication.R
import com.example.myapplication.ui.Screen
import com.example.myapplication.ui.theme.*

@Composable
fun ApplyScreen(
    navController: NavController,
    generatedImages: List<String>,
) {
    val context = LocalContext.current
    ApplyScreenContent(
        generatedImages = generatedImages,
        onImageRequest = { url ->
            ImageRequest.Builder(context).data(url).crossfade(true).build()
        },
        onApplyAll = {
            navController.navigate(Screen.Success.route) {
                popUpTo(Screen.Apply.route) { inclusive = true }
            }
        },
        onSkip = {
            navController.navigate(Screen.Companion.route) {
                popUpTo(Screen.Upload.route) { inclusive = true }
            }
        }
    )
}

@Composable
private fun ApplyScreenContent(
    generatedImages: List<String>,
    onImageRequest: (String) -> Any,
    onApplyAll: () -> Unit,
    onSkip: () -> Unit,
) {
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
                Text("moimoi", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("应用到设备", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_flower),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("让 Ta 陪伴你的每一天", fontSize = 14.sp, color = TextGray)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 预览区：左侧大卡（锁屏） + 右侧两小卡（桌面+小组件）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 左侧：锁屏壁纸（大）
                PreviewCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(440.dp),
                    label = "锁屏壁纸",
                    emoji = "",
                    imageUrl = generatedImages.getOrNull(1),
                    onImageRequest = onImageRequest
                )

                // 右侧：桌面 + 小组件（两个小卡垂直排列）
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PreviewCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(214.dp),
                        label = "桌面壁纸",
                        emoji = "",
                        imageUrl = generatedImages.getOrNull(0),
                        onImageRequest = onImageRequest
                    )
                    PreviewCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(214.dp),
                        label = "小组件",
                        emoji = "",
                        imageUrl = generatedImages.getOrNull(2),
                        onImageRequest = onImageRequest
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 主按钮
            Button(
                onClick = onApplyAll,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCoral)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_heart),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("应用全部", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(44.dp)
            ) {
                Text("稍后再说", fontSize = 14.sp, color = TextGray)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PreviewCard(
    modifier: Modifier = Modifier,
    label: String,
    emoji: String,
    imageUrl: String?,
    onImageRequest: (String) -> Any,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
    ) {
        if (imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(model = onImageRequest(imageUrl)),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        }
        // 已应用勾选标记（左上角）
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF9BCF53)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        }
        // 标签（底部）
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.85f))
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun ApplyScreenPreview() {
    ApplyScreenContent(
        generatedImages = listOf(
            "https://placeholder.com/800x1200",
            "https://placeholder.com/800x1200",
            "https://placeholder.com/800x600"
        ),
        onImageRequest = { it },
        onApplyAll = {},
        onSkip = {}
    )
}
