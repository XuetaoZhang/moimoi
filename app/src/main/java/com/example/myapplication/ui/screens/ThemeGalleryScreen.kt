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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.example.myapplication.R
import com.example.myapplication.ui.Screen
import com.example.myapplication.ui.theme.*

@Composable
fun ThemeGalleryScreen(
    navController: NavController,
    generatedImages: List<String>,
    onThemeSelected: (Int) -> Unit,
    onRegenerate: () -> Unit = {},
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { generatedImages.size })

    ThemeGalleryContent(
        generatedImages = generatedImages,
        pagerState = pagerState,
        onImageRequest = { url ->
            ImageRequest.Builder(context)
                .data(url)
                .crossfade(true)
                .scale(Scale.FIT)
                .build()
        },
        onRegenerate = onRegenerate,
        onApply = {
            onThemeSelected(pagerState.currentPage)
            navController.navigate(Screen.Apply.route)
        }
    )
}

@Composable
private fun ThemeGalleryContent(
    generatedImages: List<String>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onImageRequest: (String) -> Any,
    onRegenerate: () -> Unit,
    onApply: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
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

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("你的专属萌宠已生成！", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_heart),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = PrimaryCoral
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "快来看看吧~",
                    fontSize = 14.sp,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (generatedImages.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp),
                    contentPadding = PaddingValues(horizontal = 40.dp)
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(BgCard),
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = onImageRequest(generatedImages[page])),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(28.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(generatedImages.size) { index ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index) PrimaryCoral
                                    else TextGray.copy(alpha = 0.35f)
                                )
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无生成的图片", color = TextGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 重新生成按钮 (小)
            OutlinedButton(
                onClick = onRegenerate,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_refresh),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TextDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("重新生成", fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            // 主按钮
            Button(
                onClick = onApply,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCoral)
            ) {
                Text(
                    text = "下一步：应用陪伴",
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
private fun ThemeGalleryPreview() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    ThemeGalleryContent(
        generatedImages = listOf("", "", ""),
        pagerState = pagerState,
        onImageRequest = { it },
        onRegenerate = {},
        onApply = {}
    )
}
