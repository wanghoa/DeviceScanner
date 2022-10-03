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
import com.devicewifitracker.android.ui.setting.SettingActivity
import com.devicewifitracker.android.ui.suspicious.SuspiciousDevicActivity
import com.devicewifitracker.android.ui.table.TableAdapter
import com.devicewifitracker.android.util.Constant
import com.devicewifitracker.android.util.NetworkInfoUtil

class HomeFragment : Fragment(), Handler.Callback {

    var tableList: ArrayList<String>? = null
    var susList: ArrayList<String>? = null
    var adapter: TableAdapter? = null
    var runnable: Runnable? = null
    var progress1: Int = 0
    var mHandler: Handler? = null
    var dialogShowFlag = false
    var isShowDialog = false

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
        binding?.constraint?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        initView()

        return root
    }

    fun initView() {
        // 设置
        binding?.mainSetting.setOnClickListener {
            SettingActivity.actionOpenAct(context)
        }
        // 查看可疑设备列表
        binding?.mainSearching.nextll.setOnClickListener {
            SuspiciousDevicActivity.actionOpenAct(context, susList as ArrayList<String>)
        }


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
                mHandler?.postDelayed(this, 60)
                mHandler?.sendEmptyMessage(1)
            }
        }
        mHandler = Handler(this)
        mHandler?.postDelayed(runnable as Runnable, 0)

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)


    }



    override fun getUserVisibleHint(): Boolean {
        return super.getUserVisibleHint()
    }

    override fun onStart() {
        super.onStart()

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

    @SuppressLint("MissingPermission")
    fun scanwifi() {

        //获取 WIFI名称
        val wifiManager =
            App.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo //获取当前连接的信息
        val dhcpInfo = wifiManager.dhcpInfo  //获取DHCP 的信息
        binding?.main.tvWifiName?.text = "当前连接Wi-Fi:" + " " + wifiInfo.ssid.replace("\"", "")
        binding?.main.tvIpAdress?.text = "IP地址:" + " " + "${NetworkUtils.getIpAddressByWifi()}"
        //  +"\nip:" +"${NetworkUtils.getIPAddress(true)}"与上方获取值相同
        mHandler?.postDelayed(object : Runnable {
            override fun run() {
                val list = NetworkInfoUtil.getConnectIp()
              activity?. runOnUiThread {
                    tableList?.clear()
                    list.add(0, NetworkUtils.getGatewayByWifi())// 路由IP
                    if (!TextUtils.isEmpty(NetworkUtils.getIpAddressByWifi())) {
                        list.add(0, NetworkUtils.getIpAddressByWifi())// 手机IP
                    }
                    list?.let { tableList?.addAll(it) }
                    adapter?.notifyDataSetChanged()
                }
                //  通过Wi-Fi名称 过滤是否是可信网络（）
                tableList?.forEachIndexed { index, scanResult ->
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


    }

    override fun onStop() {
        super.onStop()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mHandler?.removeCallbacksAndMessages(runnable)
//        _binding = null
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
                    binding?.mainSearching.suspiciousDeviceTV.text =
                        "Found ${tableList?.size} suspicious device"
                    binding?.mainSearching.nextll.visibility = View.VISIBLE
                    binding?.mainSearching.textDes.visibility = View.VISIBLE
                    binding.mainSearching.recyclerView.layoutParams?.let {
                        it.height = ConvertUtils.dp2px(180f)
                    }
//                    progress1 = 0
                } else {
                    progress1++
                    binding?.mainSearching.tvScanning.text =
                        "Scanning" + " " + "${progress1}" + " " + "from" + " " + "${254}" + " " + "IP range"
                }
                binding?.mainSearching.progressBar.progress = progress1
            }
        }
        return false
    }
}