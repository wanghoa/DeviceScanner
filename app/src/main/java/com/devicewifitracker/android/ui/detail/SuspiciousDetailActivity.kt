package com.devicewifitracker.android.ui.detail

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.devicewifitracker.android.App
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivitySuspiciousDetailBinding

class SuspiciousDetailActivity :BaseActivity<ActivitySuspiciousDetailBinding>() {

    companion object{
        fun actionOpenAct(context: Context,ip:String) {
            val intent = Intent(context,SuspiciousDetailActivity::class.java).apply {
                putExtra("ip",ip)
            }
            context.startActivity(intent)

        }
    }
    override fun getLayoutId(): Int {
      return R.layout.activity_suspicious_detail
    }

    override fun initView(savedInstanceState: Bundle?) {
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.rootView?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        super.initView(savedInstanceState)
        val wifiManager =
            App.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo //获取当前连接的信息
        binding.wifiNameTv.text = "Wi-Fi:" +" "+wifiInfo.ssid.replace("\"", "")
      val networkIp =  intent.getStringExtra("ip")
        binding.copy.setOnClickListener {
            ClipboardUtils.copyText("${wifiInfo.bssid}")
            ToastUtils.showLong("Copied")
            LogUtils.d("${wifiInfo.bssid}------")
//            LogUtils.d("${wifiInfo.macAddress}------")
        }
        binding.tvIP.text =    "IP Address:" +networkIp
        binding.back.setOnClickListener {
            finish()
        }
        when (networkIp) {
            NetworkUtils.getGatewayByWifi() -> {

                Glide.with(this).load(R.mipmap.icon_router_large).into(binding.imageWarm)
//                binding.tvOrganization.text = "Organization:"+${wifiInfo.}
                binding.copy.visibility = View.VISIBLE
            }
            NetworkUtils.getIpAddressByWifi() -> {
                Glide.with(this).load(R.mipmap.icon_phone).into(binding.imageWarm)
                binding.copy.visibility = View.INVISIBLE
                binding.tvOrganization.visibility = View.INVISIBLE

            }
            else ->{
                Glide.with(this).load(R.mipmap.icon_suspicious_warm).into(binding.imageWarm)
                binding.copy.visibility = View.INVISIBLE
            }

        }
    }
}