package com.devicewifitracker.android.util;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkScanner {
    private static final String TAG = "NetworkScanner";
    private static final int TIMEOUT_MS = 500;
    private static final int PING_RETRIES = 3;

    public static void scan() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isLinkLocalAddress() || address.isLoopbackAddress() || address.isMulticastAddress()) {
                        continue;
                    }

                    String subnet = getSubnet(address.getHostAddress());
                    for (int i = 1; i < 255; i++) {
                        String host = subnet + "." + i;
                        if (ping(host)) {

                            LogUtils.i(TAG, "Device found at " + host);
                        }
                    }
                }
            }
        } catch (SocketException e) {

            LogUtils.i(TAG, "Failed to get network interfaces", e);
        }
    }

    private static String getSubnet(String ip) {
        int lastDotIndex = ip.lastIndexOf(".");
        return ip.substring(0, lastDotIndex);
    }

    private static boolean ping(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            for (int i = 0; i < PING_RETRIES; i++) {
                if (address.isReachable(TIMEOUT_MS)) {
                    return true;
                }
            }
        } catch (IOException e) {

            LogUtils.i(TAG, "Failed to ping " + host, e);
        }
        return false;
    }
}

