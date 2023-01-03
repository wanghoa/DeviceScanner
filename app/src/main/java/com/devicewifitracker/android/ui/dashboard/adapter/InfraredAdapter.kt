package com.devicewifitracker.android.ui.dashboard.adapter

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devicewifitracker.android.App
import com.devicewifitracker.android.databinding.AdapterInfraredItemBinding
import com.devicewifitracker.android.model.Infrared
import com.devicewifitracker.android.ui.gpu.GpuActivity
import com.devicewifitracker.android.ui.subscribe.SubscribeActivity
import com.devicewifitracker.android.util.SubscribeManager

/**
 * 红外探测
 */
class InfraredAdapter(private val infraredList: List<Infrared>) :
    RecyclerView.Adapter<InfraredAdapter.ViewHolder>() {

    class ViewHolder(tabItemBinding: AdapterInfraredItemBinding) :
        RecyclerView.ViewHolder(tabItemBinding.root) {
        val iv: ImageView = tabItemBinding.itemIv
        val tv: TextView = tabItemBinding.itemTv

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val tabItemBinding =
            AdapterInfraredItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(tabItemBinding)
        holder.itemView.setOnClickListener {
//            if (!SubscribeManager.instance.isSubscribe()) {
//                parent.context?.let { it1 -> SubscribeActivity.actionOpenAct(it1,"") }
//               return@setOnClickListener
//            }
            GpuActivity.actionOpenAct(parent.context)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Glide.with(App.context).load("").into(holder.iv)
        holder.iv.setImageResource(infraredList[position].pic)
        holder.tv.text = infraredList[position].text
    }

    override fun getItemCount(): Int {
        return infraredList.size
    }
}