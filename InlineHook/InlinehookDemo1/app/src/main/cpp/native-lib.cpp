#include <jni.h>
#include <string>
#include <android/log.h>
#include <dlfcn.h>

#include "include/inlineHook.h"


const char *TAG = "xiao_ya";

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))


bool hook(const char *str) {
    if (strstr(str, "windaa")) {
        LOGD("hook success");
        return true;
    }
    LOGE("hook FAILURE----");
    return false;
}

void startHook() {
    LOGD("invoke startHook()");
    void *libc = dlopen("libc.so", RTLD_NOW);
    if (libc) {
        LOGE("获取 libc 失败");
    }
    LOGD("获取 libc 成功");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_jar_inlinehookdemo1_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string tempStr = "I test inline hook----->";

    const char *temp = "asdhfk";
    // hook(temp);
    startHook();
    return env->NewStringUTF(tempStr.c_str());
}


extern "C"
JNIEXPORT void JNICALL
Java_com_jar_inlinehookdemo1_MainActivity_testHookTwo(JNIEnv *env, jobject thiz) {
    startHook();
}