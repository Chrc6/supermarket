package com.supermarket.util

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 *    @author : chrc
 *    date   : 2021/8/1  11:56 AM
 *    desc   :
 */
class Util {

    companion object {
        /**
         * dip值转px
         */
        fun dip2px(context: Context, d: Double): Int {
            val scale = context.resources.displayMetrics.density
            return (d * scale + 0.5f).toInt()
        }

        //获取屏幕真实高度，不包含下方虚拟导航栏
        fun getRealHeight(context: Context): Int {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val dm = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(dm)
            } else {
                display.getMetrics(dm)
            }
            return dm.heightPixels
        }

        /**
         * 获得设备屏幕宽度 单位像素
         */
        fun getDeviceWidthPixels(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }

        /**
         * 获得设备屏幕高度 单位像素
         */
        fun getDeviceHeightPixels(context: Context): Int {
            return context.resources.displayMetrics.heightPixels
        }
    }
}