package com.devicewifitracker.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.*
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.*
import com.devicewifitracker.android.App
import com.devicewifitracker.android.R
import com.devicewifitracker.android.databinding.FragmentHomeBinding
import com.devicewifitracker.android.room.dao.RouterDao
import com.devicewifitracker.android.room.entity.Router
import com.devicewifitracker.android.ui.setting.SettingActivity
import com.devicewifitracker.android.ui.subscribe.SubscribeActivityNew
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

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var arpList: List<String>? = ArrayList()
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

        arpList =  NetworkInfoUtil.pingIp()// 通过ping获取局域网ip
        initView()
        return root
    }

    fun initView() {
        SPUtils.getInstance().put(AGREEMENT_KEY, false)

        // 设置
        binding?.mainSetting.setOnClickListener {
            SettingActivity.actionOpenAct(context)
        }
        // 查看可疑设备列表
        binding?.mainSearching.nextll.setOnClickListener {
            if (!SubscribeManager.instance.isSubscribe()) {
//                context?.let { it1 -> SubscribeActivity.actionOpenAct(it1,"") }
                context?.let { it1 -> SubscribeActivityNew.actionOpenAct(it1, "") }
                return@setOnClickListener
            }
            SuspiciousDevicActivity.actionOpenAct(
                context,
                susList as ArrayList<String>,
                "HomeFragment"
            )
        }

        routerDao = RoomGetDao.getRouterDao()
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
    }

    fun setProgress() {
        runnable = object : Runnable {
            override fun run() {
                //此处可以使用 valueAnimator 动画进行优化
                when (progress1) {

                    in 100..150 -> {
                        mHandler?.postDelayed(this, 300)
                    }
                    in 150..200 -> {
                        mHandler?.postDelayed(this, 150)
                    }
                    in 200..240 -> {
                        mHandler?.postDelayed(this, 80)
                    }
                    in 240..254 -> {
                        mHandler?.postDelayed(this, 60)
                    }
                    else -> {
                        mHandler?.postDelayed(this, 460)
                    }
                }
                mHandler?.sendEmptyMessage(1)
            }
        }
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
        val wifiManager = App.context.getSystemService(Context.WIFI_SERVICE) as WifiManager

//        binding?.main.tvWifiName?.text = "当前连接Wi-Fi:" + " " + wifiInfo.ssid.replace("\"", "")
        binding?.main.tvWifiName?.text =
            getString(R.string.wifi) + " " + WifiSSidUtil.getWifiSSID(App.context)
        binding?.main.tvIpAdress?.text =
            getString(R.string.ip) + " " + "${NetworkUtils.getIpAddressByWifi()}"
        binding?.main.describe?.text = getString(R.string.home_des)
        //  +"\nip:" +"${NetworkUtils.getIPAddress(true)}"与上方获取值相同
        mHandler?.postDelayed(object : Runnable {
            override fun run() {
                // TODO 这里是主线程
                thread {
                    //  var arpList = NetworkInfoUtil.readArp1()// 获取到的数据数量与iOS一致

                    LogUtils.d("Scanner ", "run: pingIp===" + arpList?.size)

                    val listIp = routerDao?.loadAllRouters()// 获取数据库中的所有数据
                    roomListIp = listIp
//              activity?. runOnUiThread {
                    tableList?.clear()
                    if (!TextUtils.isEmpty(NetworkUtils.getIpAddressByWifi())) {
//                        list.add(0, NetworkUtils.getIpAddressByWifi())// 手机IP
//                        list.add(1, NetworkUtils.getGatewayByWifi())// 路由IP
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
                                getString(R.string.found) + " ${susList?.size} " + getString(R.string.suspicious_devices)
                            adapter?.notifyDataSetChanged()
                        }
                    }, 300)

//                }
                    //  通过Wi-Fi名称 过滤是否是可信网络（）
                    arpList?.forEachIndexed { index, scanResult ->
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
                    //  NetworkScanner.scan()// 耗时任务 在子线程执行
                }
            }
        }, 3000)
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
                        getString(R.string.found) + " ${susList?.size} " + getString(R.string.suspicious_devices)

                    binding?.mainSearching.nextll.visibility = View.VISIBLE
                    binding?.mainSearching.textDes.visibility = View.VISIBLE
                    binding.mainSearching.recyclerView.layoutParams?.let {
                        it.height = ConvertUtils.dp2px(180f)
                    }
//                    progress1 = 0
                    SPUtils.getInstance().put(AGREEMENT_KEY, true)
                    binding.mainSearching.progressTv.text = "${100}%"
                } else {
                    if (this.isAdded) {
                        progress1++
                        binding?.mainSearching.tvScanning.text =
                            getString(R.string.scanning) + " " + "${progress1}" + " " + getString(R.string.from) + " " + "${254}" + " " + getString(
                                R.string.ip_range
                            )
                        var percentage = (progress1 * 100.0 / 254.0).toInt()
                        var progressText = "${percentage}%"
                        binding.mainSearching.progressTv.text = progressText
                    }

                }
                binding?.mainSearching.progressBar.progress = progress1
            }
        }
        return false
    }

}