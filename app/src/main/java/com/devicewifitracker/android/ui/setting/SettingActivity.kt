package com.devicewifitracker.android.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivitySettingBinding
import com.devicewifitracker.android.ui.subscribe.SubscribeActivity
import com.devicewifitracker.android.ui.subscribe.SubscribeActivityNew
import com.devicewifitracker.android.util.OpenGooglePlay
import com.devicewifitracker.android.util.SubscribeManager

class SettingActivity : BaseActivity<ActivitySettingBinding>(){
    companion object{
        fun actionOpenAct(context: Context?) {
            val intent = Intent(context,SettingActivity::class.java)
            context?.startActivity(intent)

        }
    }
    override fun getLayoutId(): Int = R.layout.activity_setting
    override fun initView(savedInstanceState: Bundle?) {
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.rootView?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        super.initView(savedInstanceState)
        binding.back.setOnClickListener { finish() }
        binding.rePay.setOnClickListener{
            SubscribeManager.instance.restore(this,null)

        }
        binding.pay.setOnClickListener{
//            startActivity(Intent(this@SettingActivity, SubscribeActivity::class.java))
            startActivity(Intent(this@SettingActivity, SubscribeActivityNew::class.java))
        }
        //隐私
        binding.privacyPolicy.setOnClickListener {
            startActivity(Intent(this,PrivacyPolicyActivity::class.java))
        }
        // 反馈
        binding.keyback.setOnClickListener {
            startActivity(Intent(this,FeedbackActivity::class.java))
        }
        //评价
        binding.leaveMessage.setOnClickListener {
            OpenGooglePlay.openGooglePlayStore(this)
        }
        //服务
        binding.service.setOnClickListener {
            startActivity(Intent(this,TermsOfUseActivity::class.java))
        }
        //分享
        binding.share.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"

            var content = getString(R.string.app_name)
//            var content = "https://play.google.com/store/apps/details?id=$packageName"
            var title = "Let me recommend you this application '${getString(R.string.app_name)}'"

            intent.putExtra(Intent.EXTRA_TITLE, title)
            intent.putExtra(Intent.EXTRA_TEXT, content)


            val chooserIntent = Intent.createChooser(intent, "Share To：")

            startActivity(chooserIntent)
        }

    }

}