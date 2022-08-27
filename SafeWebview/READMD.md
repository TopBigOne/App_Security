[研究地址](https://github.com/seven456/SafeWebView)
* 1、WebView addJavascriptInterface安全漏洞问题；
* 2、支持网页将JS函数（function）传到Java层，方便回调；
* 3、解决各种WebView的崩溃（附日志）；
* 4、WebView设置代理（对不同Android系统版本调用Java反射实现）；
### 原理

#### 使用prompt中转反射调用Java层接口类中的方法，将方法名、参数类型、参数封装成Json进行传递；
* 另参照：
* 1、在WebView中如何让JS与Java安全地互相调用：http://www.pedant.cn/2014/07/04/webview-js-java-interface-research/，源码（20150621）：https://github.com/pedant/safe-java-js-webview-bridge
* 2、Android WebView的Js对象注入漏洞解决方案：http://blog.csdn.net/leehong2005/article/details/11808557