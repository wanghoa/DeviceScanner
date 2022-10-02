package com.devicewifitracker.android.util

import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.core.Observable

object PermissionUtil {
    fun requestPermissions(
        fragmentActivity: FragmentActivity,
        permissions: Array<String>
    ): Observable<Boolean> {
        val rxPermissions = RxPermissions(fragmentActivity)
        return rxPermissions.request(*permissions)
    }

    open fun requestPermission(
        fragmentActivity: FragmentActivity?,
        permissions: Array<String?>
    ): Observable<Boolean?>? {
        val rxPermissions = RxPermissions(fragmentActivity!!)
        return rxPermissions.request(*permissions)
    }
    fun isGranted(fragmentActivity: FragmentActivity?, permission: String?): Boolean? {
        val rxPermissions = RxPermissions(fragmentActivity!!)
        return rxPermissions.isGranted(permission)
    }

}