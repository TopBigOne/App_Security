//
// Created by dev on 2022/8/25.
//

#include <stdio.h>

#include <android/log.h>
#include "include/inlineHook.h"


#include <android/log.h>

const char *TAG = "xiao_ya";

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))


int (*old_puts)(const char *) = NULL;

int new_puts(const char *string) {
    LOGI("in new_puts()");
    old_puts("inlineHook success");
}

int hook() {
    if (registerInlineHook((uint32_t) puts, (uint32_t) new_puts, (uint32_t * *) & old_puts) !=
        ELE7EN_OK) {
        return -1;
    }
    if (inlineHook((uint32_t) puts) != ELE7EN_OK) {
        return -1;
    }

    return 0;
}

int unHook() {
    if (inlineUnHook((uint32_t) puts) != ELE7EN_OK) {
        return -1;
    }

    return 0;
}

void callOriginal() {
    puts("test");
    hook();
    puts("test");
    unHook();
    puts("test");
}
