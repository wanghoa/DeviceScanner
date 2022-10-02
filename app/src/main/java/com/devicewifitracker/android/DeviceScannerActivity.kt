package com.devicewifitracker.android

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityDeviceScannerBinding
import com.devicewifitracker.android.util.YuvToRgba
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
//import me.pqpo.smartcameralib.SmartCameraView
//import me.pqpo.smartcameralib.SmartCameraView.OnScanResultListener


class DeviceScannerActivity : BaseActivity<ActivityDeviceScannerBinding>() {

    companion object {
        fun actionOpenActivity(context: Context) {
            val intent = Intent(context, DeviceScannerActivity::class.java).apply {

            }
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_device_scanner
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
//        binding.cameraView?.start();
//        binding.cameraView?.startScan();
//        binding.cameraView?.smartScanner?.isPreview = true

        /**
         * 通过第一句代码开启了预览模式。
        你可以通过 setOnScanResultListener 设置回调获得每一帧的扫描结果，其中 result == 1 表示识别结果吻合边框
        若开启了预览模式，你可以在回调中使用 smartCameraView.getPreviewBitmap() 方法获取每一帧处理的结果。
        返回值为 false 表示不拦截扫描结果，这时 SmartCameraView 内部会在 result 为 1 的情况下自动触发拍照，若你自己处理了扫描结果返回 true 即可。
         */
//        binding.cameraView.setOnScanResultListener(object : OnScanResultListener {
//
//            override fun onScanResult(
//                smartCameraView: SmartCameraView?,
//                result: Int,
//                yuvData: ByteArray?
//            ): Boolean {
//                val bitmap = smartCameraView?.previewBitmap
//                bitmap?.let { binding.image.setImageBitmap(it) }
//
//
//                val job = Job()
//                val scope = CoroutineScope(job)
//                scope.launch {
//                    bitmap?.let { getPixels(it) }
//                    val rgb = YuvToRgba.NV21toRGBA(yuvData, 100, 100)
////                    LogUtils.d("?----${rgb}---${rgb.get(0)}---${yuvData.toString()}--------${yuvData?.get(0)}")
//                    yuvData?.forEach {
////                        LogUtils.d("--------${it}---------")
//                    }
//                }
//                return false
//            }
//
//        })
    }


    fun  getPixels(bitmap: Bitmap) {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val grayBytes = ByteArray(pixels.size)
        for (i in 0.until(grayBytes.size)) {
            // 获取单个通道存入
            val red = Color.red(pixels[i])
            grayBytes[i] = red.toByte()
            LogUtils.d("@------------${red.toByte()}---B---${ grayBytes[i]}--red:::-${red}-")
        }

    }


    override fun onPause() {
//        binding.cameraView?.stop();
//        super.onPause()
//        binding.cameraView?.stopScan();
    }
}