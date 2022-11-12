package com.devicewifitracker.android.util;

import com.blankj.utilcode.util.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingDelayUtil {
     public static String  pingDelay(String ip) {
          String lost = new String();

          String delay = new String();

          Process p = null;
          try {
               p = Runtime.getRuntime().exec("ping -c 4 " + ip);
          } catch (IOException e) {
               e.printStackTrace();
          }

          BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));

          String str = new String();

          while(true){
               try {
                    if (!((str=buf.readLine())!=null)) break;
               } catch (IOException e) {
                    e.printStackTrace();
               }
               if(str.contains("packet loss")){
                    int i= str.indexOf("received");

                    int j= str.indexOf("%");

                    System.out.println("丢包率:"+str.substring(i+10, j+1));
                    LogUtils.e("丢包率:"+str.substring(i+10, j+1));

//System.out.println("丢包率:"+str.substring(j-3, j+1));

                    lost = str.substring(i+10, j+1);

               }

               if(str.contains("avg")){
                    int i=str.indexOf("/", 20);

                    int j=str.indexOf(".", i);

                    System.out.println("延迟:"+str.substring(i+1, j));

                    delay =str.substring(i+1, j);

                    delay = delay+"ms";
                    LogUtils.e("延迟:"+delay);

               }

          }

          return delay;
     }
}
