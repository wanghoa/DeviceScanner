package com.devicewifitracker.android.util

import android.app.Activity
import android.content.Intent
import android.net.Uri

object OpenGooglePlay {
    fun openGooglePlayStore(activity: Activity) {
        activity.runOnUiThread(object : Runnable {
            override fun run() {
                val appPkg = activity?.getPackageName();
                val url = "http://play.google.com/store/apps/details?id=" + appPkg;
                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }

        });

    }
}