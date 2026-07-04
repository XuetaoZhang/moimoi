package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.Screen
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.AvatarViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MoimoiApp()
                }
            }
        }
    }
}

@Composable
fun MoimoiApp() {
    val navController = rememberNavController()
    val viewModel: AvatarViewModel = viewModel()
    val context = LocalContext.current

    val avatars by viewModel.avatars.collectAsState()
    val generatedImages by viewModel.generatedImages.collectAsState()
    val selectedImageUrl by viewModel.selectedImageUrl.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val progressText by viewModel.progressText.collectAsState()
    val generationError by viewModel.generationError.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                avatars = avatars
            )
        }

        composable(Screen.Upload.route) {
            UploadScreen(
                navController = navController,
                onImageSelected = { uri -> viewModel.setSelectedImage(uri) }
            )
        }

        composable(Screen.Loading.route) {
            // 进入此页面时触发真实 API 调用（只触发一次）
            LaunchedEffect(Unit) {
                viewModel.generateAvatarImages(context)
            }

            LoadingScreen(
                isLoading = isLoading,
                progressText = progressText,
                error = generationError,
                onComplete = {
                    // 只有 isLoading=false 且 error=null 才会触发，确保有图片后再跳转
                    if (generatedImages.isNotEmpty()) {
                        navController.navigate(Screen.ThemeGallery.route) {
                            popUpTo(Screen.Loading.route) { inclusive = true }
                        }
                    }
                },
                onRetry = {
                    viewModel.generateAvatarImages(context)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ThemeGallery.route) {
            ThemeGalleryScreen(
                navController = navController,
                generatedImages = generatedImages,
                onThemeSelected = { index -> viewModel.saveAvatar(index) }
            )
        }

        composable(Screen.Success.route) {
            SuccessScreen(
                navController = navController,
                selectedImageUrl = selectedImageUrl,
                generatedImages = generatedImages
            )
        }

        composable(Screen.SystemIntegration.route) {
            SystemIntegrationScreen(
                navController = navController,
                selectedImageUrl = selectedImageUrl
            )
        }
    }
}
