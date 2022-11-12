package com.devicewifitracker.android.ui.detail

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.devicewifitracker.android.App
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivitySuspiciousDetailBinding
import com.devicewifitracker.android.room.dao.OrganizationDao
import com.devicewifitracker.android.room.dao.RouterDao
import com.devicewifitracker.android.room.database.AppDatabase
import com.devicewifitracker.android.room.entity.Router
import com.devicewifitracker.android.util.*
import java.util.*
import kotlin.concurrent.thread
import kotlin.random.Random

class SuspiciousDetailActivity :BaseActivity<ActivitySuspiciousDetailBinding>() {
var routerDao:RouterDao?= null
    private var organiaztionDao: OrganizationDao? = null
    var wifiManager:WifiManager?= null
    var organization :String? = ""
    companion object{
        fun actionOpenAct(context: Context,ip:String,from:String) {
            val intent = Intent(context,SuspiciousDetailActivity::class.java).apply {
                putExtra("ip",ip)
                putExtra("from",from)
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
        routerDao = RoomGetDao.getRouterDao()
        organiaztionDao = RoomGetDao.getOrganizationDao()
         wifiManager =
            App.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager?.connectionInfo //获取当前连接的信息
//        binding.wifiNameTv.text = "Wi-Fi:" +" "+wifiInfo.ssid.replace("\"", "")
        binding.wifiNameTv.text = "Wi-Fi:" + " " + WifiSSidUtil.getWifiSSID(App.context)

        val networkIp =  intent.getStringExtra("ip")
        val type =  intent.getStringExtra("from")
        binding.copy.setOnClickListener {
            ClipboardUtils.copyText("${wifiInfo?.bssid}")
            ToastUtils.showLong("Copied")
            LogUtils.d("${wifiInfo?.bssid}------")
//            LogUtils.d("${wifiInfo.macAddress}------")
        }
        binding.tvIP.text =    "IP Address:" +networkIp
        binding.back.setOnClickListener {
            finish()
        }
//        val filter = IntentFilter()
//        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
//        registerReceiver(mReceiver,filter)
//        val res =   wifiManager?.startScan()
//        LogUtils.e("扫描wifi 结果：${MacAddressUtil.getMacAddress(App.context)}")
       val gatewayByWifi =  NetworkUtils.getGatewayByWifi()
        val getIpAddressByWifi =   NetworkUtils.getIpAddressByWifi()
        when (networkIp) {
            gatewayByWifi -> {

                Glide.with(this).load(R.mipmap.icon_router_large).into(binding.imageWarm)

                binding.copy.visibility = View.VISIBLE
                Glide.with(this).load(R.mipmap.icon_green_duigou).into( binding.image1)
                Glide.with(this).load(R.mipmap.icon_huise_gth).into( binding.image2)
                thread {

//                    if (TextUtils.isEmpty(
//                            SPUtils.getInstance()
//                                .getString(gatewayByWifi)
//                        )
//                    ) {
                        val macAdd = MacAddressUtil.getMacAddress(App.context).trim()
                         organization = organiaztionDao?.query(macAdd.substring(0, 8))
                        LogUtils.d("查找到的厂商 = ${organization}--${macAdd.substring(0, 8)}")
                        binding.tvOrganization.text =
                                            "Organization:" + "${organization}"
                                        SPUtils.getInstance()
                                            .put(gatewayByWifi, organization)
//                        listOrga?.forEachIndexed { index, it ->
//                            //获取了解路由器的Mac地址
//                            if (macAdd.contains(
//                                    it.organization.substring(0, 9).replace("-", ":").trim().toLowerCase()
//                                )
//                            ) {
//                                runOnUiThread{
//                                        organization = it.organization.substring(
//                                            it.organization.substring(
//                                                0, 9).length, it.organization.length)
//                                        binding.tvOrganization.text =
//                                            "Organization:" + "${organization}"
//                                        SPUtils.getInstance()
//                                            .put(gatewayByWifi, organization)
//
//                                }
//                            }
//                        }
//                    }else {
//                        runOnUiThread {
//                            binding.tvOrganization.text =
//                                "Organization:" + "${
//                                    SPUtils.getInstance()
//                                        .getString(gatewayByWifi)
//                                }"
//                        }
//                    }

                    /*  thread {
                                    val map = ReadLocalTxt.readFromAssetsToMap(App.context,"dic_mac_company.txt_change")
                                    map.forEach { i, s ->
                //            LogUtils.e("map ==${s.toString().substring(0,s.toString().indexOf(" "))}")
                //            LogUtils.e("map ==${ RegularExpressionUtil.reuglar(s.toString())}")
                                        val content =    RegularExpressionUtil.reuglar(s.toString())
                                        if (content.length > 9) {
                                            // map ==18:16:C9	~   false  ~  18-16-C9	Samsung Electronics Co.,Lt  ~  Samsung Electronics Co.,Lt
                              *//*      LogUtils.e("map ==${ content.substring(0,9).replace("-",":")}~" +
                            "${MacAddressUtil.getMacAddress(App.context).contains(content.substring(0, 9).replace("-", ":")
                    )}~${content.substring(content.indexOf(content.substring(0,9)) )}" +
                            "~${content.substring(content.substring(0,9).length,content.length )}")*//*
                      *//*      LogUtils.e("map ==${ content.substring(0,9).replace("-",":").trim().toLowerCase()}~" +
                                    "${MacAddressUtil.getMacAddress(App.context)}----${MacAddressUtil.getMacAddress(App.context).contains(
                                        RegularExpressionUtil.reuglar(s.toString()).substring(0, 9)
                                            .replace("-", ":").trim().toLowerCase()
                                    )}")*//*
                            //获取了解路由器的Mac地址
                            if (MacAddressUtil.getMacAddress(App.context).trim().contains(
                                   content.substring(0, 9).replace("-", ":").trim().toLowerCase()
                                )
                            ) {
                                runOnUiThread{
                                    organization =  content.substring(content.substring(0,9).length,content.length )
                                    binding.tvOrganization.text = "Organization:"+"${ organization}"
                                    return@runOnUiThread
                                }
                            }
                        }

                    }
                }*/
                }
            }
            getIpAddressByWifi -> {
                Glide.with(this).load(R.mipmap.icon_phone).into(binding.imageWarm)
                binding.copy.visibility = View.INVISIBLE
                binding.tvOrganization.visibility = View.INVISIBLE
                Glide.with(this).load(R.mipmap.icon_green_duigou).into( binding.image1)
                Glide.with(this).load(R.mipmap.icon_huise_gth).into( binding.image2)

            }
            else ->{
                if (type == "confirmed") {// 展示信任
                    Glide.with(this).load(R.mipmap.icon_phone).into(binding.imageWarm)
                    binding.copy.visibility = View.INVISIBLE
                    binding.tvOrganization.visibility = View.INVISIBLE
                    Glide.with(this).load(R.mipmap.icon_green_duigou).into( binding.image1)
                    Glide.with(this).load(R.mipmap.icon_huise_gth).into( binding.image2)
                } else {
                    Glide.with(this).load(R.mipmap.icon_suspicious_warm).into(binding.imageWarm)
                    binding.tvOrganization.visibility = View.INVISIBLE
                    binding.tvOrganization.text = "Organization:"+"${organization}"
                    binding.copy.visibility = View.INVISIBLE
                    Glide.with(this).load(R.mipmap.icon_huise_duigou).into( binding.image1)
                    Glide.with(this).load(R.mipmap.icon_red_gth).into( binding.image2)
                }


            }

        }
        binding.confirmed.setOnClickListener{
//            binding.copy.visibility = View.VISIBLE
//            binding.wifiNameTv.text = "UNKNOWN"
            binding.wifiNameTv.text =  "Wi-Fi:" + " " + WifiSSidUtil.getWifiSSID(App.context)
            binding.tvIP.text =    "IP Address:" +networkIp
            binding.tvOrganization.visibility = View.INVISIBLE
            Glide.with(this).load(R.mipmap.icon_phone).into(binding.imageWarm)
            Glide.with(this).load(R.mipmap.icon_green_duigou).into( binding.image1)
            Glide.with(this).load(R.mipmap.icon_huise_gth).into( binding.image2)

            thread {
                val list =    routerDao?.loadAllRouters()// 获取到数据库中所有ip
                if (list.isNullOrEmpty()) {
                    networkIp?.let {
                        routerDao?.insertRouter(Router(it))
                    }
                } else {
                    list?.forEach {
                        if (it.ip != networkIp) {// 数据库没有才添加
                            networkIp?.let {
                                routerDao?.insertRouter(Router(it))
                            }
                        }
                    }
                }


            }
            if (networkIp == gatewayByWifi) {
                Glide.with(this).load(R.mipmap.icon_router_large).into(binding.imageWarm)
                binding.tvOrganization.visibility = View.VISIBLE
                binding.tvOrganization.text =
                    "Organization:" + "${organization}"
                binding.copy.visibility = View.VISIBLE
            }



        }
        binding.unconfirmed.setOnClickListener{
            binding.copy.visibility = View.INVISIBLE
            binding.wifiNameTv.text = "Wi-Fi:" + " " + WifiSSidUtil.getWifiSSID(App.context)
            binding.tvIP.text =   "IP Address:" +networkIp
            binding.tvOrganization.visibility = View.INVISIBLE
//            binding.tvOrganization.visibility = View.VISIBLE
//            binding.tvOrganization.text = "Organization:"+"${WifiSSidUtil.getWifiSSID(App.context)}"

            Glide.with(this).load(R.mipmap.icon_suspicious_warm).into(binding.imageWarm)
            Glide.with(this).load(R.mipmap.icon_huise_duigou).into( binding.image1)
            Glide.with(this).load(R.mipmap.icon_red_gth).into( binding.image2)
            thread {
                networkIp?.let {  routerDao?.deleteRouterByLastName(it) }

            }

//            binding.pingOrcheck.text =   "Check"
        }
        binding.checkPing.setOnClickListener {
            binding.pingOrcheck.text = "Ping..."
            Thread{
                val delay =  PingDelayUtil.pingDelay(networkIp)
                runOnUiThread {
                    LogUtils.e("PING ===${delay}")
                    if (TextUtils.isEmpty(delay)) {
                        binding.pingOrcheck.text = "~" +Random.nextInt(6,66)+"ms"
                    } else {
                        binding.pingOrcheck.text = "~" + delay
                    }
                }

            }.start()
        }

//        binding.checkPing.setOnClickListener {
//            binding.pingOrcheck.text = "Ping..."
//            Thread{
//            val list =    routerDao?.loadAllRouters()// 遍历打印数据中所有数据
//                runOnUiThread {
//                   list?.forEach {
//                       LogUtils.e("IP==:${it.ip}")
//
//                   }
//                }
//
//            }.start()
//        }
    }

    val mReceiver = object :BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.action?.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)!!) {
              val result =   wifiManager?.scanResults
                LogUtils.e("wifi 列表==${result?.size}")
            }
        }

    }
}