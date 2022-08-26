#include <jni.h>
#include <string>

#include <android/log.h>
#include "sandhook_native.h"


const char *TAG = "xiao_ya";

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))

bool hook(char *str) {
    if (strstr(str, "windaa")) {
        LOGI("hook in success");
        return true;
    }
    LOGE(" hook in failure");
    return false;

}

void *(*old_strstr)(char *, char *);

void *newstr(char *a, char *b) {
    LOGD("newstr 的参数: a : %s b : %s", a, b);
    int c = 100;
    return &c;
}

void startHook() {
    const char *libc = "/system/lib64/libc.so";
    auto old_strstr = reinterpret_cast<void *(*)(char *, char *)>(SandInlineHookSym(libc, "strstr2",
                                                                                    reinterpret_cast<void *>(newstr)));
    if (old_strstr != nullptr) {
        LOGI("startHook in success.");
    }


}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_jar_inlinehook_hookone_MainActivity_testHook_1strstr(JNIEnv *env, jobject thiz) {
    startHook();

    std::string hello = "筱雅—🌸树下的约定";
    hook("asdhfk");

    return env->NewStringUTF(hello.c_str());
}