package com.supermarket.download

import android.content.Context
import android.util.Log
import androidx.annotation.Nullable
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.SpeedCalculator
import com.liulishuo.okdownload.core.Util
import com.liulishuo.okdownload.core.breakpoint.BlockInfo
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend.Listener4SpeedModel


/**
 *    @author : chrc
 *    date   : 2021/7/31  11:33 PM
 *    desc   :
 */
class MyDownloadListener4WithSpeed(private val context: Context, private val pkgName: String, private val callback: DownloadListenerCallback?) :
    DownloadListener4WithSpeed() {
    private var totalLength: Long = 0
    private var readableTotalLength: String? = null

    override fun taskStart(task: DownloadTask) {
        Log.i("bqt", "【1、taskStart】")
    }

    override fun infoReady(
        task: DownloadTask,
        info: BreakpointInfo,
        fromBreakpoint: Boolean,
        model: Listener4SpeedModel
    ) {
        totalLength = info.totalLength
        readableTotalLength = Util.humanReadableBytes(totalLength, true)
        Log.i(
            "bqt",
            "【2、infoReady】当前进度" + info.totalOffset * 1f / totalLength * 100 + "%" + "，" + info.toString()
        )
    }

    override fun connectStart(
        task: DownloadTask,
        blockIndex: Int,
        requestHeaders: Map<String?, List<String?>?>
    ) {
        Log.i("bqt", "【3、connectStart】$blockIndex")
    }

    override fun connectEnd(
        task: DownloadTask,
        blockIndex: Int,
        responseCode: Int,
        responseHeaders: Map<String?, List<String?>?>
    ) {
        Log.i("bqt", "【4、connectEnd】$blockIndex，$responseCode")
    }

    override fun progressBlock(
        task: DownloadTask,
        blockIndex: Int,
        currentBlockOffset: Long,
        blockSpeed: SpeedCalculator
    ) {
        //Log.i("bqt", "【5、progressBlock】" + blockIndex + "，" + currentBlockOffset);
    }

    override fun progress(task: DownloadTask, currentOffset: Long, taskSpeed: SpeedCalculator) {
        val readableOffset: String = Util.humanReadableBytes(currentOffset, true)
        val progressStatus = "$readableOffset/$readableTotalLength"
        val speed = taskSpeed.speed()
        val percent = currentOffset.toFloat() / totalLength * 100
        callback?.progress(percent, readableOffset, readableTotalLength?:"0M")
        Log.i("bqt", "【6、progress】$currentOffset[$progressStatus]，速度：$speed，进度：$percent%")
    }

    override fun blockEnd(
        task: DownloadTask,
        blockIndex: Int,
        info: BlockInfo?,
        blockSpeed: SpeedCalculator
    ) {
        Log.i("bqt", "【7、blockEnd】$blockIndex")
    }

    override fun taskEnd(
        task: DownloadTask,
        cause: EndCause,
        @Nullable realCause: Exception?,
        taskSpeed: SpeedCalculator
    ) {
        Log.i(
            "bqt",
            "【8、taskEnd】" + cause.name + "：" + if (realCause != null) realCause.message else "无异常"
        )
        DownloadManager.downloadManager.dealEnd(context, pkgName, cause)
    }

    interface DownloadListenerCallback {
        fun progress(progress: Float, hasDownSize: String, totalSize: String){}
    }
}