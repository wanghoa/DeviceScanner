package com.devicewifitracker.android.ui.notifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.devicewifitracker.android.databinding.FragmentNotificationsBinding
import com.devicewifitracker.android.ui.guide.guide.GuideSafetyLiveActivity
import com.devicewifitracker.android.ui.guide.guide.GuideSafetyPlaceActivity
import com.devicewifitracker.android.ui.guide.guide.GuideStrategyOutActivity

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.parentView?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        initListener()
        return root
    }

    fun initListener() {
        binding.safetyFirstParent.setOnClickListener{
            GuideSafetyLiveActivity.actionOpenAct(context)
        }
        binding.safetySenondParent.setOnClickListener{
            GuideSafetyPlaceActivity.actionOpenAct(context)

        }

        binding.safetyThirdarent.setOnClickListener{
            GuideStrategyOutActivity.actionOpenAct(context)
        }
    }



    override fun getUserVisibleHint(): Boolean {
        return super.getUserVisibleHint()
    }



    override fun onResume() {
        super.onResume()


    }

    override fun onStop() {
        super.onStop()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}