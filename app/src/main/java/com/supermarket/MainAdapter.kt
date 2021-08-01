package com.supermarket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.supermarket.bean.ItemData
import com.supermarket.download.DownloadManager
import com.supermarket.download.MyDownloadListener4WithSpeed
import com.supermarket.util.IntentUtil

/**
 *    @author : chrc
 *    date   : 2021/7/31  11:44 AM
 *    desc   :
 */
class MainAdapter(private var data: MutableList<ItemData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var clickCallback:((permissionCallback:(()->Unit)?)->Unit)? = null

    fun setClickCallback(clickCallback:((permissionCallback:(()->Unit)?)->Unit)?) {
        this.clickCallback = clickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.activity_main_content_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var itemViewHolder = holder as ItemViewHolder
        itemViewHolder.setData(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private var nameTv: TextView = itemView.findViewById(R.id.tv_item_title)
        private var amountValueTv: TextView = itemView.findViewById(R.id.tv_item_amount_value)
        private var amountTitleTv: TextView = itemView.findViewById(R.id.tv_item_amount_title)
        private var daysTv: TextView = itemView.findViewById(R.id.tv_item_days)
        private var interestTv: TextView = itemView.findViewById(R.id.tv_item_interest)
        private var applyTv: TextView = itemView.findViewById(R.id.tv_item_apply)
        private var progressTv: TextView = itemView.findViewById(R.id.tv_item_apply_progress)

        fun setData(itemData: ItemData?) {
            itemData?.apply {
                nameTv.text = name
                amountValueTv.text = amount
                daysTv.text = days.toString()
                interestTv.text = interest.toString()

                applyTv.setOnClickListener {
                    clickCallback?.invoke {
                        DownloadManager.downloadManager.download(itemView.context, DownloadManager.URL2, object : MyDownloadListener4WithSpeed.DownloadListenerCallback{
                            override fun progress(progress: Float, hasDownSize: String, totalSize: String) {
                                progressTv.text = "$progress $hasDownSize $totalSize"
                            }
                        })
//                    downloadAdress
                    }
                }
                itemView.setOnClickListener{
                    IntentUtil.goDetail(itemView.context, this)
                }
            }
        }
    }
}