package com.devicewifitracker.android.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

public class MacAddressUtil {
    /**
     *  获取了解路由器的Mac地址
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {

        String mac = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiList;
        if (wifiManager != null) {
//            wifiManager.startScan();
            wifiList = wifiManager.getScanResults();
//             NetworkUtils.getWifiScanResult();
            WifiInfo info = wifiManager.getConnectionInfo();
            if (wifiList != null && info != null) {
                for (int i = 0; i < wifiList.size(); i++) {
                    ScanResult result = wifiList.get(i);
                    if (result != null) {
                        if (info.getBSSID().equals(result.BSSID)) {
                            LogUtils.d("BSSID=" + info.getBSSID() + "=====" + result.BSSID);
                            mac = result.BSSID;
                        }
                    }

                }
            }
        }
        return mac;
    }

    // 读取mac 地址
    public static String getMacFromDevice(Context context) {
        String mac = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        return  tryGetMac(wifiManager,context);
    }

    private static String tryGetMac(WifiManager manager,Context context) {
        String mac = null;
        WifiInfo info = manager.getConnectionInfo();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            LogUtils.d("获取权限>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            return "TODO";
        }
        if (info == null || StringUtils.isEmpty(info.getMacAddress())) {
           mac =  getLocalMac();
            LogUtils.d("获取Mac地址1111：>>>>>>>>"+ mac);
        } else {
//            mac =    info.getMacAddress().toUpperCase();
            mac =  getLocalMac();
            LogUtils.d("获取Mac地址22222：>>>>>>>>"+ mac);
        }
        return mac;
    }

    private static String getLocalMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat/sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macSerial;
    }


}
