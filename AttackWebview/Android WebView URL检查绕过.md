
### Android WebView URL检查绕过
URL结构
scheme://login:password@address:port/path/to/resource/?query_string#fragment

scheme
不区分大小写，包括http、https、file、ftp等等,:之后的“//”可省略，例如http:www.qq.com, 此外，多数浏览器在scheme之前加空格也是可以正常解析的
login:password@（认证信息）
服务器有时候需要用户名和密码认证，ftp协议比较常见，http很少见，但这个不常见字段往往可以绕过很多检查
address
address字段可以是一个不区分大小写的域名、一个ipv4地址或带方括号的ipv6地址，部分浏览器接收ip地址的八进制、十进制、十六进制等写法
port
端口号
/path/to/resource
层级路径，可以使用“../”到上一级目录
query_string
查询字符串，格式为”query_string?name1=value1&name2=value2”
fragment
用于html中的页面定位
白名单绕过
白名单绕过主要参考rebeyond的文章 一文彻底搞懂安卓WebView白名单校验, 中间添加了一些自己的绕过方法。

1.contains
private static boolean checkDomain(String inputUrl)
{
String[] whiteList=new String[]{"huawei.com","hicloud.com"};
for (String whiteDomain:whiteList)
{
if (inputUrl.contains(whiteDomain)>0)
return true;
}
return  false;
}
绕过方式：
任何可以添加字符串的字段

子域名 huawei.com.mysite.com
子路径 mysite.com/huawei.com
参数 mysite.com/xxxx#huawei.com
2.indexOf
private static boolean checkDomain(String inputUrl)
{
String[] whiteList=new String[]{"huawei.com","hicloud.com"};
for (String whiteDomain:whiteList)
{
if (inputUrl.indexOf(whiteDomain)>0)
return true;
}
return  false;
}
绕过方式：
和contains相同

3.startsWith、endsWith
一般白名单会有子域名，因此不用equal

绕过方式：

startsWith huawei.com.mysite.com
endsWith mysitehuawei.com
4.://和第一个/之间提取host
private static boolean checkDomain(String inputUrl)
{
String[] whiteList=new String[]{"huawei.com","hicloud.com"};
String tempStr=inputUrl.replace("://","");
String inputDomain=tempStr.substring(0,tempStr.indexOf("/")); //提取host
for (String whiteDomain:whiteList)
{
if (inputDomain.indexOf(whiteDomain)>0)
return true;
}
return  false;
}
绕过方式：

子域名 huawei.com.mysite.com
http://huawei.com@www.rebeyond.net/poc.htm
http://a:a@www.huawei.com:b@www.baidu.com 在android中使用getHost获取到的是huawei.com,但实际访问的是baidu.com
5.使用java.net.URL提取host
private static boolean checkDomain(String inputUrl) throws MalformedURLException {
String[] whiteList=new String[]{"huawei.com","hicloud.com"};
java.net.URL url=new java.net.URL(inputUrl);
String host=url.getHost(); //提取host
for (String whiteDomain:whiteList)
{
if (host.equals(whiteDomain)) return true;

    }
    return  false;
}
绕过方式：

http://a:a@www.huawei.com:b@www.baidu.com 在android中使用getHost获取到的是huawei.com,但实际访问的是baidu.com
https://www.mysite.com\@www.huawei.com/poc.htm上述URL通过java.net.URL的getHost方法得到的host是www.huawei.com，但实际上访问的确是www.mysite.com
https://www.mysite.com\\.huawei.com经过java.net.URL的getHost方法提取得到的是www.mysite.com.huawei.com，可以绕过白名单域名的endsWith匹配，但是实际访问的确是www.mysite.com服务器(新版已修复)
6.java.net.URI
java.net.URI能获取到正常的host,但是可以利用JavaScript协议绕过

rivate static boolean checkDomain(String inputUrl) throws  URISyntaxException {
String[] whiteList=new String[]{"huawei.com","hicloud.com"};
java.net.URI url=new java.net.URI(inputUrl);
String inputDomain=url.getHost(); //提取host
for (String whiteDomain:whiteList)
{
if (inputDomain.endsWith("."+whiteDomain)) //www.huawei.com      app.hicloud.com
return true;
}
return  false;
}
绕过方式：

javascript://www.huawei.com/%0d%0awindow.location.href=‘http://www.rebeyond.net/poc.htm‘
相当于执行了一行js代码，第一行通过//符号来骗过java.net.URI获取到值为www.huawei.com的host，恰好//符号在Javascript的世界里是行注释符号，所以第一行实际并没有执行；然后通过%0d%0a换行，继续执行window.location.href=’http://www.rebeyond.net/poc.htm’

7.排除javascript协议
private static boolean checkDomain(String inputUrl) throws  URISyntaxException {
if (!inputUrl.startsWith("http://")&&!inputUrl.startsWith("https://"))
{
return false;
}
String[] whiteList=new String[]{"huawei.com","hicloud.com"};
java.net.URI url=new java.net.URI(inputUrl);
String inputDomain=url.getHost(); //提取host
for (String whiteDomain:whiteList)
{
if (inputDomain.endsWith("."+whiteDomain)) //www.huawei.com      app.hicloud.com
return true;
}
return  false;
}
绕过方式：
配合url重定向漏洞，例如https://www.huawei.com/redirect.php?url=http://mysite.com

防御办法：
Webview在请求https://www.huawei.com/redirect.php?url=http://mysite.com的时候，实际是发出了两次请求，第一次是在loadUrl中请求，第二次是请求http://mysite.com，但是第二次请求发生在loadUrl之后，而我们的白名单校验逻辑在loadUrl之前，才导致了绕过。通过在webview的shouldOverrideUrlLoading方法中检测，拦截跳转。

黑名单
有些app会在URL中指定黑名单，例如竞品域名等，这里简单罗列一些思路：

使用大小写绕过contains，例如HTTP://MySITe.com
使用特殊编码
待补充
file协议绕过
APP经常会使用file://协议加载本地文件，通常会限制在一些特定路径中，这里记录一下之前的经验：

不要用url.startWith(”file://”)来判断是否为file协议，因为“FILE://”(大小)、“File://”(大小写)、“ file://”(前边加空格)、“file:”等方式都可以绕过检测。url.contains(“file://”)更不靠谱，推荐使用getScheme()来判断协议；
file:///android_asset和file:///android_res 也可以../穿越
白名单判断了“../，但通过“..\”也是可以穿越的，例如file:///sdcard/..\../sdcard/1.html
getHost有漏洞（file://a:a@www.qq.com:b@www.baidu.com使用getHost获取到的是qq.com,但实际访问的是baidu.com)
file://baidu.com/data/data/tmp 前边的baidu.com是可以不被解析的
协议头不包括///，还是仍然能够正常loadUrl，如file:mnt/sdcard/filedomain.html
白名单判断了“../”，但通过url编码绕过，例如file:///data/data/com.app/%2e%2e/%2e%2e/%2e%2e/sdcard/xxx
replace(“../“,””)可以使用”….//“绕过
参考资料：

一文彻底搞懂安卓WebView白名单校验
https://android.googlesource.com/platform/frameworks/base/+/4afa0352d6c1046f9e9b67fbf0011bcd751fcbb5

https://android.googlesource.com/platform/frameworks/base/+/0b57631939f5824afef06517df723d2e766e0159