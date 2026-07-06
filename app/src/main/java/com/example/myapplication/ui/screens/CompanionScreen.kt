package com.example.myapplication.ui.screens

import android.graphics.Bitmap
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.ui.Screen
import com.example.myapplication.ui.theme.*
import com.example.myapplication.utils.WidgetHelper

@Composable
fun CompanionScreen(navController: NavController) {
    val context = LocalContext.current

    val bitmap = remember { WidgetHelper.getAvatarBitmap(context) }
    val days = remember { WidgetHelper.getDaysCount(context) }
    val mood = remember { WidgetHelper.getMoodText() }
    val greeting = remember { pickGreeting() }

    CompanionScreenContent(
        bitmap = bitmap,
        days = days,
        mood = mood,
        greeting = greeting,
        onRegenerate = {
            navController.navigate(Screen.Upload.route) {
                popUpTo(Screen.Companion.route) { inclusive = true }
            }
        }
    )
}

@Composable
private fun CompanionScreenContent(
    bitmap: Bitmap?,
    days: Int,
    mood: String,
    greeting: String,
    onRegenerate: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
    ) {
        // 背景形象大图
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部 logo + 重新生成入口
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("moimoi", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BgCard),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onRegenerate,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_refresh),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = TextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 顶部气泡
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 4.dp, bottomStart = 20.dp))
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(greeting, fontSize = 13.sp, color = TextDark)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 底部信息卡片
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PrimaryCoral),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_heart),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("陪伴第 ", fontSize = 13.sp, color = TextGray)
                        Text(
                            days.toString(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCoral
                        )
                        Text(" 天", fontSize = 13.sp, color = TextGray, modifier = Modifier.padding(bottom = 2.dp))
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = mood,
                        fontSize = 12.sp,
                        color = TextGray,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun pickGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val list = when (hour) {
        in 5..9 -> listOf("早呀~今天也要加油鸭！", "起床啦~陪我玩会儿嘛~")
        in 10..13 -> listOf("今天也要开心鸭~", "记得好好吃饭哦~")
        in 14..17 -> listOf("下午好呀~喝水了吗？", "陪你晒晒太阳~")
        in 18..21 -> listOf("回家啦~陪陪我嘛~", "晚上好呀~")
        else -> listOf("夜深了，早点睡呀~", "陪你一起做个好梦~")
    }
    return list.random()
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun CompanionScreenPreview() {
    CompanionScreenContent(
        bitmap = null,
        days = 23,
        mood = "今日心情：阳光正好，喵喵陪你一起发呆~",
        greeting = "今天也要开心鸭~",
        onRegenerate = {}
    )
}
