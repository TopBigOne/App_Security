#include <jni.h>
#include <string>
#include <dlfcn.h>
#include <android/log.h>
#include "include/inlineHook.h"

const char *TAG = "xiao_ya";

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))


void *(*oldstr)(char *, char *);

void *newstr(char *a, char *b) {
    LOGI("%s%s", a, b);
    int c = 1;
    return &c;
}

bool hook(char *str) {
    LOGI("invoke hook()");
    if (strstr(str, "windaa")) {
        LOGI("hook success");
        return true;
    }
    LOGE("hook FAILURE----讨厌讨厌讨厌");
    return false;
}

void startHook() {
    LOGI("invoke startHook()");
    void *libc = dlopen("libc.so", RTLD_NOW);
    if (libc == nullptr) {
        LOGE("获取 libc 失败");
        return;
    }
    LOGI("获取 libc 成功-666");
    void *str_addr = dlsym(libc, "strstr");
    if (registerInlineHook((uint32_t) str_addr, (uint32_t) newstr, (uint32_t **) oldstr) !=
        ELE7EN_OK) {
        LOGE("registerInlineHook failure");
        return;
    }

    if (inlineHook((uint32_t) str_addr) == ELE7EN_OK) {
        LOGI("registerInlineHook hook success");
    }

    LOGE("0000000000000000");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_jar_inlinehook_inlinehookdemothree_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    hook("fdad");
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT void JNICALL
Java_com_jar_inlinehook_inlinehookdemothree_MainActivity_testHookTwo(JNIEnv *env, jobject thiz) {
    startHook();
}