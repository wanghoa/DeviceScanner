package com.devicewifitracker.android.ui.suspicious

import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivitySuspiciousBinding

/**
 * 可疑设备列表
 */
class SuspiciousDevicActivity :BaseActivity<ActivitySuspiciousBinding>() {
    var tableList: ArrayList<ScanResult>? = null
    var adapter : SuspiciousAdapter ?= null
    companion object{
        const val  ACT_NAME = "act_name"
        fun actionOpenAct(context: Context, list:java.util.ArrayList<String>) {
            val  intent = Intent(context, SuspiciousDevicActivity::class.java).apply {
                val bundle = Bundle()
                bundle. putSerializable("list",list as java.io.Serializable)
                putExtras(bundle)
//            putParcelableArrayListExtra()

            }
            context.startActivity(intent)
        }
    }
//    var launcher : ActivityResultLauncher<>?= null
    override fun getLayoutId(): Int  = R.layout.activity_suspicious

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val susList =    intent.getSerializableExtra("list")!! as ArrayList<String>
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.susRoot?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
       val launch =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        }
        binding.back.setOnClickListener {
            finish()
        }


        tableList = ArrayList<ScanResult>()
        adapter = SuspiciousAdapter(susList)
        val layoutManager = LinearLayoutManager(this)
        adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.recyclerViewSuspicious.adapter = adapter
        binding.recyclerViewSuspicious.layoutManager = layoutManager
    }

}