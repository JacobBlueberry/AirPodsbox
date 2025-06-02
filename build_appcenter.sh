#!/bin/bash

# 设置变量
APP_CENTER_TOKEN="your-token-here"
OWNER_NAME="your-username"
APP_NAME="airpods-controller"
BRANCH="main"

# 创建 zip 文件
zip -r app.zip . -x "*.git*" "*.DS_Store"

# 上传到 AppCenter
curl -X POST \
  "https://api.appcenter.ms/v0.1/apps/$OWNER_NAME/$APP_NAME/builds" \
  -H "X-API-Token: $APP_CENTER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"sourceVersion\": \"$BRANCH\",
    \"debug\": true
  }"

echo "构建已开始，请稍后访问 AppCenter 下载 APK" 