package com.devicewifitracker.android.ui.subscribe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devicewifitracker.android.BottomNavigationActivity
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivitySubscribeBinding
import com.devicewifitracker.android.ui.setting.PrivacyPolicyActivity
import com.devicewifitracker.android.ui.setting.TermsOfUseActivity
import com.devicewifitracker.android.util.Constant
import com.devicewifitracker.android.util.SubscribeManager

class SubscribeActivity : BaseActivity<ActivitySubscribeBinding>() {

    companion object{
        val FLAG = "from"
        fun actionOpenAct(context: Context,from:String) {
            val intent = Intent(context, SubscribeActivity::class.java).apply {
                putExtra(FLAG,from)
            }
            context.startActivity(intent)

        }
    }
    var type  = SubscribeManager.instance.skuMonth
    override fun getLayoutId(): Int = R.layout.activity_subscribe
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.subscribeMonth.isSelected = true
        binding.closedIv.setOnClickListener{
            if (intent.getStringExtra(FLAG) == Constant.GUIDE_USER_KEY) {
                //如果是从引导页进入，跳转到首页
                startActivity(Intent(this@SubscribeActivity, BottomNavigationActivity::class.java))

            }
                finish()
        }

        binding.tvPrivacy.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }
        binding.tvTerms.setOnClickListener {
            startActivity(Intent(this, TermsOfUseActivity::class.java))
        }
        binding.subscribeMonth.setOnClickListener {
            binding.subscribeMonth.isSelected = true
            binding.subscribeJune.isSelected = false
            type = "one"

        }

        binding.subscribeJune.setOnClickListener {
            type = "six"
            binding.subscribeMonth.isSelected = false
            binding.subscribeJune.isSelected =true
//
        }
        binding.btnSubcribe.setOnClickListener {
            when (type) {
                "one" ->{
                    subscribe(SubscribeManager.instance.skuMonth)
                }
                "six"->{
                    subscribe(SubscribeManager.instance.skuYear)
                }else ->{
                  subscribe(SubscribeManager.instance.skuMonth)
                }
            }
        }
        binding.restorePurchase.setOnClickListener {
            SubscribeManager.instance.restore(this,null)
        }


    }

    private fun subscribe(sku:String){
        SubscribeManager.instance.subscribe(this,sku
        ) {
            if (intent.getStringExtra(FLAG) == Constant.GUIDE_USER_KEY) {
                //如果是从引导页进入，跳转到首页
                startActivity(Intent(this@SubscribeActivity, BottomNavigationActivity::class.java))
            }
            finish()
        }
    }
}