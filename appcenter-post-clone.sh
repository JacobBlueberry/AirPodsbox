#!/bin/bash

# 安装 Java
brew install openjdk@11

# 设置 Java 环境变量
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
export PATH=$JAVA_HOME/bin:$PATH

# 构建 APK
./gradlew assembleDebug

# 复制 APK 到输出目录
cp app/build/outputs/apk/debug/app-debug.apk $APPCENTER_OUTPUT_DIRECTORY/AirPodsController.apk 