package com.devicewifitracker.android.ui.setting

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityPrivayPolicyBinding
import com.devicewifitracker.android.util.Constant


class PrivacyPolicyActivity : BaseActivity<ActivityPrivayPolicyBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_privay_policy

    override fun initView(savedInstanceState: Bundle?) {
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.rootView?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        super.initView(savedInstanceState)
        binding.back.setOnClickListener { finish() }
        binding.webview.loadUrl(Constant.PRIVAY_POLICY)
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        binding.webview.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //使用WebView加载显示url
                view.loadUrl(url)
                //返回true
                return true
            }
        })

    }
}