package com.supermarket.bean

import java.io.Serializable

/**
 *    @author : chrc
 *    date   : 2021/7/31  11:50 AM
 *    desc   :
 */
class ItemData: Serializable {

    var name: String? = null
    var amount: String? = null
    var days: Int = 0
    var interest: Float = 0f
    var downloadAdress: String? = null

    constructor(name: String?, amount: String?, days: Int, interest: Float, downloadAdress: String?) {
        this.name = name
        this.amount = amount
        this.days = days
        this.interest = interest
        this.downloadAdress = downloadAdress
    }
}