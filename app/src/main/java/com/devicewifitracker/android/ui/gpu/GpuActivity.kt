package com.devicewifitracker.android.ui.gpu

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.devicewifitracker.android.BottomNavigationActivity
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityGpuBinding
import com.devicewifitracker.android.ui.guide.GuideActivity
import com.devicewifitracker.android.util.Constant
import com.devicewifitracker.android.util.PermissionUtil
import jp.co.cyberagent.android.gpuimage.GPUImageView

class GpuActivity :BaseActivity<ActivityGpuBinding>() {
    private val gpuImageView: GPUImageView by lazy { findViewById<GPUImageView>(R.id.surfaceView) }

    companion object{
        fun actionOpenAct(context: Context) {
            val  intent = Intent(context,GpuActivity::class.java).apply {

            }
            context.startActivity(intent)

        }
    }

    override fun getLayoutId(): Int = R.layout.activity_gpu
    override fun initView(savedInstanceState: Bundle?) {
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.constraint?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        super.initView(savedInstanceState)
        var permissionArray: Array<String?>
        permissionArray= arrayOf(
           Manifest.permission.CAMERA,
           Manifest.permission.WRITE_EXTERNAL_STORAGE

            )
        PermissionUtil.requestPermission(this, permissionArray)?.subscribe { granted ->
            if (!granted!!) {
                ToastUtils.showShort("You have denied relevant permissions")
            }
            ToastUtils.showLong("AAAAAAAAAAAAAAAA")

        }
    }
}