package com.devicewifitracker.android.ui.guide.guide

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityGuideSafetyLiveBinding
import com.devicewifitracker.android.ui.player.VideoPlayerActivity

class GuideSafetyLiveActivity :BaseActivity<ActivityGuideSafetyLiveBinding>() {
    companion object{
        fun actionOpenAct(context: Context) {
            val intent = Intent(context,GuideSafetyLiveActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun getLayoutId(): Int = R.layout.activity_guide_safety_live

    override fun initView(savedInstanceState: Bundle?) {
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.rootview?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        super.initView(savedInstanceState)
        binding.back.setOnClickListener {
            finish()
        }
        binding.videoCgqIv.setOnClickListener {
           VideoPlayerActivity.actionOpenAct(this,R.raw.video_chuanganqi)
        }
        binding.videoCzIv.setOnClickListener {
            VideoPlayerActivity.actionOpenAct(this,R.raw.video_chazuo)
        }
        binding.videoRouterIv.setOnClickListener {
            VideoPlayerActivity.actionOpenAct(this,R.raw.video_router)
        }
    }
}