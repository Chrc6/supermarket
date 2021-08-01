package com.supermarket.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.supermarket.R
import com.supermarket.util.Util

/**
 * author : chrc
 * date   : 2020/6/19  11:49 AM
 * desc   :
 */
class DownlaodTipDialogFragment : DialogFragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var progressTv: TextView

    private var callback: ((progress: Float, hasDownSize: String, totalSize: String)-> Unit)? = null

    companion object {
        fun newInstance(): DownlaodTipDialogFragment{
            return DownlaodTipDialogFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.dialog_fullScreen)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //这里的高度需要设置屏幕的分辨率高度，如果设置MATCH_PARENT得到的高度是减去状态栏的高度
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            if (window != null) {
                window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    Util.getRealHeight(requireContext())
                )
                window.setGravity(Gravity.TOP)
            }
//            dialog.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
//                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
//
//                    return@OnKeyListener true
//                }
//                false
//            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var inflate = inflater.inflate(R.layout.fragment_download_tip, container, false)
        progressBar = inflate.findViewById(R.id.progress)
        progressTv = inflate.findViewById(R.id.tv_download_progress_title)
        return inflate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {

    }

    fun createProgressCallback(): ((progress: Float, hasDownSize: String, totalSize: String)-> Unit)? {
        callback = { progress: Float, hasDownSize: String, totalSize: String ->
            progressBar.progress = progress.toInt()
            progressTv.text = context?.resources?.getString(R.string.downloading_tip, hasDownSize, totalSize)
        }
        return callback
    }

    fun fragmentShow(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitNowAllowingStateLoss()
    }

}