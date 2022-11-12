package com.devicewifitracker.android.ui.gpu

import android.Manifest
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.FlashlightUtils
import com.blankj.utilcode.util.ToastUtils
import com.devicewifitracker.android.GPUImageFilterTools
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityGpuBinding
import com.devicewifitracker.android.util.PermissionUtil
import com.devicewifitracker.android.util.doOnLayout
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.*
import jp.co.cyberagent.android.gpuimage.sample.utils.Camera1Loader
import jp.co.cyberagent.android.gpuimage.sample.utils.Camera2Loader
import jp.co.cyberagent.android.gpuimage.sample.utils.CameraLoader
import jp.co.cyberagent.android.gpuimage.util.Rotation

class GpuActivity :BaseActivity<ActivityGpuBinding>() {
//    var camera:Camera?= null
    var ifOpen = false
    private var filterAdjuster: GPUImageFilterTools.FilterAdjuster? = null
    private val gpuImageView: GPUImageView by lazy { findViewById<GPUImageView>(R.id.surfaceView) }
    private val cameraLoader: CameraLoader by lazy {
        if (Build.VERSION.SDK_INT < 21) {
            Camera1Loader(this)
        } else {
            Camera2Loader(this)
        }
    }
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
//        binding?.constraint?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        super.initView(savedInstanceState)
//        camera = Camera.open(0)
        var permissionArray: Array<String?>
        permissionArray= arrayOf(
           Manifest.permission.CAMERA,
           Manifest.permission.WRITE_EXTERNAL_STORAGE

            )
        PermissionUtil.requestPermission(this, permissionArray)?.subscribe { granted ->
            if (!granted!!) {
                ToastUtils.showShort("You have denied relevant permissions")
            }
            switchFilterTo ( GPUImageFalseColorFilter())
                 binding.back.setOnClickListener{
                     finish()
                 }
            binding.flashLight.setOnClickListener {
//                if (FlashlightUtils.isFlashlightEnable() && !FlashlightUtils.isFlashlightOn()) {
//                    FlashlightUtils.setFlashlightStatus(true)
//                } else {
//
//                    FlashlightUtils.setFlashlightStatus(false)
//                    FlashlightUtils.destroy()
//                }

                if (!ifOpen) {
//
                    cameraLoader.openFlash(true)
//                    cameraLoader.turnLightOn()
                    ifOpen = true
                } else {
                  cameraLoader.openFlash(false)
                    ifOpen = false
                }

        /*     会崩溃 获取parameters失败
         val p = camera?.parameters

                if (!ifOpen) {
                    ifOpen = true
                    p?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                    camera?.parameters = p
                } else {
                    ifOpen = false
                    p?.flashMode = Camera.Parameters .FLASH_MODE_OFF
                    camera?.parameters = p
                }*/

            }
            binding.red.setOnClickListener {
                switchFilterTo ( GPUImageFalseColorFilter())
                binding.one.visibility = View.VISIBLE
                binding.two.visibility = View.INVISIBLE
                binding.three.visibility = View.INVISIBLE
                binding.four.visibility = View.INVISIBLE
            }
            binding.green.setOnClickListener {
//                switchFilterTo (  GPUImageRGBFilter(1.0f, 1.0f, 1.0f))
                switchFilterTo (  GPUImageWhiteBalanceFilter(
                    5000.0f,
                    1.0f
                ))
                switchFilterTo (  GPUImageRGBFilter(0.0f, 1.0f, 0.0f))
                binding.one.visibility = View.INVISIBLE
                binding.two.visibility = View.VISIBLE
                binding.three.visibility = View.INVISIBLE
                binding.four.visibility = View.INVISIBLE
            }
            binding.blue.setOnClickListener {
                switchFilterTo (  GPUImageWhiteBalanceFilter(
                    5000.0f,
                    1.0f
                ))
                switchFilterTo (  GPUImageRGBFilter(0.0f, 0.0f, 1.0f))
                binding.one.visibility = View.INVISIBLE
                binding.two.visibility = View.INVISIBLE
                binding.three.visibility = View.VISIBLE
                binding.four.visibility = View.INVISIBLE
            }
            binding.blackwith.setOnClickListener {
//                switchFilterTo (GPUImageBrightnessFilter(1.5f))
                switchFilterTo (GPUImagePixelationFilter())
                binding.one.visibility = View.INVISIBLE
                binding.two.visibility = View.INVISIBLE
                binding.three.visibility = View.INVISIBLE
                binding.four.visibility = View.VISIBLE
            }

//            val imageUri: Uri =
//            gpuImageView = findViewById<GPUImageView>(R.id.gpuimageview)
//            gpuImageView// this loads image on the current thread, should be run in a thread
//            gpuImageView.setFilter(GPUImageSepiaFilter())
//            binding?.surfaceView.setImage(imageUri)
//            binding?.surfaceView.setFilter(GPUImageSepiaFilter())

            // Later when image should be saved saved:
            cameraLoader.setOnPreviewFrameListener { data, width, height ->
                gpuImageView.updatePreviewFrame(data, width, height)
            }
            gpuImageView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
            gpuImageView.setRenderMode(GPUImageView.RENDERMODE_CONTINUOUSLY)

        }

//        findViewById<View>(R.id.img_switch_camera).run {
//            if (!cameraLoader.hasMultipleCamera()) {
//                visibility = View.GONE
//            }
//            setOnClickListener {
//                cameraLoader.switchCamera()
//                gpuImageView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
//            }
//        }

    }



    private fun switchFilterTo(filter: GPUImageFilter) {
        if (gpuImageView.filter == null || gpuImageView.filter!!.javaClass != filter.javaClass) {
            gpuImageView.filter = filter
            filterAdjuster = GPUImageFilterTools.FilterAdjuster(filter)
//            filterAdjuster?.adjust(seekBar.progress)
        }
    }
    private fun getRotation(orientation: Int): Rotation {
        return when (orientation) {
            90 -> Rotation.ROTATION_90
            180 -> Rotation.ROTATION_180
            270 -> Rotation.ROTATION_270
            else -> Rotation.NORMAL
        }
    }
    override fun onResume() {
        super.onResume()
        gpuImageView.doOnLayout {
            cameraLoader.onResume(it.width, it.height)
        }
    }

    override fun onPause() {
        cameraLoader.onPause()

        ifOpen = false
        super.onPause()
    }

    override fun onStop() {
        super.onStop()

        ifOpen = false
    }
}