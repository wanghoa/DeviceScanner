package com.devicewifitracker.android.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.devicewifitracker.android.App
import com.devicewifitracker.android.R
import com.devicewifitracker.android.databinding.FragmentDashboardBinding
import com.devicewifitracker.android.model.Infrared
import com.devicewifitracker.android.ui.dashboard.adapter.InfraredAdapter
import com.devicewifitracker.android.ui.setting.SettingActivity

/**
 * 红外探测
 */
class DashboardFragment : Fragment() {

    val list  = ArrayList<Infrared>()


    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
     val picList =  listOf<Int>(
          R.mipmap.icon_item_tv,
          R.mipmap.icon_item_chazuo,
          R.mipmap.icon_item_lamp,
          R.mipmap.icon_item_bedcupboard,
          R.mipmap.icon_tv_cupboard,
          R.mipmap.icon_item_cupboard,
          R.mipmap.icon_item_sofa,
          R.mipmap.icon_item_cgq,
          R.mipmap.icon_item_huasa,
          R.mipmap.icon_item_vase,
          R.mipmap.icon_item_airconditioner,
          R.mipmap.icon_item_router,
          R.mipmap.icon_item_humidifier,
          R.mipmap.icon_item_mirror,
          R.mipmap.icon_item_remotecontrol)
      val textList =   listOf<String>(
          getString(R.string.tv),getString(R.string.socket),getString(R.string.desk_lamp),getString(R.string.bedside_cabinet),getString(R.string.tv_cabinet),getString(R.string.wardrobe),getString(R.string.soft),getString(R.string.smoke_detector),getString(R.string.shower_head),getString(R.string.flower_vase),getString(R.string.aircondition),getString(R.string.router),getString(R.string.humidifier),getString(R.string.mirror),getString(R.string.remote_control))

        for (i in 0..14) {
            list.add(Infrared(picList[i],textList[i]))
        }
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
//        binding?.container?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        binding?.mainSetting.setOnClickListener {
            SettingActivity.actionOpenAct(context)
        }
        binding?.mainRefresh.setOnClickListener {
        }
        val infraredList = ArrayList<Infrared>()
        infraredList.addAll(list)
        val adapter =   InfraredAdapter(infraredList)
        val gridManager = GridLayoutManager(App.context,3)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding?.recyclerView .adapter =  adapter
        binding?.recyclerView.layoutManager = gridManager





        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}