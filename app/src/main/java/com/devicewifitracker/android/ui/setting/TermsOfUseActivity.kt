package com.devicewifitracker.android.ui.setting

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityTermsOfUseBinding
import com.devicewifitracker.android.util.Constant

/**
 * 服务条款
 */
class TermsOfUseActivity : BaseActivity<ActivityTermsOfUseBinding>() {

    override fun getLayoutId() = R.layout.activity_terms_of_use

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.back.setOnClickListener{
            finish()
        }

        binding.webview.loadUrl(Constant.TERM_OF_USE_URL)
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

    override fun onResume() {
        super.onResume()
        binding.webview.onResume()
    }
    override fun onPause() {
        super.onPause()
        binding.webview.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}