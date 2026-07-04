package com.example.myapplication.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.ImageGenRequest
import com.example.myapplication.api.OpenAIService
import com.example.myapplication.data.Avatar
import com.example.myapplication.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AvatarViewModel : ViewModel() {

    private val _avatars = MutableStateFlow<List<Avatar>>(emptyList())
    val avatars: StateFlow<List<Avatar>> = _avatars

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _generatedImages = MutableStateFlow<List<String>>(emptyList())
    val generatedImages: StateFlow<List<String>> = _generatedImages

    private val _selectedImageUrl = MutableStateFlow("")
    val selectedImageUrl: StateFlow<String> = _selectedImageUrl

    // 真实加载状态——LoadingScreen 观察此值决定何时跳转
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 错误信息，非空时 LoadingScreen 显示失败提示
    private val _generationError = MutableStateFlow<String?>(null)
    val generationError: StateFlow<String?> = _generationError

    // 生成进度描述文字
    private val _progressText = MutableStateFlow("准备中...")
    val progressText: StateFlow<String> = _progressText

    fun setSelectedImage(uri: Uri) {
        _selectedImageUri.value = uri
        // 重置上次生成结果
        _generatedImages.value = emptyList()
        _generationError.value = null
    }

    /**
     * 图生图流程：直接把照片传给 grok-imagine-image-quality，生成3张不同姿态的Q版形象
     */
    fun generateAvatarImages(context: Context) {
        val uri = _selectedImageUri.value
        if (uri == null) {
            _generationError.value = "请先上传照片"
            return
        }
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _generationError.value = null
            _generatedImages.value = emptyList()

            try {
                _progressText.value = "正在读取照片..."
                val base64Image = ImageUtils.uriToBase64(context, uri)
                    ?: throw Exception("照片读取失败，请重新选择")

                _progressText.value = "AI 正在生成专属形象（1/3）..."
                val imageUrls = generateQVersionImages(base64Image)

                if (imageUrls.isEmpty()) {
                    throw Exception("未能获取生成图片，请重试")
                }

                _progressText.value = "生成成功！"
                _generatedImages.value = imageUrls
                Log.d("AvatarVM", "Generated ${imageUrls.size} images")

            } catch (e: Exception) {
                Log.e("AvatarVM", "Generation failed", e)
                _generationError.value = "生成失败：${e.message ?: "网络错误，请检查网络连接"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** 图生图：生成2张竖图(桌面/锁屏)+1张方图(小组件)，带场景背景 */
    private suspend fun generateQVersionImages(base64Image: String): List<String> {
        // 6个场景随机选一个
        val scenes = listOf(
            "日系原木风温馨客厅，暖白墙面浅木地板，布艺沙发，柔和暖光，治愈居家氛围，柔焦胶片质感，温馨暖米色调",
            "夏日城市公园草坪，翠绿乔木，远处城市天际线，明亮自然日光，清新治愈氛围，长焦浅景深",
            "秋日橙红色水杉林荫道，地面铺满落叶，暖金色秋日柔光，治愈静谧氛围，暖橙色调",
            "大片白色花田，鲜绿茎叶交错，柔和散射日光，清新治愈氛围，清透白绿色调",
            "雪后林间木屋，覆雪松枝，窗内暖黄灯光，冬日柔光，静谧治愈氛围，暖白棕褐色调",
            "黄昏海滨沙滩，天际橙粉晚霞渐变，落日柔光，浪漫静谧氛围，粉紫暖调"
        )
        val selectedScene = scenes.random()

        val tasks = listOf(
            // 竖图1：站立姿态，用于桌面壁纸
            Triple("站立微笑", "1024x1792", "自然站立，面向正前方，开心微笑，角色居中偏下，背景：$selectedScene"),
            // 竖图2：挥手姿态，用于锁屏壁纸
            Triple("挥手打招呼", "1024x1792", "举起一只手挥手打招呼，笑容灿烂，角色居中偏下，背景：$selectedScene"),
            // 方图：睡觉姿态，用于小组件
            Triple("侧卧睡觉", "1024x1024", "侧卧睡觉，眼睛微闭，表情安详可爱，角色居中，背景：$selectedScene")
        )

        val urls = mutableListOf<String>()
        for ((idx, task) in tasks.withIndex()) {
            val (postureName, size, postureDesc) = task
            _progressText.value = "AI 正在生成专属形象（${idx + 1}/3）：$postureName..."
            val prompt = "根据提供的照片，生成一个Q版卡通形象：大头小身Q版风格，头身比3:1，圆润可爱，" +
                "严格保留原照片中的外貌特征（毛色/肤色/发型/五官/表情），" +
                "姿态与场景：$postureDesc，高质量插画，角色清晰突出。"
            val request = ImageGenRequest(
                model = "grok-imagine-image-quality",
                prompt = prompt,
                image = base64Image,
                n = 1,
                size = size,
                responseFormat = "url"
            )
            val response = OpenAIService.api.generateImages(request)
            val url = response.data.firstOrNull()?.url
                ?: response.data.firstOrNull()?.b64Json?.let { "data:image/png;base64,$it" }
            if (url != null) urls.add(url)
        }
        return urls
    }

    fun saveAvatar(selectedThemeIndex: Int) {
        val uri = _selectedImageUri.value ?: return
        val images = _generatedImages.value
        if (images.isNotEmpty() && selectedThemeIndex < images.size) {
            _selectedImageUrl.value = images[selectedThemeIndex]
        }
        val newAvatar = Avatar(
            id = UUID.randomUUID().toString(),
            originalImageUri = uri.toString(),
            generatedImages = images,
            selectedThemeIndex = selectedThemeIndex,
            createdAt = System.currentTimeMillis()
        )
        _avatars.value = listOf(newAvatar) + _avatars.value
    }
}
