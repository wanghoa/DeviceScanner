package com.devicewifitracker.android.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

//  原文链接：https://blog.csdn.net/qq1137830424/article/details/80081075
// TODO Android YUV转RGB YUV转Bitmap Bitmap转RGB
public class YuvToRGB {
    /**
     * yuv转rgb
     * @param yuv yuv数据源
     * @param width 图片宽度
     * @param height 图片高度
     * @return
     */
    public static byte[] yuvToRgb(byte[] yuv, int width, int height){
        Bitmap bitmap = yuvToBitmap(yuv, width, height);
        if(bitmap != null){
            return bitmapToRgb(bitmap);
        }
        return null;
    }

    /**
     * yuv转bitmap
     * @param nv21 yuv数据源
     * @param width 图片宽度
     * @param height 图片高度
     * @return
     */
    public static Bitmap yuvToBitmap(byte[] nv21, int width, int height) {
        Bitmap bitmap = null;
        try {
            YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 100, stream);
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    /**
     * bitmap转rgb
     * @param bitmap bitmap数据源
     * @return
     */
    public static byte[] bitmapToRgb(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer);

        byte[] rgba = buffer.array();
        byte[] pixels = new byte[(rgba.length / 4) * 3];

        int count = rgba.length / 4;

        for (int i = 0; i < count; i++) {

            pixels[i * 3] = rgba[i * 4];            //R
            pixels[i * 3 + 1] = rgba[i * 4 + 1];    //G
            pixels[i * 3 + 2] = rgba[i * 4 + 2];    //B

        }

        return pixels;
    }



}
