package com.jar.attackwebview

import android.util.Log
import android.webkit.JavascriptInterface

/**
 * @author  : dev
 * @version :
 * @Date    :  2022/8/23 11:57
 * @Desc    :
 *
 * Google在Android4.2以后对调用的函数以@JavascriptInterface进行注解从而避免漏洞攻击，
 * 也就是说我们js调用Android的方法，必须要在JavascriptInterface中进行声明，这样才能调用，


 *
 */
class AndroidToJs {

    companion object {
        private const val TAG = "AndroidToJs : "

    }

    // 定义JS需要调用的方法，被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public fun hello(msg: String) {
        Log.e(TAG, "Hello，$msg")
    }

    @JavascriptInterface
    public fun invokeByJs(msg: String) {
        Log.d(TAG, msg)
    }


}