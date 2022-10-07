package com.devicewifitracker.android.util

//import androidx.core.content.PackageManagerCompat.LOG_TAG
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


object PhoneIP {
    fun getLocalIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.getInetAddresses()
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString()
                    }
                }
            }
        } catch (ex: SocketException) {
//            Log.e(LOG_TAG, ex.toString())
        }
        return null
    }



}