#include <jni.h>
#include <string>
#include <dlfcn.h>
#include <android/log.h>

const char *TAG = "xiao_ya";

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))



extern "C" JNIEXPORT jstring JNICALL
Java_com_jar_inlinehook_inlinehookdemofour_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT void JNICALL
Java_com_jar_inlinehook_inlinehookdemofour_MainActivity_testHookTwo(JNIEnv *env, jobject thiz) {
    // startHook();
}