# ChOcrLiteAndroidOnnx

#### Demo APK下载
[Gitee下载](https://gitee.com/benjaminwan/ch-ocr-lite-android-onnx/releases)

#### 介绍
Chineseocr Lite Android Onnx To Ncnn Demo，超轻量级中文OCR Android Demo，支持ncnn推理 (DBNet+AngleNet+CRNN)
把onnx模型格式转为ncnn格式，并使用ncnn推理框架进行OCR识别。
**代码和模型均源自chineseocr lite的onnx分支**

详情请查看 [https://github.com/ouyanghuiyu/chineseocr_lite](https://github.com/ouyanghuiyu/chineseocr_lite)

采用ncnn神经网络前向计算框架[https://github.com/Tencent/ncnn](https://github.com/Tencent/ncnn)

整合了如下算法：
dbnet(图像分割)+anglenet(文字方向检测)+crnn(文字识别)
#### 说明
1. 封装为独立的Library。
2. Native层以C++编写。
3. demo app以Kotlin-JVM编写。
4. 内置的ncnn预编译库版本ncnn-android-lib.zip 20200916 b766c8c
5. 内置的opencv动态库版本opencv-3.4.10-android-sdk.zip

#### 编译说明
1.  AndroidStudio 4.0.2或以上
2.  NDK
3.  cmake 3.4.1或以上

#### master分支PseNet版本
[ChOcrLiteAndroidPseNet](https://github.com/benjaminwan/ChOcrLiteAndroidPseNet)

####  master分支DBNet版本
[ChOcrLiteAndroidDBNet](https://github.com/benjaminwan/ChOcrLiteAndroidDBNet)

#### Android各版本区别(仅供参考)
1. 模型总大小对比：未压缩的情况下，PseNet版(27.3M) > DBNet版(23M) > onnx版(4.72M)。
2. PseNet与DBNet版本仅图像分割的算法不同，文字方向检测与文字识别部分完全相同。分割速度方面，DBNet版本稍微快一点点，分割效果方面，PseNet效果稍好一点点。
3. OnnxToNcnn版本与其它两个版本最大不同在于模型超轻量，当然总体的效果就不如其它两个版本，没有专门针对竖向文字的模型，所以对竖向文字的识别效果比其它两个版本差(比如春联)。速度方面与DBNet版本差不多。
