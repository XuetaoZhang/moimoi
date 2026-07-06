package com.example.myapplication.ui

sealed class Screen(val route: String) {
    object Upload : Screen("upload")
    object Loading : Screen("loading")
    object ThemeGallery : Screen("theme_gallery")
    object Apply : Screen("apply")
    object Success : Screen("success")
    object Companion : Screen("companion")
}
