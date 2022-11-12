package com.devicewifitracker.android.ui.suspicious

import android.net.wifi.ScanResult
import android.os.Build
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.devicewifitracker.android.databinding.SuspiciousListItemBinding
import com.devicewifitracker.android.ui.detail.SuspiciousDetailActivity

class SuspiciousAdapter(private val tableList: List<String>?) : RecyclerView.Adapter<SuspiciousAdapter.ViewHolder>() {

    class ViewHolder(susItemBinding: SuspiciousListItemBinding) : RecyclerView.ViewHolder(susItemBinding.root) {
        val susIpText: TextView = susItemBinding.susIpText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val tableItemBinding = SuspiciousListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(tableItemBinding)
        holder.itemView.setOnClickListener {

            val position = holder.bindingAdapterPosition
            val table = tableList?.get(position)
//            DataActivity.actionOpenTable(parent.context, table.name)
            table?.let { it1 -> SuspiciousDetailActivity.actionOpenAct(parent.context, it1,"unconfirmed") }
        }
        return holder
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.setExtraMarginForFirstAndLastItem(position == 0, position == tableList.size - 1)
        val table = tableList?.get(position)
        holder.susIpText.text = table
    }

    override fun getItemCount() = tableList?.size?:0

}