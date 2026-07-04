package com.example.myapplication.data

data class Avatar(
    val id: String,
    val name: String = "专属形象",
    val originalImageUri: String,
    val generatedImages: List<String> = emptyList(),
    val selectedThemeIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val daysCount: Int = 0
)

data class GeneratedTheme(
    val imageUrl: String,
    val title: String,
    val description: String
)
