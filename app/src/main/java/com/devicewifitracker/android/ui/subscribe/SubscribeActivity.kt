package com.devicewifitracker.android.ui.subscribe

import android.os.Bundle
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivitySubscribeBinding
import com.devicewifitracker.android.util.SubscribeManager

class SubscribeActivity : BaseActivity<ActivitySubscribeBinding>() {
    var type  = SubscribeManager.instance.skuMonth
    override fun getLayoutId(): Int = R.layout.activity_subscribe
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.subscribeMonth.isSelected = true
        binding.closedIv.setOnClickListener{
            finish()
        }

        binding.tvPrivacy.setOnClickListener {
//            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }
        binding.tvTerms.setOnClickListener {
//            startActivity(Intent(this, TermsOfUseActivity::class.java))
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
                }
            }
        }


    }

    private fun subscribe(sku:String){
        SubscribeManager.instance.subscribe(this,sku
        ) { finish() }
    }
}