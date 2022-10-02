package com.devicewifitracker.android.util;

import android.text.TextUtils;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NetworkInfoUtil {
    private static String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
//            Log.i("kalshen", "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }


    public static void sendDataToLoacl() {
        //局域网内存在的ip集合
        final List<String> ipList = new ArrayList<>();
        final Map<String, String> map = new HashMap<>();

        //获取本机所在的局域网地址
        String hostIP = getHostIP();
        if (TextUtils.isEmpty(hostIP)){
            return;
        }
        int lastIndexOf = hostIP.lastIndexOf(".");
        final String substring = hostIP.substring(0, lastIndexOf + 1);
        //创建线程池
        //        final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramPacket dp = new DatagramPacket(new byte[0], 0, 0);
                DatagramSocket socket;
                try {
                    socket = new DatagramSocket();
                    int position = 2;
                    while (position < 255) {
                        LogUtils.d("Scanner ", "run: udp-" + substring + position);
                        dp.setAddress(InetAddress.getByName(substring + String.valueOf(position)));
                        socket.send(dp);
                        position++;
                        if (position == 125) {//分两段掉包，一次性发的话，达到236左右，会耗时3秒左右再往下发
                            socket.close();
                            socket = new DatagramSocket();
                        }
                    }
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static List<String> readArp(TextView tv_main_result) {
        try {
            List<String> ipList = new ArrayList<>();
            BufferedReader br = new BufferedReader(
                    new FileReader("/proc/net/arp"));
            String line = "";
            String ip = "";
            String flag = "";
            String mac = "";
//            tv_main_result.setText("");
            if (br.readLine() == null) {
//                Log.e("scanner", "readArp: null");
            }
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() < 63) continue;
                if (line.toUpperCase(Locale.US).contains("IP")) continue;
                ip = line.substring(0, 17).trim();
                flag = line.substring(29, 32).trim();
                mac = line.substring(41, 63).trim();
                if (mac.contains("00:00:00:00:00:00")) continue;
                LogUtils.d("scanner", "readArp: mac= " + mac + " ; ip= " + ip + " ;flag= " + flag);
//                tv_main_result.append("\nip:" + ip + "\tmac:" + mac);
                ipList.add(ip);

            }
            br.close();
            return ipList;
        } catch (Exception ignored) {
        }
        return null;
    }
   // Android 10以上版本使用  （待验证）

    public  static ArrayList<String> getConnectIp() throws Exception {
        ArrayList<String> connectIpList = new ArrayList<String>();
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("ip neigh show");
        proc.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        //BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] splitted = line.split(" +");
            if (splitted != null && splitted.length >= 4) {
                String ip = splitted[0];
                connectIpList.add(ip);
            }
        }
        return connectIpList;
    }
}
