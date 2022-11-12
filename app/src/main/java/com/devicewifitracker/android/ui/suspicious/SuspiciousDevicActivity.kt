package com.devicewifitracker.android.ui.suspicious

import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivitySuspiciousBinding
import com.devicewifitracker.android.room.dao.RouterDao
import com.devicewifitracker.android.room.database.AppDatabase
import kotlin.concurrent.thread

/**
 * 可疑设备列表
 */
class SuspiciousDevicActivity :BaseActivity<ActivitySuspiciousBinding>() {
    var tableList: ArrayList<ScanResult>? = null
    var adapter : SuspiciousAdapter ?= null
    var routerDao: RouterDao?= null
    var mSusList: ArrayList<String>?= null
    var type:String?= null
    companion object{
        const val  ACT_NAME = "act_name"
        fun actionOpenAct(context: Context?, list:java.util.ArrayList<String>,from:String) {
            val  intent = Intent(context, SuspiciousDevicActivity::class.java).apply {
                val bundle = Bundle()
                bundle. putSerializable("list",list as java.io.Serializable)
                putExtra("from",from)
                putExtras(bundle)
//            putParcelableArrayListExtra()

            }
            context?.startActivity(intent)
        }
    }
//    var launcher : ActivityResultLauncher<>?= null
    override fun getLayoutId(): Int  = R.layout.activity_suspicious

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val susList =    intent.getSerializableExtra("list")!! as ArrayList<String>
        type =   intent.getStringExtra("from")
         mSusList = susList
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.susRoot?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
       val launch =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        }
        binding.back.setOnClickListener {
            finish()
        }
        routerDao =  AppDatabase.getDatabase(this).routerDao()



        tableList = ArrayList<ScanResult>()
//        adapter = SuspiciousAdapter(susList)
        adapter = SuspiciousAdapter(mSusList)
        val layoutManager = LinearLayoutManager(this)
        adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.recyclerViewSuspicious.adapter = adapter
        binding.recyclerViewSuspicious.layoutManager = layoutManager
    }

    override fun onResume() {
        super.onResume()

        thread {
           val listIp = routerDao?.loadAllRouters()// 从数据库获取可信任设备 如果存在就从不可信任列表中移除
            if (listIp.isNullOrEmpty())return@thread
            listIp?.forEachIndexed { index, router ->
                if (mSusList!!.contains(router.ip)) {
                    mSusList!!.remove(router.ip)
                }
            }
            runOnUiThread{
                adapter?.notifyDataSetChanged()
                if (mSusList?.size == 0 && type !="HomeFragment" ) {
                    // 处理当从不可信列表进入详情 ，只有一条数据也被置为受信任 直接返回到主页
                    finish()
                }
            }

        }

        if (mSusList?.size == 0 || mSusList.isNullOrEmpty()) {
            binding.empty.visibility = View.VISIBLE
        } else {
            binding.empty.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
    }
    override fun onStop() {
        super.onStop()
        type = ""
    }

}