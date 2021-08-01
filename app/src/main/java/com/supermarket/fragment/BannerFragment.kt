package com.supermarket.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.drawee.view.SimpleDraweeView
import com.supermarket.R

/**
 *    @author : chrc
 *    date   : 2021/7/31  1:57 PM
 *    desc   :
 */
class BannerFragment: Fragment() {

    private lateinit var simpleDraweeView: SimpleDraweeView

    companion object {
        fun newInstance(picUrl: String?): BannerFragment {
            var bannerFragment = BannerFragment()
            var bundle = Bundle().apply {
                putString("picUrl", picUrl)
            }
            bannerFragment.arguments = bundle
            return bannerFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.activity_main_head_child, container, false)
        initView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var url = arguments?.getString("picUrl") ?: ""
        simpleDraweeView.setImageURI(url)
    }

    private fun initView(view: View?) {
        view?.apply {
            simpleDraweeView = this.findViewById(R.id.simple_drawee_view)
        }
    }
}