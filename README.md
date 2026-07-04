# Moimoi - AI形象助手

一个基于AI生成技术的虚拟形象陪伴应用，让思念与陪伴无处不在。

## 项目概述

用户上传照片后，AI生成专属的Q版虚拟形象，可以应用到手机的各个场景中，实现7x24小时的虚拟陪伴。

## 已实现功能

### ✅ 核心功能

1. **首页 (HomeScreen)**
   - 展示已创建的虚拟形象
   - 显示陪伴天数统计
   - 点击创建新形象入口
   - 更换背景和装扮按钮（UI已实现）

2. **上传照片页面 (UploadScreen)**
   - 支持从相册选择照片
   - 照片预览功能
   - 隐私保护提示
   - 下一步按钮导航到AI生成流程

3. **AI生成加载页面 (LoadingScreen)**
   - 显示加载进度动画
   - 展示AI生成状态
   - 风格选项展示（色彩斑斓、渐变写实）
   - 自动跳转到主题画廊

4. **主题画廊页面 (ThemeGalleryScreen)**
   - 横向滑动查看生成的多个形象
   - 卡片式展示设计
   - 主题标签（时光素材、古金古玩）
   - 点击应用按钮保存选择

5. **同步成功页面 (SuccessScreen)**
   - 成功提示动画
   - 返回首页导航

### ✅ 技术实现

- **架构**: MVVM架构模式
- **UI框架**: Jetpack Compose
- **导航**: Navigation Compose
- **网络请求**: Retrofit + OkHttp
- **图片加载**: Coil
- **状态管理**: StateFlow + ViewModel
- **权限管理**: Accompanist Permissions

### ✅ AI集成

- OpenAI兼容API集成
- 基于用户上传照片生成Q版形象
- 生成多个不同状态的形象（站立、打招呼、睡觉等）
- 使用PRD文档中提供的API配置：
  - Base URL: https://api.openai-next.com
  - Model: gpt-image-1
  - API Key: 已集成到代码中

## 未完全实现的功能

### ⚠️ 部分实现

1. **系统级集成（技术限制）**
   - ❌ App图标替换 - 需要自定义Launcher或第三方启动器支持
   - ❌ 锁屏界面替换 - 需要系统级权限或锁屏应用
   - ⚠️ 壁纸设置 - 代码中已添加权限，但需要用户手动确认
   - ❌ 来电/通知形象 - 需要系统级权限和复杂的系统集成

2. **多形象管理**
   - ✅ 单个形象的创建和保存
   - ❌ 多形象切换和管理界面
   - ❌ 形象的编辑和删除功能

3. **高级功能**
   - ❌ 根据季节/时间自动切换形象服饰
   - ❌ 天气联动
   - ❌ 电量状态联动
   - ❌ 动态壁纸（Live Wallpaper）

## 需要您做的事情

### 1. 构建和运行

```bash
cd /Users/zhangxuetao/miaow/miaowapp
./gradlew assembleDebug
```

或在Android Studio中打开项目并运行。

### 2. 测试AI生成功能

- 上传一张清晰的人物或宠物照片
- 等待AI生成（可能需要30-60秒）
- 如果API返回失败，应用会使用占位符图片

### 3. 系统集成功能（需要额外工作）

由于Android系统限制，以下功能需要额外的工作：

#### 设置壁纸
在`ThemeGalleryScreen.kt`或`SuccessScreen.kt`中可以添加：

```kotlin
val wallpaperManager = WallpaperManager.getInstance(context)
// 需要下载图片到本地，然后设置
wallpaperManager.setBitmap(bitmap)
```

#### App图标替换
需要：
- 创建自定义Launcher应用
- 或使用快捷方式API创建替代图标
- 或引导用户使用第三方启动器（如Nova Launcher）

#### 锁屏和通知
需要：
- 开发独立的锁屏应用
- 或创建通知监听服务

### 4. 优化建议

1. **数据持久化**: 当前形象数据存储在内存中，关闭应用后会丢失。建议添加：
   - Room数据库或DataStore
   - SharedPreferences保存形象列表

2. **错误处理**: 添加更完善的网络错误提示和重试机制

3. **图片缓存**: 将生成的图片保存到本地存储

4. **权限请求**: 在运行时请求相机、存储等权限

## 项目结构

```
app/src/main/java/com/example/myapplication/
├── MainActivity.kt                 # 主Activity和导航配置
├── api/
│   └── OpenAIService.kt           # AI生成API服务
├── data/
│   └── Avatar.kt                  # 数据模型
├── ui/
│   ├── Navigation.kt              # 路由定义
│   ├── screens/
│   │   ├── HomeScreen.kt          # 首页
│   │   ├── UploadScreen.kt        # 上传页面
│   │   ├── LoadingScreen.kt       # 加载页面
│   │   ├── ThemeGalleryScreen.kt  # 主题画廊
│   │   └── SuccessScreen.kt       # 成功页面
│   └── theme/                     # 主题配置
├── viewmodel/
│   └── AvatarViewModel.kt         # 业务逻辑
└── utils/
    └── ImageUtils.kt              # 图片处理工具

## 依赖库

- androidx.compose.*: Jetpack Compose UI框架
- androidx.navigation:navigation-compose: 导航
- io.coil-kt:coil-compose: 图片加载
- com.squareup.retrofit2: 网络请求
- com.google.accompanist:accompanist-permissions: 权限管理
- androidx.lifecycle:lifecycle-viewmodel-compose: ViewModel

## 权限要求

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.SET_WALLPAPER" />
```

## API配置

已在代码中配置：
- Base URL: `https://api.openai-next.com/`
- API Key: `sk-07J0p9ER0VGTcUdB228a699e6e07444bB43544CdCfA840D9`
- Model: `gpt-image-1`

## 演示流程

1. 启动应用 -> 看到空白形象卡片
2. 点击"创建专属形象" -> 跳转到上传页面
3. 点击相机图标上传照片 -> 选择图片
4. 点击"下一步：AI伴侣生成" -> 进入加载页面
5. 等待AI生成（自动） -> 跳转到主题画廊
6. 滑动查看生成的形象 -> 选择喜欢的
7. 点击"点击应用" -> 跳转到成功页面
8. 点击"返回首页" -> 看到已创建的形象

## 注意事项

1. **网络要求**: 需要能访问OpenAI API的网络环境
2. **API配额**: 注意API调用次数限制
3. **图片大小**: 建议上传的图片不要太大，避免内存问题
4. **兼容性**: 最低支持Android 10 (API 29)

## 后续开发建议

1. 实现数据持久化
2. 添加多形象管理
3. 实现壁纸设置功能
4. 开发配套的自定义Launcher
5. 添加形象编辑功能
6. 实现社交分享功能
7. 添加形象动画效果
