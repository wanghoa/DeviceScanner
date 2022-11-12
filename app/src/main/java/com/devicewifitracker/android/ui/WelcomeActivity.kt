package com.devicewifitracker.android.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.devicewifitracker.android.BottomNavigationActivity
import com.devicewifitracker.android.MainActivity
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityWelcomeBinding
import com.devicewifitracker.android.ui.guide.GuideActivity
import com.devicewifitracker.android.ui.subscribe.SubscribeActivity
import com.devicewifitracker.android.util.Constant
import com.devicewifitracker.android.util.PermissionUtil
import com.devicewifitracker.android.util.SubscribeManager

class WelcomeActivity :BaseActivity<ActivityWelcomeBinding>() {
    override fun getLayoutId(): Int  = R.layout.activity_welcome
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        requestPermission()
    }

    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private fun requestPermission() {
        var permissionArray: Array<String?>
        mHandler.postDelayed(Runnable {
            permissionArray= arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,

            )
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
                permissionArray.plus( Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                permissionArray.plus( Manifest.permission.ACCESS_FINE_LOCATION)
                permissionArray.plus(  Manifest.permission.ACCESS_COARSE_LOCATION)
                permissionArray.plus(  Manifest.permission.ACCESS_WIFI_STATE)
                permissionArray.plus(    Manifest.permission.CHANGE_WIFI_STATE)
                permissionArray.plus(   Manifest.permission.READ_EXTERNAL_STORAGE)
                permissionArray.plus(       Manifest.permission.WRITE_EXTERNAL_STORAGE)


            }
            PermissionUtil.requestPermission(this, permissionArray)?.subscribe { granted ->
                if (!granted!!) {
                    ToastUtils.showShort("You have denied relevant permissions")
                }
                if(SPUtils.getInstance().getBoolean(Constant.GUIDE_KEY)){
//                    startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                    if (!SubscribeManager.instance.isSubscribe()) {
                        SubscribeActivity.actionOpenAct(this@WelcomeActivity,
                            Constant.GUIDE_USER_KEY
                        )
                        finish()
                        return@subscribe
                    }
                    startActivity(Intent(this@WelcomeActivity, BottomNavigationActivity::class.java))
                }else{
                    startActivity(Intent(this@WelcomeActivity, GuideActivity::class.java))
                }
                finish()
            }
        }, 10)
    }
}