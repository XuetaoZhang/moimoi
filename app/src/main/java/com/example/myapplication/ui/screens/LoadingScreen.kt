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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingScreen(
    isLoading: Boolean,
    progressText: String,
    error: String?,
    onComplete: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    // Only navigate after generation actually started and finished (avoids spurious call at initial isLoading=false)
    var generationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(isLoading) {
        if (isLoading) generationStarted = true
    }
    LaunchedEffect(isLoading, error) {
        if (generationStarted && !isLoading && error == null) {
            onComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5F5)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            if (error != null) {
                // ── 错误状态 ────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB4A2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("❌", fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "生成失败",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B4513)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = error,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B4513)
                    )
                ) {
                    Text("重新生成", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text("返回上传", fontSize = 16.sp)
                }

            } else {
                // ── 加载中状态 ──────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB4A2)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = Color.White,
                        strokeWidth = 6.dp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "AI 正在唤醒你的专属伙伴...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B4513)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = progressText,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "预计需要 30-60 秒，请耐心等待",
                    fontSize = 12.sp,
                    color = Color.Gray.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(60.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StyleOption("🎨", "色彩斑斓")
                    StyleOption("✨", "渐变写实")
                }
            }
        }
    }
}

@Composable
fun StyleOption(emoji: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 32.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = Color(0xFF8B4513))
    }
}
