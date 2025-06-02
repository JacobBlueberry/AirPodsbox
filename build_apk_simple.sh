#!/bin/bash

# 创建临时目录
TEMP_DIR=$(mktemp -d)
cd "$TEMP_DIR"

# 下载 Android SDK
curl -L -o sdk.zip https://dl.google.com/android/repository/commandlinetools-mac-9477386_latest.zip
unzip sdk.zip

# 设置环境变量
export ANDROID_HOME="$TEMP_DIR/android-sdk"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin"

# 接受许可
yes | sdkmanager --licenses

# 安装必要的组件
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

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