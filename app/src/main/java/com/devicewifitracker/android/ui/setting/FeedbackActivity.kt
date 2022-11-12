package com.devicewifitracker.android.ui.setting

import android.os.Bundle
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityFeedbackBinding
import com.devicewifitracker.android.util.SendEmail

class FeedbackActivity : BaseActivity<ActivityFeedbackBinding>() {
    override fun getLayoutId(): Int =  R.layout.activity_feedback
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        SendEmail.sendEmail(this)

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}