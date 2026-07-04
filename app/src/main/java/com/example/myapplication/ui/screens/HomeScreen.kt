package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.Avatar
import com.example.myapplication.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    avatars: List<Avatar>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB4A2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🐱", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("moimoi", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Text("⚙️", fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (avatars.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(380.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha = 0.6f))
                        .border(
                            width = 2.dp,
                            color = Color(0xFFFFB4A2).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .clickable {
                            navController.navigate(Screen.Upload.route)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "➕",
                            fontSize = 48.sp,
                            color = Color(0xFFFFB4A2)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "点击创建专属形象",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                val avatar = avatars.first()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(380.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White)
                ) {
                    if (avatar.generatedImages.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                avatar.generatedImages[avatar.selectedThemeIndex]
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (avatars.isNotEmpty()) {
                val avatar = avatars.first()
                val daysCount = calculateDays(avatar.createdAt)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(120.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF0E6)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB4A2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "☀️", fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "陪你 $daysCount 天",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B4513)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "今天天气晴，就来陪我玩吧",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF8B4513)
                        )
                    ) {
                        Text("更换背景", fontSize = 14.sp)
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B4513)
                        )
                    ) {
                        Text("装扮一下", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

fun calculateDays(createdAt: Long): Int {
    val currentTime = System.currentTimeMillis()
    val diffInMillis = currentTime - createdAt
    val daysCount = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    return if (daysCount == 0) 1 else daysCount
}
