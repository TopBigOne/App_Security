package com.jar.attackwebview

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.jar.attackwebview.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mWebView: WebView

    lateinit var webSetting: WebSettings

    companion object {
        private const val TAG = "MainActivity : "
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initWebView()
        initEvent()

    }

    private fun initWebView() {
        mWebView = binding.mainWebView
        webSetting = mWebView.settings
        webSetting.javaScriptEnabled = true
    }

    private fun initEvent() {
        androidCallJs()
        evaluateJavascript()
        jsCallAndroid()
    }

    private fun jsCallAndroid() {
        mWebView.loadUrl("file:///android_asset/javascript1.html")
        mWebView.webViewClient = webViewClientThree

    }

    private fun evaluateJavascript() {
        mWebView.loadUrl("file:///android_asset/javascript.html");
        mWebView.post {
            mWebView.evaluateJavascript("javascript:callJS()", object : ValueCallback<String> {
                override fun onReceiveValue(value: String?) {
                    // 此处为js返回的结果
                }
            })

        }
    }

    private fun androidCallJs() {
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        mWebView.addJavascriptInterface(AndroidToJs(), "ITestTwo")
        mWebView.loadUrl("file:///android_asset/AndroJs.html")
        mWebView.webChromeClient = webChromeClient
        mWebView.webViewClient = webViewClientOne

        binding.btnOne.setOnClickListener {
            mWebView.post(object : Runnable {
                override fun run() {
                    mWebView.loadUrl("javascript:callJS()");
                }
            })
        }
    }

    private val webViewClientOne = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?,
                request: WebResourceRequest?): Boolean {
            request?.let {
                view?.loadUrl(it.url.toString())
            }
            return true
        }

        // ERR_cleartext_not_permitted
    }

    private val webChromeClient = object : WebChromeClient() {
        override fun onJsAlert(view: WebView?, url: String?, message: String?,
                result: JsResult?): Boolean {
            val b: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
            b.setTitle("android Alert")
            b.setMessage(message)
            b.setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, which -> result!!.confirm() })
            b.setCancelable(false)
            b.create().show()

            return true
        }
    }


    private val webViewClientThree = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?,
                request: WebResourceRequest?): Boolean {
            // 步骤2：根据协议的参数，判断是否是所需要的url
            // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
            //约定的url协议为：js://webview?arg1=WindXaa&arg2=attack（同时也是约定好的需要拦截的）

            // 步骤2：根据协议的参数，判断是否是所需要的url
            // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
            //约定的url协议为：js://webview?arg1=WindXaa&arg2=attack（同时也是约定好的需要拦截的）
            val uri = request?.url
            // 如果url的协议 = 预先约定的 js 协议
            // 就解析往下解析参数
            // 如果url的协议 = 预先约定的 js 协议
            // 就解析往下解析参数
            uri?.let {
                if(uri.scheme.equals("js")) {
                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if(uri.authority.equals("webview")) {
                        //  步骤3：
                        // 执行JS所需要调用的逻辑
                        Log.d(TAG, "shouldOverrideUrlLoading: js调用了Android的方法")
                        // 可以在协议上带有参数并传递到Android上
                        val params: HashMap<String, String> = HashMap()
                        val collection: Set<String> = uri.getQueryParameterNames()

                        Log.e(TAG, "params : $params")
                    }
                    return true
                }


            }
            request?.url.let {
                return super.shouldOverrideUrlLoading(view, it.toString())
            }
        }
    }


}