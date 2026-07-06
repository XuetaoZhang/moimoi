package com.example.myapplication.ui.screens

import android.net.Uri
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.ui.theme.*

@Composable
fun LoadingScreen(
    isLoading: Boolean,
    progressText: String,
    error: String?,
    onComplete: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    selectedImageUri: Uri? = null,
    progressPercent: Int = 0,
) {
    var generationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(isLoading) {
        if (isLoading) generationStarted = true
    }
    LaunchedEffect(isLoading, error) {
        if (generationStarted && !isLoading && error == null) {
            onComplete()
        }
    }

    LoadingScreenContent(
        isError = error != null,
        errorText = error,
        progressText = progressText,
        progressPercent = progressPercent,
        selectedImageUri = selectedImageUri,
        onRetry = onRetry,
        onBack = onBack,
    )
}

@Composable
private fun LoadingScreenContent(
    isError: Boolean,
    errorText: String?,
    progressText: String,
    progressPercent: Int,
    selectedImageUri: Uri?,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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

            if (isError) {
                Spacer(modifier = Modifier.height(60.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(PrimaryCoral),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("❌", fontSize = 48.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("生成失败", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        errorText ?: "",
                        fontSize = 14.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = onRetry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCoral)
                    ) {
                        Text("重新生成", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Text("返回上传", fontSize = 16.sp, color = TextDark)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("上传成功", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🎉", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "正在为你生成 Q 萌形象...",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 照片展示
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .height(340.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(BgCard),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(28.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("📷", fontSize = 64.sp)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 进度条
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🐾", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "AI 正在努力创作中...",
                            fontSize = 14.sp,
                            color = TextDark,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${progressPercent}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCoral
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PrimaryCoral,
                        trackColor = BgCard,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        progressText,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "预计需要 30-60 秒，请耐心等待",
                    fontSize = 12.sp,
                    color = TextGray.copy(alpha = 0.8f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun LoadingScreenPreview() {
    LoadingScreenContent(
        isError = false,
        errorText = null,
        progressText = "正在生成第 2 张 · 挥手打招呼",
        progressPercent = 65,
        selectedImageUri = null,
        onRetry = {},
        onBack = {}
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun LoadingScreenErrorPreview() {
    LoadingScreenContent(
        isError = true,
        errorText = "网络错误，请检查网络连接后重试。",
        progressText = "",
        progressPercent = 0,
        selectedImageUri = null,
        onRetry = {},
        onBack = {}
    )
}
