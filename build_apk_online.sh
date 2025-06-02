#!/bin/bash

# 创建临时目录
TEMP_DIR=$(mktemp -d)
cd "$TEMP_DIR"

# 下载构建工具
curl -L -o build-tools.zip https://github.com/buildapk/build-tools/releases/latest/download/build-tools.zip
unzip build-tools.zip

# 复制项目文件
cp -r /Users/jacob/Documents/编程/耳机/* .

# 构建 APK
./gradlew assembleDebug

# 复制 APK 到原目录
cp app/build/outputs/apk/debug/app-debug.apk /Users/jacob/Documents/编程/耳机/AirPodsController.apk

# 清理
cd - > /dev/null
rm -rf "$TEMP_DIR"

echo "APK 已生成：AirPodsController.apk" 