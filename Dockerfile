FROM ubuntu:22.04

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# 下载 Android SDK
RUN wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip \
    && unzip commandlinetools-linux-9477386_latest.zip \
    && mkdir -p /opt/android-sdk/cmdline-tools \
    && mv cmdline-tools /opt/android-sdk/cmdline-tools/latest

# 设置环境变量
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin

# 接受许可并安装必要的组件
RUN yes | sdkmanager --licenses \
    && sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# 设置工作目录
WORKDIR /app

# 复制项目文件
COPY . .

# 构建 APK
CMD ./gradlew assembleDebug 