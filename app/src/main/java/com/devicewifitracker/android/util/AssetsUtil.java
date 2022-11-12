package com.devicewifitracker.android.util;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ProjectName: AndroidTemplateProject2
 * @Package: com.insworks.lib_datas.utils
 * @ClassName: AssetsUtil
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 * @Description: Assets和raw文件文件读取工具类
 * <p>
 * String[] list(String path);//列出该目录下的下级文件和文件夹名称
 * <p>
 * InputStream open(String fileName);//以顺序读取模式打开文件，默认模式为ACCESS_STREAMING
 * <p>
 * InputStream open(String fileName, int accessMode);//以指定模式打开文件。读取模式有以下几种：
 * //ACCESS_UNKNOWN : 未指定具体的读取模式
 * //ACCESS_RANDOM : 随机读取
 * //ACCESS_STREAMING : 顺序读取
 * //ACCESS_BUFFER : 缓存读取
 */

public class AssetsUtil {
    /**
     * 获取assets目录下的网页
     * 这种方式可以加载assets目录下的网页，并且与网页有关的css，js，图片等文件也会的加载。
     * webView.loadUrl("file:///android_asset/html/index.html");
     *
     * @param filePath
     */
    public static String getHtml(String filePath) {
        return "file:///android_asset/" + filePath;
    }

    /**
     * 获取所有文件
     *
     * @param path 目录
     * @return
     */
    public static String[] getfiles(Context context, String path) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            assetManager.close();
        }
        return files;

    }

    /**
     * 获取assets目录下的图片资源
     *
     * @param fileName
     */
    public static Bitmap getPic(Context context, String fileName) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = context.getAssets().open(fileName);
            bitmap = BitmapFactory.decodeStream(is);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(is);
        }
        return bitmap;
    }

    /**
     * 关闭流
     *
     * @param is
     */
    private static void close(Closeable... is) {
        for (Closeable i : is) {
            try {
                i.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取assets目录下的文本资源
     *
     * @param fileName
     */
    public static String getTex(Context context, String fileName) {
        InputStream is = null;
        String result = "";
        try {
            is = context.getAssets().open(fileName);
            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            result = new String(buffer, "utf8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(is);
        }
        return result;

    }

    /**
     * 获取assets目录下的音频资源
     *
     * @param fileName
     */
    public static AssetFileDescriptor getAudio(Context context, String fileName) {
        AssetFileDescriptor afd = null;
        try {
            // 打开指定音乐文件,获取assets目录下指定文件的AssetFileDescriptor对象
            afd = context.getAssets().openFd(fileName);
//            MediaPlayer mPlayer=new MediaPlayer();
//            mPlayer.reset();
//        // 使用MediaPlayer加载指定的声音文件。
//            mPlayer.setDataSource(afd.getFileDescriptor(),
//                    afd.getStartOffset(), afd.getLength());
//            // 准备声音
//            mPlayer.prepare();
//        // 播放
//            mPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(afd);
        }
        return afd;

    }

    /**
     * 获取assets目录下的URI
     * 可用于播放视频资源
     *
     * @param fileName
     */
    public static Uri getUri(Context context, String fileName) {
        File file = getFile(context, fileName);
        //播放视频
//        VideoView mVideoView = new VideoView(context);
//        mVideoView.setVideoURI(Uri.fromFile(file));
//        mVideoView.start();
        return Uri.fromFile(file);
    }

    /**
     * 将流拷贝到本地 然后返回本地file  默认拷贝到Files目录
     *
     * @param context
     * @param name
     * @return
     */
    public static File getFile(Context context, String name) {
        InputStream is = null;
        FileOutputStream os = null;
        try {
            File dir = context.getFilesDir();
            File file = new File(dir, name);
            if (file.exists()) {
                return file;

            } else {
                file.createNewFile();
                os = new FileOutputStream(file);
                is = context.getAssets().open(name);
                byte[] buffer = new byte[1024];
                int bufferRead = 0;
                while ((bufferRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bufferRead);
                }
                os.flush();
                is.close();
                os.close();
                Log.d("Test", "=========getFile success=========");
                return file;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(is, os);
        }
        return null;
    }

    /**
     * 获取raw目录下的资源
     *
     * @param resId 资源id
     */
    public static InputStream getRawStream(Context context, int resId) {
        return context.getResources().openRawResource(resId);

    }

    /**
     * 获取raw目录下的资源
     *
     * @param resId 资源id
     */
    public static String getRawFilePath(Context context, int resId) {
        return "android.resource://" + context.getPackageName() + "/" + resId;
    }


    /**
     * 从assets目录中复制内容到sd卡中
     *
     * @param context   Context 使用CopyFiles类的Activity
     * @param assetPath String  原文件路径  如：/aa
     * @param newPath   String  复制后路径  如：xx:/bb/cc
     */
    public static void copyFilesFassets(Context context, String assetPath, String newPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            String fileNames[] = context.getAssets().list(assetPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(context, assetPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {//如果是文件
                is = context.getAssets().open(assetPath);
                fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //如果捕捉到错误则通知UI线程
        } finally {
            close(is, fos);
        }
    }

}
