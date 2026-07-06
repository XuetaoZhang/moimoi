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
import com.example.myapplication.utils.WidgetHelper
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

    val generatedImages by viewModel.generatedImages.collectAsState()
    val selectedImageUrl by viewModel.selectedImageUrl.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val progressText by viewModel.progressText.collectAsState()
    val progressPercent by viewModel.progressPercent.collectAsState()
    val generationError by viewModel.generationError.collectAsState()

    val hasAvatar = remember { WidgetHelper.getAvatarBitmap(context) != null }
    val startDestination = if (hasAvatar) Screen.Companion.route else Screen.Upload.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Upload.route) {
            UploadScreen(
                navController = navController,
                onImageSelected = { uri -> viewModel.setSelectedImage(uri) }
            )
        }

        composable(Screen.Loading.route) {
            LaunchedEffect(Unit) {
                viewModel.generateAvatarImages(context)
            }

            LoadingScreen(
                isLoading = isLoading,
                progressText = progressText,
                progressPercent = progressPercent,
                selectedImageUri = selectedImageUri,
                error = generationError,
                onComplete = {
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
                onThemeSelected = { index -> viewModel.saveAvatar(index) },
                onRegenerate = { viewModel.generateAvatarImages(context) }
            )
        }

        composable(Screen.Apply.route) {
            ApplyScreen(
                navController = navController,
                generatedImages = generatedImages
            )
        }

        composable(Screen.Success.route) {
            SuccessScreen(
                navController = navController,
                selectedImageUrl = selectedImageUrl,
                generatedImages = generatedImages
            )
        }

        composable(Screen.Companion.route) {
            CompanionScreen(navController = navController)
        }
    }
}
