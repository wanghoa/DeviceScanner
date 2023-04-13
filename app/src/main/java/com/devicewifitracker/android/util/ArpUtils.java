package com.devicewifitracker.android.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ArpUtils {
    public static InetAddress getRouterIpAddress() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.isUp()) {
                continue;
            }
            if (networkInterface.isLoopback()) {
                continue;
            }
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address.isLinkLocalAddress()) {
                    continue;
                }
                if (address.isSiteLocalAddress()) {
                    return address;
                }
            }
        }
        return null;
    }

    public static Enumeration<NetworkInterface> getAllNetworkInterfaces() throws SocketException {
        return NetworkInterface.getNetworkInterfaces();
    }

    public static Enumeration<InetAddress> getAllInetAddresses(NetworkInterface networkInterface) {
        return networkInterface.getInetAddresses();
    }

    public static Enumeration<InetAddress> getAllInetAddresses() throws SocketException {
        Enumeration<NetworkInterface> interfaces = getAllNetworkInterfaces();
        return new Enumeration<InetAddress>() {
            private Enumeration<InetAddress> current = null;

            @Override
            public boolean hasMoreElements() {
                while (current == null || !current.hasMoreElements()) {
                    if (!interfaces.hasMoreElements()) {
                        return false;
                    }
                    current = getAllInetAddresses(interfaces.nextElement());
                }
                return true;
            }

            @Override
            public InetAddress nextElement() {
                return current.nextElement();
            }
        };
    }

    public static InetAddress getArpCacheEntry(String ip) throws SocketException {
        Enumeration<NetworkInterface> interfaces = getAllNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            byte[] mac = networkInterface.getHardwareAddress();
            if (mac == null) {
                continue;
            }
            Enumeration<InetAddress> addresses = getAllInetAddresses(networkInterface);
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address.getHostAddress().equals(ip)) {
                    return address;
                }
            }
        }
        return null;
    }
//
//    public static Enumeration<InetAddress> getArpCache(String mac) throws SocketException {
//        Enumeration<NetworkInterface> interfaces
}