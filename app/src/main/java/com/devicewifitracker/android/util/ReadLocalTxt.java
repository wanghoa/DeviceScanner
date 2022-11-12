package com.devicewifitracker.android.util;

import android.content.Context;

import com.blankj.utilcode.util.ToastUtils;
import com.devicewifitracker.android.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ReadLocalTxt {


    /**
     * 从assets中读取txt
     */
    public static String readFromAssets(Context context, String fileName) {
        String result = "";
        try {
//            InputStream is = context.getAssets().open("filecontent.txt");
            InputStream is = context.getAssets().open(fileName);
             result = readTextFromSDcard(is);
//            textView.setText(result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从raw中读取txt
     */
    private void readFromRaw(Context context,int resId) {
        try {
//            InputStream is = context.getResources().openRawResource(R.raw.filecontent);
            InputStream is = context.getResources().openRawResource(resId);
            String text = readTextFromSDcard(is);
            //            textView.setText(text);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 按行读取txt
     *解析输入流，返回txt中的字符串
     * @param is
     * @return
     * @throws Exception
     */
    private static String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        reader.close();
        bufferedReader.close();
        return buffer.toString();
    }

    /**
     * 从assets中读取txt 并存入Map
     */
    public static  Map<Integer,String> readFromAssetsToMap(Context context, String fileName) {
        Map<Integer, String > map = new HashMap<Integer, String>();
        try {
//            InputStream is = context.getAssets().open("filecontent.txt");
            InputStream is = context.getAssets().open(fileName);
            map = readText(is);
//            textView.setText(result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @param is
     * @return
     * @throws Exception
     */
    private static Map<Integer,String> readText(InputStream is) throws Exception {
        Map<Integer, String > map = new HashMap<Integer, String>();
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String str;
        String lineTxt = null;
        int count = 0;//初始化 key值
        while ((lineTxt = bufferedReader.readLine()) != null) {  //
            if (!"".equals(lineTxt)) {
//                        String reds = lineTxt.split("\+")[0];  //java 正则表达式
                String reds = lineTxt.split("\""+"+")[0];  //java 正则表达式

                map.put(count, reds);//依次放到map 0，value0;1,value2
                count++;
            }
        }
        reader.close();
        bufferedReader.close();
        return map;
    }

    public static Map<Integer,String> readTxt() {
        //将读出来的一行行数据使用Map存储
        String filePath = "/sdcard/sdl_log.txt";//手机上地址
        Map<Integer, String > map = new HashMap<Integer, String>();
        try {
            File file = new File(filePath);
            int count = 0;//初始化 key值
            if (file.isFile() && file.exists()) {  //文件存在的前提
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
                BufferedReader br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {  //
                    if (!"".equals(lineTxt)) {
//                        String reds = lineTxt.split("\+")[0];  //java 正则表达式
                        String reds = lineTxt.split("\""+"+")[0];  //java 正则表达式

                        map.put(count, reds);//依次放到map 0，value0;1,value2
                        count++;
                    }
                }
                isr.close();
                br.close();
            }else {

                ToastUtils.showLong("can not find file");//找不到文件情况下
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
