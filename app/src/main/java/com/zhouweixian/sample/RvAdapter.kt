package com.zhouweixian.sample

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by zhouweixian on 2017/7/27
 */

class RvAdapter(ctx: Context, lst: List<String>) : RecyclerView.Adapter<RvAdapter.VH>() {

    private var datas: List<String>
    private var context: Context

    init {
        this.context = ctx
        this.datas = lst
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.tv.text=datas[position]
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        return VH(TextView(context))
    }


    class VH(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var tv: TextView

        init {
            tv = itemView as TextView
        }

    }
}
