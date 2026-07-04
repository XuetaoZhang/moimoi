#!/bin/bash

# Moimoi 安装脚本

echo "🔍 检查手机连接状态..."
adb devices

echo ""
echo "如果上面显示 'List of devices attached' 但没有设备："
echo ""
echo "📱 请在手机上："
echo "1. 开启开发者模式："
echo "   设置 → 关于手机 → 连续点击'版本号'7次"
echo ""
echo "2. 开启USB调试："
echo "   设置 → 系统 → 开发者选项 → USB调试（打开）"
echo ""
echo "3. 用USB线连接手机到电脑"
echo "   手机会弹出提示，点击'允许'"
echo ""
echo "4. 然后重新运行此脚本"
echo ""
echo "================================================"
echo ""

# 检查是否有设备
DEVICE_COUNT=$(adb devices | grep -w "device" | wc -l)

if [ $DEVICE_COUNT -eq 0 ]; then
    echo "❌ 未检测到设备"
    echo ""
    echo "建议使用微信/QQ传输APK到手机："
    echo "文件位置：app/build/outputs/apk/debug/app-debug.apk"
    exit 1
else
    echo "✅ 检测到设备，开始安装..."
    adb install -r app/build/outputs/apk/debug/app-debug.apk

    if [ $? -eq 0 ]; then
        echo ""
        echo "🎉 安装成功！"
        echo "现在可以在手机上打开 moimoi 应用了"
    else
        echo ""
        echo "❌ 安装失败"
        echo "请尝试微信/QQ传输方式"
    fi
fi
