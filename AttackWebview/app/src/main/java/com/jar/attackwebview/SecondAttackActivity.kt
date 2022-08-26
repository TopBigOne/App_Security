package com.jar.attackwebview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.jar.attackwebview.databinding.ActivityMainBinding
import com.jar.attackwebview.databinding.ActivitySeconAttackBinding

class SecondAttackActivity : AppCompatActivity() {

    lateinit var binding: ActivitySeconAttackBinding
    lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeconAttackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initWebView()

        initEvent();

    }


    private fun initWebView() {
        webView = binding.secondWebView
        //设置是否允许 WebView 使用 JavaScript
        webView.settings.javaScriptEnabled = true

    }

    private fun initEvent() {
        binding.btnAllowFileAccess.setOnClickListener {
            //设置是否允许 WebView 使用 File 协议
            //会报这个错：
            // "Access to XMLHttpRequest at 'file:///etc/hosts' from origin 'null' has been blocked by CORS policy: Cross origin requests are only supported for protocol schemes: http, data, chrome, chrome-untrusted, https.", source: file:///data/local/tmp/fileAttack.html (0)
            webView.settings.allowFileAccess = true
            webView.settings.allowFileAccessFromFileURLs = true

            webView.loadUrl("file:///data/local/tmp/fileAttack.html");
        }


        // 访问一个网站
        binding.btnAllowUniversalAccessFromFileUrl.setOnClickListener {
            //设置是否允许 WebView 使用 File 协议
            //会报这个错：
            // "Access to XMLHttpRequest at 'file:///etc/hosts' from origin 'null' has been blocked by CORS policy: Cross origin requests are only supported for protocol schemes: http, data, chrome, chrome-untrusted, https.", source: file:///data/local/tmp/fileAttack.html (0)
            webView.settings.allowFileAccess = true
            webView.settings.javaScriptEnabled = true
           webView.settings.allowUniversalAccessFromFileURLs = true

            webView.loadUrl("file:///data/local/tmp/fileAttack_2.html");
        }


    }
}