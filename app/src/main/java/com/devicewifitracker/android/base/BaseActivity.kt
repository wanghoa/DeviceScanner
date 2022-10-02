package com.devicewifitracker.android.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.BarUtils
import com.devicewifitracker.android.R

abstract class BaseActivity<DBinding : ViewDataBinding> : AppCompatActivity() {
    lateinit var binding: DBinding
    abstract  fun getLayoutId() :Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestedOrientation  = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = DataBindingUtil.setContentView(this,getLayoutId())
        //让view 与 model 感应起来 影响界面更新
        binding.lifecycleOwner  = this

        initView(savedInstanceState)
        initData()
    }

    open fun initView(savedInstanceState: Bundle?) {
        //设置状态栏颜色
//        StatusBarUtil.setColor(this,getColor(R.color.black))
        // Black J 库更改状态栏颜色 如果没问题 可以将上面的库替换掉。
        BarUtils.setStatusBarColor(this,getColor(R.color.black))
//        StatusBarUtil.setLightMode(this)
    }
    open fun initData() {}
}