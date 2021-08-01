package com.supermarket.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gyf.barlibrary.ImmersionBar
import com.supermarket.R
import com.supermarket.bean.ItemData
import com.supermarket.download.DownloadManager
import com.supermarket.download.MyDownloadListener4WithSpeed
import com.supermarket.fragment.DownlaodTipDialogFragment
import com.supermarket.util.IntentUtil
import com.supermarket.util.Util

class DetailActivity : BaseActivity() {

    private lateinit var backIv: ImageView
    private lateinit var titleTv: TextView
    private lateinit var inrValueTv: TextView
    private lateinit var inrDescTv: TextView
    private lateinit var daysTv: TextView
    private lateinit var interestTv: TextView
    private lateinit var downloadTv: TextView

    private var itemData: ItemData? = null
    private var callback: ((progress: Float, hasDownSize: String, totalSize: String)-> Unit)? = null
    private var downloadingFragment: DownlaodTipDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initView()
        initData()
    }

    private fun initData() {
        intent?.extras?.apply {
            itemData = getSerializable(IntentUtil.SERIALIZABLE_DATA) as? ItemData
        }
        itemData?.apply {
            titleTv.text = name
            inrValueTv.text = amount
            interestTv.text = interest.toString()
            daysTv.text = days.toString()
        }
    }

    private fun initView() {
        ImmersionBar.with(this).transparentStatusBar().init()
        backIv = findViewById(R.id.iv_back)
        titleTv = findViewById(R.id.tv_title)
        inrValueTv = findViewById(R.id.tv_inr_value)
        inrDescTv = findViewById(R.id.tv_desc)
        daysTv = findViewById(R.id.tv_day_value)
        interestTv = findViewById(R.id.tv_interest_value)
        downloadTv = findViewById(R.id.tv_download)

        var dp17 = Util.dip2px(this, 17.toDouble())
        titleTv.setPadding(0,dp17 + ImmersionBar.getStatusBarHeight(this), 0, dp17)

        downloadTv.setOnClickListener {
            itemData?.apply {
//                DownloadManager.downloadManager.download(this@DetailActivity, itemData.downloadAdress, null)
                DownloadManager.downloadManager.download(this@DetailActivity, DownloadManager.URL3, object : MyDownloadListener4WithSpeed.DownloadListenerCallback{
                    override fun progress(progress: Float, hasDownSize: String, totalSize: String) {
                        callback?.invoke(progress, hasDownSize, totalSize)
                    }
                })
                createDownloadingDialogFragment()
                downloadingFragment?.fragmentShow(supportFragmentManager,"DownlaodTipDialogFragment")
            }
        }
        backIv.setOnClickListener {
            finish()
        }
    }

    private fun createDownloadingDialogFragment(): DownlaodTipDialogFragment {
        if (downloadingFragment == null) {
            downloadingFragment = DownlaodTipDialogFragment.newInstance()
            callback = downloadingFragment!!.createProgressCallback()
        }
        return downloadingFragment!!
    }
}