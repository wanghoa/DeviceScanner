package com.devicewifitracker.android

import android.app.Application
import android.content.Context
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.Utils
import com.devicewifitracker.android.util.SubscribeManager

class App : Application() {
    companion object{
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Utils.init(this)
        CrashUtils.init()
        SubscribeManager.instance.initBilling(this)
    }
}