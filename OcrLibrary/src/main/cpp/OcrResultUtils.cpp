#include <OcrUtils.h>
#include "include/OcrResultUtils.h"

OcrResultUtils::OcrResultUtils(JNIEnv *env, OcrResult &ocrResult, jobject boxImg) {
    jniEnv = env;

    jclass jOcrResultClass = env->FindClass("com/benjaminwan/ocrlibrary/OcrResult");

    if (jOcrResultClass == NULL) {
        LOGE("OcrResult class is null");
    }

    jmethodID jOcrResultConstructor = env->GetMethodID(jOcrResultClass, "<init>",
                                                       "(Ljava/util/ArrayList;DLjava/util/ArrayList;Ljava/util/ArrayList;Landroid/graphics/Bitmap;DLjava/lang/String;)V");

    jobject textBoxes = getTextBoxes(ocrResult.textBoxes);

    jdouble textBoxesTime = (jdouble) ocrResult.textBoxesTime;

    jobject angles = getAngles(ocrResult.angles);

    jobject textLines = getTextLines(ocrResult.lines);
    jdouble fullTime = (jdouble) ocrResult.fullTime;
    jstring jStrRest = jniEnv->NewStringUTF(ocrResult.strRes.c_str());

    jOcrResult = env->NewObject(jOcrResultClass, jOcrResultConstructor,
                                textBoxes, textBoxesTime,
                                angles, textLines, boxImg, fullTime, jStrRest);

    jniEnv->DeleteLocalRef(textBoxes);
    jniEnv->DeleteLocalRef(angles);
    jniEnv->DeleteLocalRef(textLines);
    jniEnv->DeleteLocalRef(jStrRest);
}

OcrResultUtils::~OcrResultUtils() {
    jniEnv = NULL;
}

jobject OcrResultUtils::getJObject() {
    return jOcrResult;
}

jclass OcrResultUtils::newJListClass() {
    jclass clazz = jniEnv->FindClass("java/util/ArrayList");
    if (clazz == NULL) {
        LOGE("ArrayList class is null");
        return NULL;
    }
    return clazz;
}

jmethodID OcrResultUtils::getListConstructor(jclass clazz) {
    jmethodID constructor = jniEnv->GetMethodID(clazz, "<init>", "()V");
    return constructor;
}

jobject OcrResultUtils::newJPoint(cv::Point &point) {
    jclass clazz = jniEnv->FindClass("com/benjaminwan/ocrlibrary/Point");
    if (clazz == NULL) {
        LOGE("Point class is null");
        return NULL;
    }
    jmethodID constructor = jniEnv->GetMethodID(clazz, "<init>", "(II)V");
    jobject obj = jniEnv->NewObject(clazz, constructor, point.x, point.y);
    return obj;
}

jobject OcrResultUtils::getTextBoxes(std::vector<TextBox> &textBoxes) {
    jclass jListClass = newJListClass();
    jmethodID jListConstructor = getListConstructor(jListClass);
    jobject jList = jniEnv->NewObject(jListClass, jListConstructor);
    jmethodID jListAdd = jniEnv->GetMethodID(jListClass, "add", "(Ljava/lang/Object;)Z");

    for (const auto &textBox : textBoxes) {
        for (auto point : textBox.box) {
            jobject jPoint = newJPoint(point);
            jniEnv->CallBooleanMethod(jList, jListAdd, jPoint);
        }
    }
    return jList;
}

jobject OcrResultUtils::newJAngle(Angle &angle) {
    jclass clazz = jniEnv->FindClass("com/benjaminwan/ocrlibrary/Angle");
    if (clazz == NULL) {
        LOGE("Angle class is null");
        return NULL;
    }
    jmethodID constructor = jniEnv->GetMethodID(clazz, "<init>", "(IFD)V");
    jobject obj = jniEnv->NewObject(clazz, constructor, angle.index, angle.score, angle.time);
    return obj;
}

jobject OcrResultUtils::getAngles(std::vector<Angle> &angles) {
    jclass jListClass = newJListClass();
    jmethodID jListConstructor = getListConstructor(jListClass);
    jobject jList = jniEnv->NewObject(jListClass, jListConstructor);
    jmethodID jListAdd = jniEnv->GetMethodID(jListClass, "add", "(Ljava/lang/Object;)Z");

    for (int i = 0; i < angles.size(); ++i) {
        jobject jAngle = newJAngle(angles[i]);
        jniEnv->CallBooleanMethod(jList, jListAdd, jAngle);
    }
    return jList;
}

jfloatArray OcrResultUtils::getJScoresArray(std::vector<float> &scores) {
    jfloatArray jScores = jniEnv->NewFloatArray(scores.size());
    jniEnv->SetFloatArrayRegion(jScores, 0, scores.size(), (jfloat *) scores.data());
    return jScores;
}

jobject OcrResultUtils::newJTextLine(TextLine &textLine) {
    jclass clazz = jniEnv->FindClass("com/benjaminwan/ocrlibrary/TextLine");
    if (clazz == NULL) {
        LOGE("TextLine class is null");
        return NULL;
    }
    jmethodID constructor = jniEnv->GetMethodID(clazz, "<init>",
                                                "(Ljava/lang/String;[FD)V");
    jstring line = jniEnv->NewStringUTF(textLine.line.c_str());
    jfloatArray array = getJScoresArray(textLine.scores);
    jobject obj = jniEnv->NewObject(clazz, constructor, line, array, (jdouble) textLine.time);
    return obj;
}

jobject OcrResultUtils::getTextLines(std::vector<TextLine> &textLines) {
    jclass jListClass = newJListClass();
    jmethodID jListConstructor = getListConstructor(jListClass);
    jobject jList = jniEnv->NewObject(jListClass, jListConstructor);
    jmethodID jListAdd = jniEnv->GetMethodID(jListClass, "add", "(Ljava/lang/Object;)Z");

    for (int i = 0; i < textLines.size(); ++i) {
        jobject jTextLine = newJTextLine(textLines[i]);
        jniEnv->CallBooleanMethod(jList, jListAdd, jTextLine);
    }

    return jList;
}