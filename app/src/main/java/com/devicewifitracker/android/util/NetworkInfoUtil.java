package com.devicewifitracker.android.util;

import android.text.TextUtils;
import android.widget.TextView;
import com.blankj.utilcode.util.LogUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
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

    /**
     * 1：通过java运行cmd命令，来通过arp命令获取同一网络下设备信息，对于支持linux 和windows的设备有效，像一些非智能设备，就无力回天了
     *
     * 2：使用android手机通过向子网内所有设备先发送一遍udp包，实现与在线的设备都进行通信一遍，
     * 这样对应的路由信息就自动存储在本地手机中，然后在通过读取android 本机的arp缓存表，来获取设备信息
     * ————————————————
     * 版权声明：本文为CSDN博主「予渝与裕舆」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/u012842328/article/details/107384978 android获取局域网设备的ip和对应的mac地址
     * @param tv_main_result
     * @return
     */

    public static List<String> readArp(TextView tv_main_result) {
        try {
            List<String> ipList = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line = "";
            String ip = "";
            String flag = "";
            String mac = "";
//            tv_main_result.setText("");
            if (br.readLine() == null) {
                LogUtils.e("scanner", "readArp: null");
            }
            LogUtils.d("scanner", "readArp: line= " + line + " ; line= " + line.length() + " ;line= " + line.length());

            while ((line = br.readLine()) != null) {
                line = line.trim();
                //line=  192.168.0.110    0x1         0x2         d6:ab:d3:49:2e:f6     *        wlan0 ;length =77
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

    /**
     * 适配Android 10 以上机型 代替 readArp（） android 10 及以上禁止使用 绝对路径获取文件
     */
    private static final String IP_CMD = "ip neighbor";
    public static List<String> readArp1() {
        String line = "";
        String ip = "";
        String flag = "";
        String mac = "";
        List<String> ipList = new ArrayList<>();
        BufferedReader reader = null;
        try {
            Process ipProc = null;
                ipProc = Runtime.getRuntime().exec(IP_CMD);
                ipProc.waitFor();
            LogUtils.d("ipProc.exitValue()", "value=" + ipProc.exitValue());
        ;
            if (ipProc.exitValue() != 0) {//方法返回子进程的退出值。
                    throw new Exception("Unable to access ARP entries");
                }
            reader = new BufferedReader(new InputStreamReader(ipProc.getInputStream(), "UTF-8"));

            while ((line = reader.readLine()) != null) {
//                String[] neighborLine = line.split("\\s+");
//                // We don't have a validated ARP entry for this case.
//                if (neighborLine.length <= 4) {
//                    continue;
//                }
//                String ipaddr = neighborLine[0];
//                InetAddress addr = InetAddress.getByName(ipaddr);
//                if (addr.isLinkLocalAddress() || addr.isLoopbackAddress()) {
//                    continue;
//                }
//                String macAddress = neighborLine[4];
                line = line.trim();
                LogUtils.d("scanner", "readArp: line= " + line + " ; line= " + line.length() + " ;line= " + line.length());
                //line= 192.168.0.117 dev wlan0 lladdr 10:08:b1:e6:d4:e7 STALE
                if (line.length() < 54) continue;
                if (line.toUpperCase(Locale.US).contains("IP")) continue;
                ip = line.substring(0, 14).trim();
//                flag = line.substring(29, 32).trim();
                mac = line.substring(31, 48).trim();
                if (mac.contains("00:00:00:00:00:00")) continue;
                LogUtils.d("scanner", "readArp: mac= " + mac + " ; ip= " + ip + " ;flag= " + flag);

                ipList.add(ip);
            }
            reader.close();
            return ipList;
            // Android 10以上版本使用  （待验证）
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public  static ArrayList<String> getConnectIp() throws Exception {
        ArrayList<String> connectIpList = new ArrayList<String>();
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("ip neigh show");
        proc.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));//使用这个获取不到
//        BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));// 使用这个获取好多数据 Android12 崩溃   java.io.FileNotFoundException: /proc/net/arp: open failed: EACCES (Permission denied)
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

    public static List<String> getPingIp() {
        try {
            String ipAddress = "127.0.0.1"; // 要 Ping 的 IP 地址
            String pingCommand = "/system/bin/ping -c 1 " + ipAddress; // Ping 命令
            Runtime runtime = Runtime.getRuntime();
//            Process process = runtime.exec(pingCommand);
            Process process = runtime.exec("arp -a");
            int exitCode = process.waitFor(); // 等待 Ping 命令执行完成
            if (exitCode == 0) {
                // Ping 成功
                // 从 Ping 命令的输出中提取相关信息
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                String ip = "";
                String flag = "";
                String mac = "";
                List<String> ipList = new ArrayList<>();
                while ((line = bufferedReader.readLine()) != null) {
                    // 处理 Ping 命令的输出
                    line = line.trim();
                    LogUtils.d("Ping++", "Ping: line= " + line + " ; line= " + line.length());

                }
                bufferedReader.close();
                return ipList;
            } else {
                // Ping 失败
            }

        } catch (IOException e) {
            // 处理 IOException 异常
        } catch (InterruptedException e) {
            // 处理 InterruptedException 异常
        }
        return null;
    }



/*    public static List<LinkAddress> getAndrid12(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        LinkProperties linkProperties = connectivityManager.getLinkProperties(  connectivityManager.getActiveNetwork());
//        LinkProperties linkProperties = connectivityManager.getActiveNetwork().getLinkProperties();

// 获取当前连接的 IP 地址列表
        List<LinkAddress> linkAddresses = linkProperties.getLinkAddresses();

// 获取 ARP 缓存表中的对应关系

        Map<InetAddress, MacAddress> arpCache = ARPCache.get(linkAddresses);
        return linkAddresses;

    }*/


}
