# ChOcrLiteAndroidOnnx

#### APK下载
[Gitee下载](https://gitee.com/benjaminwan/ch-ocr-lite-android-onnx/releases)

#### 介绍
chineseocr lite android onnx，超轻量级中文ocr android demo，支持ncnn推理 (dbnet+anglenet+crnn)

**代码和模型均源自chineseocr lite的onnx分支**
详情请查看 [https://github.com/ouyanghuiyu/chineseocr_lite](https://github.com/ouyanghuiyu/chineseocr_lite)

采用ncnn神经网络前向计算框架[https://github.com/Tencent/ncnn](https://github.com/Tencent/ncnn)

整合了如下算法：
dbnet(图像分割)+anglenet(文字方向检测)+crnn(文字识别)

#### 架构

1. 封装为独立的Library。
2. Native层以C++编写。
3. demo app以kotlin-jvm编写。
4. 内置的ncnn预编译库版本ncnn-android-lib.zip 20200916 b766c8c
5. 内置的opencv动态库版本opencv-3.4.10-android-sdk.zip

#### 编译环境

1.  AndroidStudio 4.0或以上
2.  NDK
3.  cmake 3.4.1或以上

#### psenet版本
[ChOcrLiteAndroidPseNet](https://github.com/benjaminwan/ChOcrLiteAndroidPseNet)

#### dbnet版本
[ChOcrLiteAndroidDBNet](https://github.com/benjaminwan/ChOcrLiteAndroidDBNet)

#### 各版本区别(个人总结仅供参考)
1、模型总大小对比：未压缩的情况下，pseNet版(27.3M) > dbNet版(23M) > onnx版(4.72M)。
2、pseNet与dbNet版本仅图像分割的算法不同，文字方向检测与文字识别部分完全相同。分割速度方面，dbNet版本稍微快一点点，分割效果方面，pseNet效果稍好一点点。所以总体来说半斤八两。
3、onnx版本与其它两个版本最大不同在于模型超轻量，当然总体的效果就不如其它两个版本，没有专门针对竖向文字的模型，所以对竖向文字的识别效果比其它两个版本差。因为分割也是采用dbNet算法，所以速度方面与dbNet版本差不多。
