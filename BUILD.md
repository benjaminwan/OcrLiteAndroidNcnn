# 编译说明

1. AndroidStudio 2022.1.1或以上；
2. NDK下载，在SDK Tools中下载，版本选最新版；
3. cmake 3.22.1，在SDK Tools中下载；
4. 原始模型来自https://github.com/ouyanghuiyu/chineseocr_lite/tree/onnx/models_ncnn。
5. 模型目录结构为
```
  OcrLiteAndroidNcnn/OcrLibrary/src/main/assets
  ├── angle_op.bin
  ├── angle_op.param
  ├── crnn_lite_op.bin
  ├── crnn_lite_op.param
  ├── dbnet_op.bin
  ├── dbnet_op.param
  └── keys.txt
  ```

5.下载opencv-mobile-3.4.15-android.7z，[下载地址](https://gitee.com/benjaminwan/ocr-lite-android-ncnn/attach_files/843219/download/opencv-mobile-3.4.15-android.7z)
解压后目录结构为

```
OcrLiteAndroidNcnn/OcrLibrary/src/sdk
    └── native
        ├── jni
        └── staticlibs
```

6. ncnn预编译库版本为 20221128，[下载地址](https://github.com/Tencent/ncnn/releases/tag/20221128)

* 目标是编译cpu版和gpu版，所以需要下载 "不带vulkan的"和"带vulkan的"两种库。
* 但是每种库又分为静态库和动态库，所以一共就有四种库。
* 下载ncnn-20221128-android.zip或ncnn-20221128-android-shared.zip，二选一，解压到OcrLiteAndroidNcnn/OcrLibrary/src/main/ncnn
*

下载ncnn-20221128-android-vulkan.zip或ncnn-20221128-android-vulkan-shared.zip，二选一，解压到OcrLiteAndroidNcnn/OcrLibrary/src/main/ncnn-vulkan

* 解压后目录结构为

```
OcrLiteAndroidNcnn/OcrLibrary/src/main/ncnn
    ├── arm64-v8a
    ├── armeabi-v7a
    ├── x86
    └── x86_64
OcrLiteAndroidNcnn/OcrLibrary/src/main/ncnn-vulkan
    ├── arm64-v8a
    ├── armeabi-v7a
    ├── x86
    └── x86_64
```

* **注意：解压后还必须修改每个abi目录下的lib/cmake/ncnn/ncnn.cmake，注释掉此行```# INTERFACE_COMPILE_OPTIONS "-fno-rtti;-fno-exceptions"```
  一共有4个文件需要修改，否则会造成编译错误。**

### 编译Release包

* mac/linux使用命令编译```./gradlew assembleRelease```
* win使用命令编译```gradlew.bat assembleRelease```
* 输出apk文件在app/build/outputs/apk

### AndroidStudio调试启动

* 先在左侧边栏中找到"Build Variants"选项卡
* 在选项卡里，有app和OcrLibrary两项，且"Active Build Variant"可以选择CpuDebug/CpuRelease/GpuDebug/GpuRelease
* 选中需要的"Active Build Variant"，注意app和OcrLibrary必须选择相同选项，等待刷新，然后直接用工具栏的运行或调试按钮启动。
* 选中cpu版时: minSdkVersion=21，最终编译出来的apk较小
* 选中gpu版时:minSdkVersion=24，因为sdk24(Android N/7.0)启用了新的打包和签名方式，再加上vulkan支持，会增加了不少体积

### 重新编译

删除项目根目录下的如下缓存文件夹

```
.idea
build
app/build
OcrLibrary/.cxx
OcrLibrary/build
```

