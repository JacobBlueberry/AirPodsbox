#!/bin/bash

# 复制 APK 到输出目录
cp app/build/outputs/apk/debug/app-debug.apk $APPCENTER_OUTPUT_DIRECTORY/AirPodsController.apk

# 显示构建信息
echo "APK 已生成：AirPodsController.apk"
echo "APK 大小：$(du -h $APPCENTER_OUTPUT_DIRECTORY/AirPodsController.apk | cut -f1)"
echo "APK 路径：$APPCENTER_OUTPUT_DIRECTORY/AirPodsController.apk" 