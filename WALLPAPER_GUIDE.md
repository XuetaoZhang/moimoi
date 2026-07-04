# 如何添加壁纸设置功能

## 步骤1：确保权限已添加（已完成）
在 `AndroidManifest.xml` 中已经添加了：
```xml
<uses-permission android:name="android.permission.SET_WALLPAPER" />
```

## 步骤2：在成功页面添加壁纸设置按钮

修改 `SuccessScreen.kt`，在"返回首页"按钮前添加：

```kotlin
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.utils.WallpaperHelper
import kotlinx.coroutines.launch

@Composable
fun SuccessScreen(
    navController: NavController,
    selectedImageUrl: String  // 需要传入选中的图片URL
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showToast by remember { mutableStateOf(false) }

    Box(/* ... */) {
        Column(/* ... */) {
            // ... 现有的成功提示UI ...

            // 添加壁纸设置按钮
            OutlinedButton(
                onClick = {
                    scope.launch {
                        val success = WallpaperHelper.setBothWallpapers(
                            context, 
                            selectedImageUrl
                        )
                        showToast = true
                        // 可以显示Toast提示
                    }
                },
                modifier = Modifier
                    .width(180.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("设置为壁纸", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* 返回首页 */ },
                // ... 现有的样式 ...
            ) {
                Text("返回首页")
            }
        }
    }
}
```

## 步骤3：修改导航传递图片URL

在 `MainActivity.kt` 中，修改导航到成功页面的代码：

```kotlin
// 在 ViewModel 中添加
private val _selectedImageUrl = MutableStateFlow<String?>(null)
val selectedImageUrl: StateFlow<String?> = _selectedImageUrl

fun setSelectedImageUrl(url: String) {
    _selectedImageUrl.value = url
}

// 在导航中传递
composable(Screen.Success.route) {
    val selectedUrl by viewModel.selectedImageUrl.collectAsState()
    SuccessScreen(
        navController = navController,
        selectedImageUrl = selectedUrl ?: ""
    )
}
```

## 步骤4：在主题画廊保存选中的URL

在 `ThemeGalleryScreen.kt` 中，点击"点击应用"时：

```kotlin
Button(
    onClick = {
        selectedIndex = pagerState.currentPage
        val selectedUrl = generatedImages[selectedIndex]
        onThemeSelected(selectedIndex, selectedUrl)  // 修改回调
        navController.navigate(Screen.Success.route)
    },
    // ...
) {
    Text("⭐ 点击应用")
}
```

## 完整示例

### 修改 AvatarViewModel.kt
```kotlin
fun saveAvatar(selectedThemeIndex: Int, selectedUrl: String) {
    _selectedImageUrl.value = selectedUrl
    // ... 其他保存逻辑 ...
}
```

### 修改 MainActivity.kt 导航
```kotlin
composable(Screen.ThemeGallery.route) {
    ThemeGalleryScreen(
        navController = navController,
        generatedImages = generatedImages,
        onThemeSelected = { index, url ->
            viewModel.saveAvatar(index, url)
        }
    )
}
```

## 测试步骤

1. 完成形象创建流程
2. 在成功页面点击"设置为壁纸"
3. 检查手机的主屏幕和锁屏是否更换成功

## 注意事项

- 某些设备可能需要用户在系统设置中手动授权
- 部分定制Android系统可能限制第三方应用设置壁纸
- 图片下载需要网络连接
- 建议先测试单张壁纸设置，再尝试同时设置主屏和锁屏
