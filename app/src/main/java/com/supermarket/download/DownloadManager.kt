package com.supermarket.download

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.core.Util
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.dispatcher.CallbackDispatcher
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher
import com.liulishuo.okdownload.core.download.DownloadStrategy
import com.liulishuo.okdownload.core.file.DownloadUriOutputStream
import com.liulishuo.okdownload.core.file.ProcessFileStrategy
import java.io.File
import java.io.FilenameFilter


/**
 *    @author : chrc
 *    date   : 2021/7/31  6:10 PM
 *    desc   : 参考自:https://www.jianshu.com/p/b7212abd4a43
 */
class DownloadManager {

    var map: HashMap<String, DownloadTask> = HashMap()

    companion object {
        val downloadManager: DownloadManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DownloadManager()
        }
        const val URL1 =
            "https://oatest.dgcb.com.cn:62443/mstep/installpkg/yidongyingxiao/90.0/DGMmarket_rtx.apk"
        const val URL2 =
            "https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk"
        const val URL3 =
            "https://downapp.baidu.com/appsearch/AndroidPhone/1.0.78.155/1/1012271b/20190404124002/appsearch_AndroidPhone_1-0-78-155_1012271b.apk"

        val PARENT_PATH: String =
            Environment.getExternalStorageDirectory().absolutePath.toString() + "/supermarket"
    }

    fun download(context: Context, url: String?, callback: MyDownloadListener4WithSpeed.DownloadListenerCallback?) {
        var pkgName = getApkname(url?:"")
        url?.apply {
            var task = createDownloadTask(this, pkgName)
            task?.apply {
                enqueue(MyDownloadListener4WithSpeed(context, pkgName, callback))
                map[pkgName] = this;
            }
        }
    }

    private fun getApkname(url: String?): String {
        return if (url == null || url.length < 10) {
            url?:"supermarket_app"
        } else {
            url.substring(url.length - 10, url.length)
        }
    }

    private fun createDownloadTask(url: String, pdkName: String): DownloadTask? {
        return DownloadTask.Builder(url, createFile()) //设置下载地址和下载目录，这两个是必须的参数
            .setFilename(pdkName) //设置下载文件名，没提供的话先看 response header ，再看 url path(即启用下面那项配置)
            .setFilenameFromResponse(false) //是否使用 response header or url path 作为文件名，此时会忽略指定的文件名，默认false
//            .setPassIfAlreadyCompleted(true) //如果文件已经下载完成，再次下载时，是否忽略下载，默认为true(忽略)，设为false会从头下载
            .setPassIfAlreadyCompleted(false) //如果文件已经下载完成，再次下载时，是否忽略下载，默认为true(忽略)，设为false会从头下载
            .setConnectionCount(1) //需要用几个线程来下载文件，默认根据文件大小确定；如果文件已经 split block，则设置后无效
            .setPreAllocateLength(false) //在获取资源长度后，设置是否需要为文件预分配长度，默认false
            .setMinIntervalMillisCallbackProcess(100) //通知调用者的频率，避免anr，默认3000(下载进度回调的间隔时间（毫秒）)
            .setWifiRequired(false) //是否只允许wifi下载，默认为false
            .setAutoCallbackToUIThread(true) //是否在主线程通知调用者，默认为true
            //.setHeaderMapFields(new HashMap<String, List<String>>())//设置请求头
            //.addHeader(String key, String value)//追加请求头
            .setPriority(10) //设置优先级，默认值是0，值越大下载优先级越高
            .setReadBufferSize(4096) //设置读取缓存区大小，默认4096
            .setFlushBufferSize(16384) //设置写入缓存区大小，默认16384
            .setSyncBufferSize(65536) //写入到文件的缓冲区大小，默认65536
            .setSyncBufferIntervalMillis(2000) //写入文件的最小时间间隔，默认2000
            .build()
    }

    private fun createFile(): File {
        var file = File(PARENT_PATH)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun launchOrInstallApp(context: Context, pkgName: String?) {
        if (!TextUtils.isEmpty(pkgName)) {
            val intent: Intent? = context.packageManager.getLaunchIntentForPackage(pkgName ?: "")
            if (intent == null) { //如果未安装，则先安装
                installApk(context, File(PARENT_PATH, pkgName))
            } else { //如果已安装，跳转到应用
                context.startActivity(intent)
            }
        } else {
            Toast.makeText(context, "包名为空！", Toast.LENGTH_SHORT).show()
            installApk(context, File(PARENT_PATH, pkgName))
        }
    }

    //1、申请两个权限：WRITE_EXTERNAL_STORAGE 和 REQUEST_INSTALL_PACKAGES ；2、配置FileProvider
    fun installApk(context: Context, file: File?) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            uri = FileProvider.getUriForFile(
                context, context.packageName.toString() + ".fileprovider",
                file!!
            )
            //【content://{$authority}/external/temp.apk】或【content://{$authority}/files/bqt/temp2.apk】
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK //【file:///storage/emulated/0/temp.apk】
            uri = Uri.fromFile(file)
        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Log.i("bqt", "【Uri】$uri")
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    fun buildOkDownload(context: Context): OkDownload? {
        return OkDownload.Builder(context.applicationContext)
            .downloadStore(Util.createDefaultDatabase(context)) //断点信息存储的位置，默认是SQLite数据库
            .callbackDispatcher(CallbackDispatcher()) //监听回调分发器，默认在主线程回调
            .downloadDispatcher(DownloadDispatcher()) //下载管理机制，最大下载任务数、同步异步执行下载任务的处理
            .connectionFactory(Util.createDefaultConnectionFactory()) //选择网络请求框架，默认是OkHttp
            .outputStreamFactory(DownloadUriOutputStream.Factory()) //构建文件输出流DownloadOutputStream，是否支持随机位置写入
            .processFileStrategy(ProcessFileStrategy()) //多文件写文件的方式，默认是根据每个线程写文件的不同位置，支持同时写入
            //.monitor(monitor); //下载状态监听
            .downloadStrategy(DownloadStrategy()) //下载策略，文件分为几个线程下载
            .build()
    }

    /**
     * 删除一个文件，或删除一个目录下的所有文件
     *
     * @param dirFile      要删除的目录，可以是一个文件
     * @param filter       对要删除的文件的匹配规则(不作用于目录)，如果要删除所有文件请设为 null
     * @param isDeleateDir 是否删除目录，false时只删除目录下的文件而不删除目录
     */
    fun deleateFiles(dirFile: File, filter: FilenameFilter?, isDeleateDir: Boolean) {
        if (dirFile.isDirectory) { //是目录
            for (file in dirFile.listFiles()) {
                deleateFiles(file, filter, isDeleateDir) //递归
            }
            if (isDeleateDir) {
                println("目录【" + dirFile.absolutePath + "】删除" + if (dirFile.delete()) "成功" else "失败") //必须在删除文件后才能删除目录
            }
        } else if (dirFile.isFile) { //是文件。注意 isDirectory 为 false 并非就等价于 isFile 为 true
            val symbol = if (isDeleateDir) "\t" else ""
            if (filter == null || filter.accept(dirFile.parentFile, dirFile.name)) { //是否满足匹配规则
                println(symbol + "- 文件【" + dirFile.absolutePath + "】删除" + if (dirFile.delete()) "成功" else "失败")
            } else {
                println(symbol + "+ 文件【" + dirFile.absolutePath + "】不满足匹配规则，不删除")
            }
        } else {
            println("文件不存在")
        }
    }

    fun dealEnd(context: Context, pkgName: String, cause: EndCause) {
        if (cause == EndCause.COMPLETED) {
            Toast.makeText(context, "任务完成", Toast.LENGTH_SHORT).show()
//            itemInfo.status = 3 //修改状态
            launchOrInstallApp(context, pkgName)
        } else {
//            itemInfo.status = 2 //修改状态
            if (cause == EndCause.CANCELED) {
                Toast.makeText(context, "任务取消", Toast.LENGTH_SHORT).show()
            } else if (cause == EndCause.ERROR) {
                Log.i("bqt", "【任务出错】")
            } else if (cause == EndCause.FILE_BUSY || cause == EndCause.SAME_TASK_BUSY || cause == EndCause.PRE_ALLOCATE_FAILED) {
                Log.i("bqt", "【taskEnd】" + cause.name)
            }
        }
    }

    fun onDestroy() {
        for (key in map.keys) {
            val task = map[key]
            task?.cancel()
        }
    }
}