#include <jni.h>
#include <string>

#include <android/log.h>


const char *TAG = "xiao_ya";

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))


extern "C" JNIEXPORT jstring JNICALL
Java_com_jar_inlinehook_sandhooktwo_MainActivity_stringFromJNI(JNIEnv *env, jobject obj) {
    std::string hello = "Hello from C++";

    jclass cls = env->GetObjectClass(obj);
    jmethodID mid = env->GetMethodID(cls, "getPackageManager",
                                     "()Landroid/content/pm/PackageManager;");

    mid = env->GetMethodID(cls, "getPackageName", "()Ljava/lang/String;");//
    auto packageName = (jstring) env->CallObjectMethod(obj, mid);

    const char *str;
    str = env->GetStringUTFChars(packageName, nullptr);

    LOGD("package_name:  %s",str);
    env->ReleaseStringUTFChars(packageName, str);

    std::string strRc = "ok";
    if (strcmp(str, "com.fenfei.demo") != 0) {
        strRc = "OMG";
    }

    return env->NewStringUTF(strRc.c_str());
}