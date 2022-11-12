package com.devicewifitracker.android.util

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration

/**
 * 屏幕适配使用  但是未生效
 */
object DisplayMetrics {
    var sNoncompatDensity = 0F
    var sNoncompatScaledDensity =0F
    fun setCustomDensity(activity: Activity, application: Application) {
        val appDisplayMetrics = application.resources.displayMetrics
        if (sNoncompatDensity == 0F) {
            sNoncompatDensity = appDisplayMetrics.density
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity
            application.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onConfigurationChanged(p0: Configuration) {
                    if (p0 != null && p0.fontScale > 0) {
                        sNoncompatScaledDensity = application.resources.displayMetrics.scaledDensity

                    }
                }

                override fun onLowMemory() {
                    TODO("Not yet implemented")
                }

            })
        }

        val targetDensity = appDisplayMetrics.widthPixels / 360
        val targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity)
        val targetDensityDpi = 160 * targetDensity
        appDisplayMetrics.density = targetDensity.toFloat()
        appDisplayMetrics.scaledDensity = targetScaledDensity
        appDisplayMetrics.densityDpi = targetDensityDpi
        val activityDisplayMetrics = activity.resources.displayMetrics
        activityDisplayMetrics.density = targetDensity.toFloat()
        activityDisplayMetrics.scaledDensity = targetScaledDensity
        activityDisplayMetrics.densityDpi = targetDensityDpi

    }
}