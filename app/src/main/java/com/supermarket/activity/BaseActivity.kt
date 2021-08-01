package com.supermarket.activity

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity

/**
 *    @author : chrc
 *    date   : 2021/7/31  1:02 PM
 *    desc   :
 */
open class BaseActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }
}