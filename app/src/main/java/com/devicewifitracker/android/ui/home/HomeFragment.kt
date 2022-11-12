package com.devicewifitracker.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.*
import com.devicewifitracker.android.App
import com.devicewifitracker.android.R
import com.devicewifitracker.android.databinding.FragmentHomeBinding
import com.devicewifitracker.android.room.dao.OrganizationDao
import com.devicewifitracker.android.room.dao.RouterDao
import com.devicewifitracker.android.room.database.AppDatabase
import com.devicewifitracker.android.room.entity.Organization
import com.devicewifitracker.android.room.entity.Router
import com.devicewifitracker.android.ui.setting.SettingActivity
import com.devicewifitracker.android.ui.subscribe.SubscribeActivity
import com.devicewifitracker.android.ui.suspicious.SuspiciousDevicActivity
import com.devicewifitracker.android.ui.table.TableAdapter
import com.devicewifitracker.android.util.*
import com.devicewifitracker.android.util.Constant.AGREEMENT_KEY
import kotlin.concurrent.thread

class HomeFragment : Fragment(), Handler.Callback {

    private var tableList: ArrayList<String>? = null
    private var susList: ArrayList<String>? = null
    private var adapter: TableAdapter? = null
    private var runnable: Runnable? = null
    private var progress1: Int = 0
    private var mHandler: Handler? = null
    private var dialogShowFlag = false
    private var isShowDialog = false
    private var routerDao: RouterDao? = null

    //    private var organiaztionDao: OrganizationDao? = null
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mHandler = Handler(this)
        SPUtils.getInstance().put(Constant.GUIDE_KEY, true)
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
//        binding?.constraint?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        initView()

