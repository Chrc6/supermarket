package com.supermarket.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.supermarket.activity.DetailActivity
import com.supermarket.bean.ItemData

/**
 *    @author : chrc
 *    date   : 2021/8/1  12:38 PM
 *    desc   :
 */
class IntentUtil {

    companion object {
        const val SERIALIZABLE_DATA = "serializable_data"

        fun goDetail(context: Context, itemData: ItemData) {
            var intent = Intent(context, DetailActivity::class.java).apply {
                var bundle = Bundle().apply {
                    putSerializable(SERIALIZABLE_DATA, itemData)
                }
                putExtras(bundle)
            }
            context.startActivity(intent)
        }
    }
}