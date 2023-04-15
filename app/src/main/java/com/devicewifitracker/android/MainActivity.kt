package com.devicewifitracker.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager.OnNetworkActiveListener
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.*
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.*
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityMainBinding
import com.devicewifitracker.android.ui.guide.guide.GuideStrategyActivity
import com.devicewifitracker.android.ui.setting.SettingActivity
import com.devicewifitracker.android.ui.suspicious.SuspiciousDevicActivity
import com.devicewifitracker.android.ui.table.TableAdapter
import com.devicewifitracker.android.util.Constant
import com.devicewifitracker.android.util.LocationUtils
import com.devicewifitracker.android.util.NetworkInfoUtil
import com.devicewifitracker.android.util.PermissionUtil
import java.util.logging.Logger

/**
 * 当前类未使用
 */

class MainActivity : BaseActivity<ActivityMainBinding>(), Handler.Callback,
    GuideStrategyActivity.CallBack {
    var tableList: ArrayList<String>? = null
    var susList: ArrayList<String>? = null
    var adapter: TableAdapter? = null
    var runnable: Runnable? = null
    var progress1: Int = 0
    var mHandler: Handler? = null
    var dialogShowFlag = false
    var isShowDialog = false
    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mHandler = Handler(this)
        SPUtils.getInstance().put(Constant.GUIDE_KEY, true)
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.rootView?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        binding.mainWifiDetect.setTextColor(getColor(R.color.color_59D2B1))
        binding.mainWifiDetect.setOnClickListener {
            binding.mainWifiDetect.setTextColor(getColor(R.color.color_59D2B1))
            binding.mainGuide.setTextColor(getColor(R.color.color_666666))

        }
        binding.mainGuide.setOnClickListener {
            GuideStrategyActivity.setCallBack(this)
            binding.mainWifiDetect.setTextColor(getColor(R.color.color_666666))
            binding.mainGuide.setTextColor(getColor(R.color.color_59D2B1))
            GuideStrategyActivity.actionOpenAct(this)
            val drawable = ContextCompat.getDrawable(this, R.mipmap.icon_wifi_defalt)
            drawable?.setBounds(0, 0, 60, 60)
            binding.mainWifiDetect.setCompoundDrawables(null, drawable, null, null)
        }
        NetworkInfoUtil.sendDataToLoacl()
        binding?.main!!.btnScan.setOnClickListener {
            binding?.mainSearching!!.mainSearchingLl.visibility = View.VISIBLE
            binding?.main!!.mainLl.visibility = View.GONE
            binding.tvAppName.visibility = View.GONE

            binding.mainSearching.tvFinishDescribe.text = getString(R.string.main_searching_closed)
            val barHeight1 = BarUtils.getStatusBarHeight()//获取状态栏高度
            binding?.mainSearching?.mainSearchingLl!!.setPadding(
                0,
                ConvertUtils.dp2px(40.toFloat()) - barHeight1,
                0,
                0
            )


            setProgress()
        }

        binding.deviceScanner.setOnClickListener {
            var permissionArray: Array<String?>
            permissionArray = arrayOf(Manifest.permission.CAMERA)
            // 动态开启权限（不处理会导致启动相机服务失败）
            PermissionUtil.requestPermission(this, permissionArray)?.subscribe { granted ->
                if (!granted!!) {

                } else {
                    DeviceScannerActivity.actionOpenActivity(this)

                }
            }
        }


        // 设置
        binding?.mainSetting!!.setOnClickListener {
            SettingActivity.actionOpenAct(this)
        }
        // 查看可疑设备列表
        binding.mainSearching.nextll.setOnClickListener {
//            SuspiciousDevicActivity.actionOpenAct(this, susList as ArrayList<String>)
        }


        susList = ArrayList<String>()
        tableList = ArrayList<String>()
        adapter = TableAdapter(tableList!!)
        val layoutManager = LinearLayoutManager(this)
        adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.mainSearching.recyclerView.adapter = adapter
        binding.mainSearching.recyclerView.layoutManager = layoutManager
        binding.mainSearching.recyclerView.layoutParams?.let {
            it.height = ConvertUtils.dp2px(260f)
        }



        val drawable = ContextCompat.getDrawable(this, R.mipmap.icon_wifi)
        drawable?.setBounds(0, 0, 70, 70)
        binding.mainWifiDetect.setCompoundDrawables(null, drawable, null, null)
//        binding.mainWifiDetect.isSelected = true
//        binding.mainWifiDetect.isEnabled = true

        val drawable1 = ContextCompat.getDrawable(this, R.mipmap.icon_guide)
        drawable1?.setBounds(0, 0, 50, 60)
        binding.mainGuide.setCompoundDrawables(null, drawable1, null, null)
        NetworkUtils.isAvailableByPing()//用ping判断网络是否可用

    }


    @SuppressLint("MissingPermission")
    fun scanwifi() {

        //获取 WIFI名称
        val wifiManager =
            App.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo //获取当前连接的信息
        val dhcpInfo = wifiManager.dhcpInfo  //获取DHCP 的信息
        binding?.main!!.tvWifiName.text = "当前连接Wi-Fi:" + " " + wifiInfo.ssid.replace("\"", "")
        binding?.main!!.tvIpAdress?.text = "IP地址:" + " " + "${NetworkUtils.getIpAddressByWifi()}"
        //  +"\nip:" +"${NetworkUtils.getIPAddress(true)}"与上方获取值相同
//etworkUtils.getGatewayByWifi() 路由器ip
        mHandler?.postDelayed(object : Runnable {
            override fun run() {
//                val list = NetworkInfoUtil.readArp(binding.tvAppName)
                val list = NetworkInfoUtil.getConnectIp()
                runOnUiThread {
                    tableList?.clear()
//                    list.add(0,NetworkUtils.getIpAddressByWifi())
                    list.add(0, NetworkUtils.getGatewayByWifi())// 路由IP
                    if (!TextUtils.isEmpty(NetworkUtils.getIpAddressByWifi())) {
                        list.add(0, NetworkUtils.getIpAddressByWifi())// 手机IP
                    }
                    list?.let { tableList?.addAll(it) }
                    adapter?.notifyDataSetChanged()
                }
                //  通过Wi-Fi名称 过滤是否是可信网络（）
                tableList?.forEachIndexed { index, scanResult ->
//                    LogUtils.e("${NetworkUtils.getIpAddressByWifi()}----${scanResult}")
                    //  前者自己IP 后者 路由IP
                    if (!NetworkUtils.getIpAddressByWifi()
                            .equals(scanResult) && !NetworkUtils.getGatewayByWifi()
                            .equals(scanResult)
                    ) {
                        susList?.add(scanResult)
                    }
                }
            }
        }, 300)


//        var permissionArray: Array<String?>
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            permissionArray = arrayOf(
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.CHANGE_WIFI_STATE
//            )
//        } else {
//            permissionArray = arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.CHANGE_WIFI_STATE
//            )
//        }
//        // 获取权限
//        PermissionUtil.requestPermission(
//            this, permissionArray
//        )?.subscribe { granted ->
//            LogUtils.d("是否开启了权限 =${granted}")
//            if (!granted!!) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    if (!LocationUtils.isGpsEnabled()) {
//                        LogUtils.d("1-------------------------------")
//                        //开启GPS定位
//                        LocationUtils.openGpsSettings()
//                    } else {
//
//                    }
//                } else {
//                    // 先判断Android系统，9.0以上除了 需要定位权限还需要开启GPS才能获取WIFI名称
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//                        if (!LocationUtils.isGpsEnabled()) {
//                            //开启GPS定位
//                            LocationUtils.openGpsSettings()
//                        } else {
//                        }
//                    }
//                }
//                } else {
//
//                }
//            }
//        }
    }

    fun setProgress() {
        runnable = object : Runnable {
            override fun run() {
                mHandler?.postDelayed(this, 60)
                mHandler?.sendEmptyMessage(1)
            }
        }

        mHandler?.postDelayed(runnable as Runnable, 0)

    }

    override fun handleMessage(p0: Message): Boolean {
        when (p0.what) {
            1 -> {
                if (progress1 == 254) {
                    runnable?.let { mHandler?.removeCallbacks(it) }
                    binding.mainSearching.tvScanning.text =
                        getString(R.string.main_searching_finish)
                    binding.mainSearching.tvFinishDescribe.text =
                        getString(R.string.main_searching_finish_describe)

                    binding.mainSearching.suspiciousDeviceTV.visibility = View.VISIBLE
                    binding.mainSearching.suspiciousDeviceTV.text =
                        "Found ${tableList?.size} suspicious device"
                    binding.mainSearching.nextll.visibility = View.VISIBLE
                    binding.mainSearching.textDes.visibility = View.VISIBLE
                    binding.mainSearching.recyclerView.layoutParams?.let {
                        it.height = ConvertUtils.dp2px(180f)
                    }
//                    progress1 = 0
                } else {
                    progress1++
                    binding.mainSearching.tvScanning.text =
                        "Scanning" + " " + "${progress1}" + " " + "from" + " " + "${254}" + " " + "IP range"
                }
                binding.mainSearching.progressBar.progress = progress1
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()

        if (dialogShowFlag) {
            NetworkUtils.registerNetworkStatusChangedListener(object : OnNetworkActiveListener,
                NetworkUtils.OnNetworkStatusChangedListener {
                override fun onNetworkActive() {
                }

                override fun onDisconnected() {
                }

                override fun onConnected(networkType: NetworkUtils.NetworkType?) {
                    // 判断Wi-Fi是否打开 ; 判断Wi-Fi是否是连接状态
                    if (NetworkUtils.getWifiEnabled() && NetworkUtils.isWifiConnected()) {
                        scanwifi()
                        isShowDialog = false
                    }
                }
            })
        }
        // 判断Wi-Fi是否打开 ; 判断Wi-Fi是否是连接状态
        if (NetworkUtils.getWifiEnabled() && NetworkUtils.isWifiConnected()) {
            scanwifi()

        } else {
            // 打开网络设置界面  TODO 弹出窗口提示 连接Wi-Fi或 打开Wi-Fi
            ToastUtils.showLong("连接WI-FI")
            if (!isShowDialog) {
                val buidler = AlertDialog.Builder(this)
                buidler.setTitle("")
                buidler.setMessage("请连接无线网络")
                buidler.setCancelable(true)
                buidler.setPositiveButton("确认", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        NetworkUtils.openWirelessSettings()
                        dialogShowFlag = true
                        isShowDialog = true
                        p0?.dismiss()
                    }

                })
                buidler.setNegativeButton("关闭", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0?.dismiss()
                    }
                })
                buidler.create().show()

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun update() {
        binding.mainWifiDetect.setTextColor(getColor(R.color.color_59D2B1))
        binding.mainGuide.setTextColor(getColor(R.color.color_666666))
        val drawable = ContextCompat.getDrawable(this, R.mipmap.icon_wifi)
        drawable?.setBounds(0, 0, 70, 70)
        binding.mainWifiDetect.setCompoundDrawables(null, drawable, null, null)
    }

}