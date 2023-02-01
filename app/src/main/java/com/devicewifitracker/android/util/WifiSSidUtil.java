package com.devicewifitracker.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;

import java.util.List;

/**
 * 解决部分机型获取不到wifi ssid的问题
 */
public class WifiSSidUtil {
    private static final String WIFISSID_UNKNOW = "<unknown ssid>";

    /**
     * 三种方式获取wifi ssid 解决了在华为mate9 获取不到的问题 但是在华为 mate 40 与p40 上还是获取不到
     * @param context
     * @return
     */
    public static String getWifiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager ctm =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        String result = wifiId != null ? wifiId.trim() : null;
        LogUtils.d("SSID=" + result);
        if (!TextUtils.isEmpty(result)) {
            if (result.charAt(0) == '"' && result.charAt(result.length() - 1) == '"') {
                result = result.substring(1, result.length() - 1);
                LogUtils.e("SSID 1=" + result);
            }
        }
        if (TextUtils.isEmpty(result) || WIFISSID_UNKNOW.equalsIgnoreCase(result.trim())) {
            NetworkInfo networkInfo = getNetworkInfo(context);
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo() != null) {
                    result = networkInfo.getExtraInfo().replace("\"", "");
                    LogUtils.e("SSID 2=" + result);
                }
            }

        }
        if (TextUtils.isEmpty(result) || WIFISSID_UNKNOW.equalsIgnoreCase(result.trim())) {

            result = getSSIDByNetworkId(context);
            LogUtils.e("SSID 3=" + result);
        }
        if (TextUtils.isEmpty(result) || WIFISSID_UNKNOW.equalsIgnoreCase(result.trim())) {
        NetworkInfo networkInfo = ctm.getActiveNetworkInfo();
            result = networkInfo.getExtraInfo();
        }
        return result;

    }

    public static NetworkInfo getNetworkInfo(Context context) {
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != connectivityManager) {
                return connectivityManager.getActiveNetworkInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getSSIDByNetworkId(Context context) {
        String ssid = WIFISSID_UNKNOW;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int networkId = wifiInfo.getNetworkId();
           @SuppressLint("MissingPermission") List<WifiConfiguration> configuration =  wifiManager.getConfiguredNetworks();
            for (WifiConfiguration wifiEnterpriseConfig : configuration) {
                if (wifiEnterpriseConfig.networkId == networkId) {
                    ssid = wifiEnterpriseConfig.SSID;
                    break;
                }
            }
        }
        return ssid;
    }
}