        return root
    }

    fun initView() {
        SPUtils.getInstance().put(AGREEMENT_KEY,false)
//      val result =   AssetsUtil.getTex(App.context,"dic_mac_company.txt")
//        val result = ReadLocalTxt.readFromAssets(App.context,"dic_mac_company.txt")
//        LogUtils.d("Dic_mac_company =${result}")
        // 设置
        binding?.mainSetting.setOnClickListener {
            SettingActivity.actionOpenAct(context)
        }
        // 查看可疑设备列表
        binding?.mainSearching.nextll.setOnClickListener {
            if (!SubscribeManager.instance.isSubscribe()) {
                context?.let { it1 -> SubscribeActivity.actionOpenAct(it1,"") }
               return@setOnClickListener
            }
            SuspiciousDevicActivity.actionOpenAct(
                context,
                susList as ArrayList<String>,
                "HomeFragment"
            )
        }

        routerDao = RoomGetDao.getRouterDao()
//        organiaztionDao = RoomGetDao.getOrganizationDao()
        susList = ArrayList<String>()
        tableList = ArrayList<String>()
        adapter = TableAdapter(tableList!!)
        val layoutManager = LinearLayoutManager(context)
        adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding?.mainSearching.recyclerView.adapter = adapter
        binding?.mainSearching.recyclerView.layoutManager = layoutManager
        binding?.mainSearching.recyclerView.layoutParams?.let {
            it.height = ConvertUtils.dp2px(260f)
        }

        binding.mainRefresh.setOnClickListener {
            binding?.mainSearching.mainSearchingLl.visibility = View.GONE
            binding?.main.mainLl.visibility = View.VISIBLE
            binding?.tvAppName.visibility = View.VISIBLE
            progress1 = 0
            runnable?.let { it1 -> mHandler?.removeCallbacks(it1) }
            binding?.mainSearching.suspiciousDeviceTV.visibility = View.GONE
            binding?.mainSearching.nextll.visibility = View.GONE
            binding?.mainSearching.textDes.visibility = View.GONE
        }
        binding?.main.btnScan.setOnClickListener {
            binding?.mainSearching.mainSearchingLl.visibility = View.VISIBLE
            binding?.main.mainLl.visibility = View.GONE
            binding?.tvAppName.visibility = View.GONE

            binding?.mainSearching.tvFinishDescribe.text = getString(R.string.main_searching_closed)
            val barHeight1 = BarUtils.getStatusBarHeight()//获取状态栏高度
            binding?.mainSearching?.mainSearchingLl.setPadding(
                0,
                ConvertUtils.dp2px(40.toFloat()) - barHeight1,
                0,
                0
            )
            setProgress()
        }
/*// 将 存进集合中的数据添加到数据库
        if (TextUtils.isEmpty(
                SPUtils.getInstance()
                    .getString("MAC_ADRESS")
            )
        ) {
            thread {


                val map = ReadLocalTxt.readFromAssetsToMap(App.context,"dic_mac_company.txt")
//            val map = ReadLocalTxt.readFromAssetsToMap(App.context,"dic_mac_company_change.txt")
                map.forEach { i, s ->
                    val content =    RegularExpressionUtil.reuglar(s.toString())// 正则去掉行号
                    if (TextUtils.isEmpty(content)) {
                        return@forEach
                    }
                    if (content.length > 9) {// 一分二十秒
                        // 截取前8位 使用":"替换“-”后转成小写
                        val macAddress =  content.substring(0, 9).replace("-", ":").trim().toLowerCase()
                        val company =  content.substring(content.substring(0, 9).length, content.length)
                        LogUtils.d("数据库======${macAddress}~${company}")
                        organiaztionDao?.insertOrganization(Organization(null,macAddress,company))


                    }
                }
                SPUtils.getInstance()
                    .put("MAC_ADRESS","macAddress")
            }
        }*/


    }

    fun setProgress() {
        runnable = object : Runnable {
            override fun run() {
                when (progress1) {
                    in 20..50 -> {
                        mHandler?.postDelayed(this, 500)
                    }
                    in 50..80 -> {
                        mHandler?.postDelayed(this, 400)
                    }
                    in 80..100 -> {
                        mHandler?.postDelayed(this, 600)
                    }
                    in 100..150 -> {
                        mHandler?.postDelayed(this, 550)
                    }
                    in 150..200 -> {
                        mHandler?.postDelayed(this, 600)
                    }
                    in 200..240 -> {
                        mHandler?.postDelayed(this, 500)
                    }
                    in 240..254 -> {
                        mHandler?.postDelayed(this, 660)
                    }
                    else -> {
                        mHandler?.postDelayed(this, 360)
                    }
                }
                mHandler?.sendEmptyMessage(1)
            }
        }
        mHandler = Handler(this)
        mHandler?.postDelayed(runnable as Runnable, 0)

    }

    override fun onResume() {
        super.onResume()
        if (dialogShowFlag) {
            NetworkUtils.registerNetworkStatusChangedListener(object :
                ConnectivityManager.OnNetworkActiveListener,
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
                val buidler = activity?.let { AlertDialog.Builder(it) }
                buidler?.setTitle("")
                buidler?.setMessage("请连接无线网络")
                buidler?.setCancelable(true)
                buidler?.setPositiveButton("确认", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        NetworkUtils.openWirelessSettings()
                        dialogShowFlag = true
                        isShowDialog = true
                        p0?.dismiss()
                    }
                })
                buidler?.setNegativeButton("关闭", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0?.dismiss()
                    }
                })
                buidler?.create()?.show()
            }
        }
    }

    var roomListIp: List<Router>? = null

    @SuppressLint("MissingPermission")
    fun scanwifi() {
        //获取 WIFI名称
        val wifiManager =
            App.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo //获取当前连接的信息
        val dhcpInfo = wifiManager.dhcpInfo  //获取DHCP 的信息
//        binding?.main.tvWifiName?.text = "当前连接Wi-Fi:" + " " + wifiInfo.ssid.replace("\"", "")
        binding?.main.tvWifiName?.text = getString(R.string.wifi) + " " + WifiSSidUtil.getWifiSSID(App.context)
        binding?.main.tvIpAdress?.text = getString(R.string.ip) + " " + "${NetworkUtils.getIpAddressByWifi()}"
        binding?.main.describe?.text = getString(R.string.home_des)
        //  +"\nip:" +"${NetworkUtils.getIPAddress(true)}"与上方获取值相同
        mHandler?.postDelayed(object : Runnable {
            override fun run() {
                // TODO 这里是主线程
                thread {
//                    NetworkInfoUtil.sendDataToLoacl()
                    val list = NetworkInfoUtil.getConnectIp()
                    NetworkInfoUtil.readArp(binding.tvAppName)
//                    list.forEach {
//                        LogUtils.e("局域网ip ====${it}")
//                    }
                    val listIp = routerDao?.loadAllRouters()// 获取数据库中的所有数据
                    roomListIp = listIp
//                    listIp?.forEach {
//                        LogUtils.e("数据库中ip ====${it}")
//                    }
//              activity?. runOnUiThread {
                    tableList?.clear()

                    if (!TextUtils.isEmpty(NetworkUtils.getIpAddressByWifi())) {
                        list.add(0, NetworkUtils.getIpAddressByWifi())// 手机IP
                        list.add(1, NetworkUtils.getGatewayByWifi())// 路由IP
                        tableList?.add(0, NetworkUtils.getIpAddressByWifi())// 手机IP
                        tableList?.add(1, NetworkUtils.getGatewayByWifi())// 路由IP
                        if (!listIp.isNullOrEmpty()) {
                            listIp.forEachIndexed { index, router ->
                                if (!tableList!!.contains(router.ip)) {
                                    tableList?.add(router.ip)
                                }

                            }
                        }
                    }
                    mHandler?.postDelayed(object : Runnable {
                        override fun run() {
                            roomListIp?.forEachIndexed { index, router ->
                                if (susList!!.contains(router.ip)) {
                                    susList!!.remove(router.ip)
                                }
                            }
                            binding?.mainSearching.suspiciousDeviceTV.text =
                              getString(R.string.found)  +" ${susList?.size} "+getString(R.string.suspicious_devices)
                            adapter?.notifyDataSetChanged()
                        }
                    }, 300)
                    // list?.let { tableList?.addAll(it) }

//                }
                    //  通过Wi-Fi名称 过滤是否是可信网络（）
                    list?.forEachIndexed { index, scanResult ->
                        //  前者自己IP 后者 路由IP
                        if (!NetworkUtils.getIpAddressByWifi()
                                .equals(scanResult) && !NetworkUtils.getGatewayByWifi()
                                .equals(scanResult)
                        ) {
                            if (!susList!!.contains(scanResult)) {
                                susList?.add(scanResult)
                            }

                        }
                    }
                }
            }
        }, 300)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mHandler?.removeCallbacksAndMessages(runnable)
    }

    override fun handleMessage(p0: Message): Boolean {
        when (p0.what) {
            1 -> {
                if (progress1 == 254) {
                    runnable?.let { mHandler?.removeCallbacks(it) }
                    binding?.mainSearching.tvScanning.text =
                        getString(R.string.main_searching_finish)
                    binding?.mainSearching.tvFinishDescribe.text =
                        getString(R.string.main_searching_finish_describe)

                    binding?.mainSearching.suspiciousDeviceTV.visibility = View.VISIBLE
                    roomListIp?.forEachIndexed { index, router ->
                        if (susList!!.contains(router.ip)) {
                            susList!!.remove(router.ip)
                        }
                    }
                    binding?.mainSearching.suspiciousDeviceTV.text =
                    getString(R.string.found)  +" ${susList?.size} "+getString(R.string.suspicious_devices)

                    binding?.mainSearching.nextll.visibility = View.VISIBLE
                    binding?.mainSearching.textDes.visibility = View.VISIBLE
                    binding.mainSearching.recyclerView.layoutParams?.let {
                        it.height = ConvertUtils.dp2px(180f)
                    }
//                    progress1 = 0

                    SPUtils.getInstance().put(AGREEMENT_KEY, true)

                } else {
                    progress1++
                    binding?.mainSearching.tvScanning.text =
                        getString(R.string.scanning) + " " + "${progress1}" + " " + getString(R.string.from) + " " + "${254}" + " " + getString(R.string.ip_range)
                }
                binding?.mainSearching.progressBar.progress = progress1
            }
        }
        return false
    }

}