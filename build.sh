#!/bin/bash

# 确保脚本在错误时停止
set -e

# 检查是否安装了 Java
if ! command -v java &> /dev/null; then
    echo "请先安装 Java"
    exit 1
fi

# 检查是否安装了 Android SDK
if [ -z "$ANDROID_HOME" ]; then
    echo "请设置 ANDROID_HOME 环境变量指向 Android SDK 目录"
    exit 1
fi

# 执行构建
./gradlew assembleDebug

# 复制 APK 到当前目录
cp app/build/outputs/apk/debug/app-debug.apk AirPodsController.apk

echo "构建完成！APK 文件已生成：AirPodsController.apk" 