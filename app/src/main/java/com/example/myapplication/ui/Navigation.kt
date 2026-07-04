package com.example.myapplication.ui

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Upload : Screen("upload")
    object Loading : Screen("loading")
    object ThemeGallery : Screen("theme_gallery")
    object Success : Screen("success")
    object SystemIntegration : Screen("system_integration")
}
