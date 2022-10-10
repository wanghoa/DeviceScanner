package com.devicewifitracker.android.ui.gpu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityGpuBinding

class GpuActivity :BaseActivity<ActivityGpuBinding>() {

    companion object{
        fun actionOpenAct(context: Context) {
            val  intent = Intent(context,GpuActivity::class.java).apply {

            }
            context.startActivity(intent)

        }
    }

    override fun getLayoutId(): Int = R.layout.activity_gpu

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }
}