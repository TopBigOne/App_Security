#include <jni.h>
#include <string>


#include "android/log.h"

const char *TAG = "xiao_ya";

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))


bool mywin0(char *a) {
    if (strstr(a, "windaa")) {
        LOGI(" I am success");
        return true;
    }
    LOGI(" I am failed");
    return false;
}

bool mywin1(char *a) {
    return true;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_jar_plthookone_MainActivity_hookOne(JNIEnv *env, jobject thiz) {
    if (mywin0("adshfk")) {
        LOGI("mywin0 is true");
    } else {
        LOGI("mywin0 is failed");
    }
}