package com.devicewifitracker.android.ui.guide.guide

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityGuideStrategyBinding

class GuideStrategyActivity :BaseActivity<ActivityGuideStrategyBinding>(){

    companion object{
       private var mCB :CallBack?= null
        fun actionOpenAct(context: Context) {
            val intent = Intent(context,GuideStrategyActivity::class.java)
             context.startActivity(intent)

        }
        fun setCallBack(cb:CallBack){
            mCB = cb
        }
    }
    override fun getLayoutId(): Int = R.layout.activity_guide_strategy

    override fun initView(savedInstanceState: Bundle?) {
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.parentView?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        super.initView(savedInstanceState)
        binding.safetyFirstParent.setOnClickListener{
            GuideSafetyLiveActivity.actionOpenAct(this)
        }
        binding.safetySenondParent.setOnClickListener{
            GuideSafetyPlaceActivity.actionOpenAct(this)

        }

        binding.safetyThirdarent.setOnClickListener{
            GuideStrategyOutActivity.actionOpenAct(this)
        }
        binding.back.setOnClickListener {

            mCB?.update()
            finish()
        }
    }

    interface  CallBack{
        fun update()
    }
}