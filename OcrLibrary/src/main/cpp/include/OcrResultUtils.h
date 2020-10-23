#ifndef __OCR_RESULT_UTILS_H__
#define __OCR_RESULT_UTILS_H__

#include <jni.h>
#include "OcrStruct.h"

class OcrResultUtils {
public:
    OcrResultUtils(JNIEnv *env, OcrResult &ocrResult, jobject boxImg);

    ~OcrResultUtils();

    jobject getJObject();

private:
    JNIEnv *jniEnv;
    jobject jOcrResult;

    jclass newJListClass();

    jmethodID getListConstructor(jclass clazz);

    jobject newJPoint(cv::Point &point);

    jobject newJBox(std::vector<cv::Point> &box);

    jobject newJTextBox(jobject box, float score);

    jobject getTextBoxes(std::vector<TextBox> &textBoxes);

    jobject newJAngle(Angle &angle);

    jobject getAngles(std::vector<Angle> &angles);

    jfloatArray getJScoresArray(std::vector<float> &scores);

    jobject newJTextLine(TextLine &textLine);

    jobject getTextLines(std::vector<TextLine> &textLines);

};


#endif //__OCR_RESULT_UTILS_H__
