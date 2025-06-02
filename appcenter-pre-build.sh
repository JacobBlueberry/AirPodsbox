#!/bin/bash

# 安装 Gradle
brew install gradle

# 设置 Gradle 环境变量
export GRADLE_HOME=/usr/local/opt/gradle/libexec
export PATH=$GRADLE_HOME/bin:$PATH

# 安装 Android SDK
brew install android-sdk

# 设置 Android SDK 环境变量
export ANDROID_HOME=/usr/local/share/android-sdk
export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH

# 接受 Android SDK 许可
yes | sdkmanager --licenses

# 安装必要的 Android SDK 组件
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" 