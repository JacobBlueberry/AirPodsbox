#!/bin/bash

# 确保脚本在错误时停止
set -e

echo "开始构建 APK..."

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "正在安装 Docker..."
    curl -fsSL https://get.docker.com | sh
fi

# 构建 Docker 镜像
echo "构建 Docker 镜像..."
docker build -t android-builder .

# 运行容器并构建 APK
echo "构建 APK..."
docker run --rm -v "$(pwd):/app" android-builder

# 复制 APK 到当前目录
echo "复制 APK 文件..."
cp app/build/outputs/apk/debug/app-debug.apk AirPodsController.apk

echo "构建完成！APK 文件已生成：AirPodsController.apk" 