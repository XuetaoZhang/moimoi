package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.example.myapplication.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeGalleryScreen(
    navController: NavController,
    generatedImages: List<String>,
    onThemeSelected: (Int) -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { generatedImages.size })
    var selectedIndex by remember { mutableStateOf(0) }

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
                title = { Text("主题画廊", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "专属主题",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B4513)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "为您的 AI 伴侣挑上一件二三的装扮。",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (generatedImages.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentPadding = PaddingValues(horizontal = 40.dp)
                ) { page ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(getThemeColor(page))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(context)
                                        .data(generatedImages[page])
                                        .crossfade(true)
                                        .scale(Scale.FIT)
                                        .build()
                                ),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(generatedImages.size) { index ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index) Color(0xFF8B4513)
                                    else Color.LightGray
                                )
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无生成的图片", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ThemeTag("🕐", "时光素材")
                ThemeTag("🏺", "古金古玩")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    selectedIndex = pagerState.currentPage
                    onThemeSelected(selectedIndex)
                    navController.navigate(Screen.Success.route)
                },
                modifier = Modifier
                    .width(180.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B4513)
                )
            ) {
                Text(
                    text = "⭐ 点击应用",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ThemeTag(emoji: String, label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        modifier = Modifier.padding(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 14.sp, color = Color(0xFF8B4513))
        }
    }
}

fun getThemeColor(index: Int): Color {
    val colors = listOf(
        Color(0xFFB8E6D5),
        Color(0xFFFFE4E1),
        Color(0xFFE6E6FA)
    )
    return colors[index % colors.size]
}
