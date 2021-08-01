package com.supermarket.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.facebook.drawee.backends.pipeline.Fresco
import com.gyf.barlibrary.ImmersionBar
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.supermarket.fragment.BannerFragment
import com.supermarket.MainAdapter
import com.supermarket.R
import com.supermarket.bean.ItemData
import com.supermarket.util.Util

class MainActivity : BaseActivity() {

    private lateinit var xRecyclerView: XRecyclerView
    private lateinit var titleTv: TextView

    private lateinit var mainAdapter: MainAdapter
    private var datas: ArrayList<ItemData> = ArrayList()
    private var page: Int = 1

    private var permissionCallback:(()->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Fresco.initialize(applicationContext);
        initView()
        initTestData()
    }

    private fun initTestData() {
        if (page == 1) datas.clear()
        for (i in (page-1)*10 until page*10) {
//            var itemData = ItemData("testname$i"," testamount$i",i, i.toFloat(), "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png")
            var itemData = ItemData("testname$i"," testamount$i",i, i.toFloat(), null)
            datas.add(itemData)
        }
        mainAdapter.notifyDataSetChanged()
        if (page == 1) {
            xRecyclerView.refreshComplete()
        } else {
            xRecyclerView.loadMoreComplete()
        }
        page++
        xRecyclerView.setLoadingMoreEnabled(page < 4)
    }

    private fun initView() {
        ImmersionBar.with(this).transparentStatusBar()
            .statusBarDarkFont(true)
            .init()

        xRecyclerView = findViewById(R.id.recycler_view)
        titleTv = findViewById(R.id.tv_title)
        titleTv.setPadding(0, ImmersionBar.getStatusBarHeight(this), 0, 0)
        var layoutParams = titleTv.layoutParams
        layoutParams.height = Util.dip2px(this, 47.toDouble()) + ImmersionBar.getStatusBarHeight(this)
        titleTv.layoutParams = layoutParams

        xRecyclerView.defaultRefreshHeaderView.setRefreshTimeVisible(true)
        var view = LayoutInflater.from(this).inflate(R.layout.activity_main_head, null, false)
        initHeadView(view)
        xRecyclerView.addHeaderView(view)
        xRecyclerView.defaultFootView.setNoMoreHint("没有更多数据")

        xRecyclerView.layoutManager = LinearLayoutManager(this)
        mainAdapter = MainAdapter(datas)
        mainAdapter.setClickCallback {
            var checkSelfPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
                it?.invoke()
            } else {
                permissionCallback = it
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }
        xRecyclerView.adapter = mainAdapter
        xRecyclerView.setLoadingListener(object: XRecyclerView.LoadingListener{
            override fun onRefresh() {
                Log.i("main===", "onRefresh")
                page = 1
                initTestData()
            }

            override fun onLoadMore() {
                Log.i("main===", "onLoadMore")
                initTestData()
            }

        })
    }

    private fun initHeadView(view: View?) {
        view?.apply {
            var viewPager = this.findViewById(R.id.view_pager2) as ViewPager2
            viewPager.adapter = object : FragmentStateAdapter(this@MainActivity){
                override fun getItemCount(): Int {
                    return datas.size
                }

                override fun createFragment(position: Int): Fragment {
                    return BannerFragment.newInstance(datas[position].downloadAdress)
                }

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var hasAllGranted = true
        for (granted in grantResults) {
            if (granted != PackageManager.PERMISSION_GRANTED) {
                hasAllGranted = false
            }
        }
        if (hasAllGranted) {
            permissionCallback?.invoke()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ImmersionBar.with(this).destroy()
    }
}