package com.devicewifitracker.android.ui.table

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.devicewifitracker.android.App
import com.devicewifitracker.android.R
import com.devicewifitracker.android.databinding.IpTableItemBinding
import com.devicewifitracker.android.ui.detail.SuspiciousDetailActivity
import com.devicewifitracker.android.ui.subscribe.SubscribeActivity
import com.devicewifitracker.android.util.Constant
import com.devicewifitracker.android.util.SubscribeManager

class TableAdapter(private val tableList: List<String>) :
    RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    class ViewHolder(tableItemBinding: IpTableItemBinding) :
        RecyclerView.ViewHolder(tableItemBinding.root) {
        val tableIpText: TextView = tableItemBinding.tabIpText
        val tabEquipmentText: TextView = tableItemBinding.tabEquipmentText
        val image: ImageView = tableItemBinding.tabImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val tableItemBinding =
            IpTableItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(tableItemBinding)
        holder.itemView.setOnClickListener {
            if (!TextUtils.isEmpty(
                    SPUtils.getInstance()
                        .getString("MAC_ADRESS")
                ) && SPUtils.getInstance().getBoolean(Constant.AGREEMENT_KEY)
            ) {

//                if (!SubscribeManager.instance.isSubscribe()) {
////                    startActivity(Intent(this@DeviceListActivity,SubscribeActivity::class.java))
//                    SubscribeActivity.actionOpenAct(parent.context,"")
//
//
//                } else {
                    val position = holder.bindingAdapterPosition
                    val table = tableList[position]
//            DataActivity.actionOpenTable(parent.context, table.name)
                    SuspiciousDetailActivity.actionOpenAct(parent.context, table, "confirmed")
//                }

            } else {
//                ToastUtils.showLong("数据检查中,请稍后...")

                val buidler = parent.context?.let { AlertDialog.Builder(it) }
                buidler?.setTitle("")
                buidler?.setMessage(parent.context.getString(R.string.device_scanning))
                buidler?.setCancelable(true)
                buidler?.setPositiveButton(parent.context.getString(R.string.ok), object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        p0?.dismiss()
                    }
                })
                buidler?.setNegativeButton("", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0?.dismiss()
                    }
                })
                buidler?.create()?.show()
            }
        }
        return holder
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.setExtraMarginForFirstAndLastItem(position == 0, position == tableList.size - 1)
        val ip = tableList[position]
        holder.tableIpText.text = ip
        val wm =
            Utils.getApp().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wm.connectionInfo
        if (NetworkUtils.getIpAddressByWifi().equals(ip) || NetworkUtils.getGatewayByWifi()
                .equals(ip)
        ) {


            holder.tabEquipmentText.setTextColor(
                ContextCompat.getColor(
                    App.context,
                    R.color.white
                )
            )
//        LogUtils.e("ip=="+"${ip}-------${NetworkUtils.getGatewayByWifi() }---${NetworkUtils.getIpAddressByWifi()}")
            when (ip) {
                NetworkUtils.getGatewayByWifi() -> {
                    Glide.with(App.context).load(R.mipmap.icon_router_large).into(holder.image)
                    holder.tabEquipmentText.text = holder.itemView.context.getString(R.string.router)
                }
                NetworkUtils.getIpAddressByWifi() -> {
                    Glide.with(App.context).load(R.mipmap.icon_phone).into(holder.image)
                    holder.tabEquipmentText.text = holder.itemView.context.getString(R.string.my_iPhone)
                }
            }
        } else {
            holder.tabEquipmentText.text = ""
//            holder.tabEquipmentText.setTextColor(
//                ContextCompat.getColor(
//                    App.context,
//                    R.color.red_ff0039
//                )
//            )
//            Glide.with(App.context).load(R.mipmap.icon_suspicious_warm).into(holder.image)
            Glide.with(App.context).load(R.mipmap.icon_phone).into(holder.image)
        }
//        LogUtils.d("SSID:${wifiInfo.ssid.replace("\"", "")}----${ table.SSID}--${wifiInfo.ssid.replace("\"", "").equals(table.SSID)}")

    }

    override fun getItemCount() = tableList.size

}