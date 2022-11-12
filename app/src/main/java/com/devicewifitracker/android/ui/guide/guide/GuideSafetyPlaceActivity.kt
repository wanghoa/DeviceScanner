package com.devicewifitracker.android.ui.guide.guide

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityGuideSafetyPlaceBinding

class GuideSafetyPlaceActivity : BaseActivity<ActivityGuideSafetyPlaceBinding>() {
    companion object{
        fun actionOpenAct(context: Context?) {
            val intent = Intent(context,GuideSafetyPlaceActivity::class.java)
            context?.startActivity(intent)
        }
    }
    override fun getLayoutId(): Int = R.layout.activity_guide_safety_place
    override fun initView(savedInstanceState: Bundle?) {
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.rootview?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        super.initView(savedInstanceState)
        binding.back.setOnClickListener {
            finish()
        }
    }
}