@file:Suppress("DEPRECATION")

package jp.co.cyberagent.android.gpuimage.sample.utils

import android.app.Activity
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.util.Log
import android.view.Surface
import com.blankj.utilcode.util.ToastUtils

class Camera1Loader(private val activity: Activity) : CameraLoader() {

    private var cameraInstance: Camera? = null
    private var cameraFacing: Int = Camera.CameraInfo.CAMERA_FACING_BACK

    override fun onResume(width: Int, height: Int) {
        setUpCamera()
    }

    override fun onPause() {
        releaseCamera()
    }

    override fun switchCamera() {
        cameraFacing = when (cameraFacing) {
            Camera.CameraInfo.CAMERA_FACING_FRONT -> Camera.CameraInfo.CAMERA_FACING_BACK
            Camera.CameraInfo.CAMERA_FACING_BACK -> Camera.CameraInfo.CAMERA_FACING_FRONT
            else -> return
        }
        releaseCamera()
        setUpCamera()
    }

    override fun getCameraOrientation(): Int {
        val degrees = when (activity.windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
        return if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            (90 + degrees) % 360
        } else { // back-facing
            (90 - degrees) % 360
        }
    }

    override fun hasMultipleCamera(): Boolean {
        return Camera.getNumberOfCameras() > 1
    }

    private fun setUpCamera() {
        val id = getCurrentCameraId()
        try {
            cameraInstance = getCameraInstance(id)
        } catch (e: IllegalAccessError) {
            Log.e(TAG, "Camera not found")
            return
        }
        val parameters = cameraInstance!!.parameters

        if (parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        }
        cameraInstance!!.parameters = parameters

        cameraInstance!!.setPreviewCallback { data, camera ->
            if (data == null || camera == null) {
                return@setPreviewCallback
            }
            val size = camera.parameters.previewSize
            onPreviewFrame?.invoke(data, size.width, size.height)
        }
        cameraInstance!!.startPreview()
    }

    private fun getCurrentCameraId(): Int {
        val cameraInfo = Camera.CameraInfo()
        for (id in 0 until Camera.getNumberOfCameras()) {
            Camera.getCameraInfo(id, cameraInfo)
            if (cameraInfo.facing == cameraFacing) {
                return id
            }
        }
        return 0
    }

    private fun getCameraInstance(id: Int): Camera {
        return try {
            Camera.open(id)
        } catch (e: Exception) {
            throw IllegalAccessError("Camera not found")
        }
    }

    private fun releaseCamera() {
        cameraInstance!!.setPreviewCallback(null)
        cameraInstance!!.release()
        cameraInstance = null
    }

    companion object {
        private const val TAG = "Camera1Loader"
    }


    override fun openFlash(isOpen:Boolean) {
        ToastUtils.showLong("camera1")

        val parameters = cameraInstance?.parameters
        parameters?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
        cameraInstance?.parameters = parameters
    }

    override fun turnLightOn() {
        if (cameraInstance == null) {
            return;
        }
        val parameters = cameraInstance?.getParameters();
        if (parameters == null) {
            return;
        }
      val flashModes = parameters.getSupportedFlashModes();
        // Check if camera flash exists
        if (flashModes == null) {
            // Use the screen as a flashlight (next best thing)
            return;
        }
        val flashMode = parameters.getFlashMode();

        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cameraInstance?.setParameters(parameters);
            } else {
            }
        }
    }

}