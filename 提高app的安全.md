### app的安全

[实现android反调试](https://juejin.cn/post/6987569382671007780)

[Android APP漏洞之战（10）——调试与反调试技巧详解](https://bbs.pediy.com/thread-272452.htm)

[Android APP漏洞之战系列](https://github.com/WindXaa/Android-Vulnerability-Mining)

[安卓逆向VIP特训破解视频编程系列教程](https://www.youtube.com/watch?v=x4VJnpExiNc&list=PLkuKHvw9NmzRyt9HX90D_G9AvdBQPYcmp)

[猎豹清理大师内存清理权限泄露漏洞](https://wy.zone.ci/bug_detail.php?wybug_id=wooyun-2014-048735)

[Android APP通用型拒绝服务漏洞分析报告](https://blogs.360.net/post/android-app%E9%80%9A%E7%94%A8%E5%9E%8B%E6%8B%92%E7%BB%9D%E6%9C%8D%E5%8A%A1%E6%BC%8F%E6%B4%9E%E5%88%86%E6%9E%90%E6%8A%A5%E5%91%8A.html)

[EdXposed](https://github.com/ElderDrivers/EdXposed)

[MobSF](https://github.com/MobSF/Mobile-Security-Framework-MobSF)

[android-backup-extractor](https://github.com/nelenkov/android-backup-extractor)

[mprop 绕过debuggle=false](https://github.com/wpvsyou/mprop)

[安全的sp：secure-preferences](https://github.com/HussainDerry/secure-preferences)

HFS文件管理服务器

[Http/Https中间人攻击APP升级劫持漏洞](https://bbs.pediy.com/thread-268464.htm)

*  http明文传输升级劫持
*  http+hash验证升级劫持
*  https+hash验证升级劫持
IDA动态调试

[安卓 https 证书校验和绕过](https://juejin.cn/post/6992844908788711438)

* [JustTrustMe](https://github.com/Fuzion24/JustTrustMe)
* SSLkiller
* Frida脚本
  * [frida-android-unpinning-ssl](https://codeshare.frida.re/@masbog/frida-android-unpinning-ssl/)
  * [r0capture](https://github.com/r0ysue/r0capture)
* 硬编码

### 插件漏洞分类

* 动态加载漏洞

* 签名绕过漏洞

* zip加压漏洞

* janus 漏洞

  > Android 4.4后加入了**对JAR/DEX存放目录文件的user_id 和动态加载JAR/DEX的进程的user_id是否一致的判断，如果不一致将抛出异常导致加载失败**，这样就很好的可以防范替换加载的dex文件，进行恶意注入

  - 熟悉JEB、IDA、Jadx、apktool等常用逆向工具，了解ELF、DEX、OAT、smali 文件格式

  
[安全与逆向- so文件格式分析](https://www.jianshu.com/p/7e2d77419427/)
[Android逆向笔记 —— DEX 文件格式解析](https://juejin.cn/post/6844903847647772686)




  