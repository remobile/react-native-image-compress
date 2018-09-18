package com.remobile.imagecompress;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import com.facebook.react.bridge.Promise;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class ImageCompressModule extends BaseModule {

    public ImageCompressModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    public String getName() {
        return "ImageCompress";
    }

    @ReactMethod
    public void compressImage(String imgSrc, int maxFileSize, Promise promise) {
        try {
            Bitmap image =  getBitMBitmap(imgSrc);
            WritableMap map = Arguments.createMap();
            if (image == null) {
                map.putString("success", "false");
                promise.resolve(map);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length > maxFileSize && options > 10) { //循环判断如果压缩后图片是否大于maxFileSize,大于继续压缩
               baos.reset();//重置baos即清空baos
				Log.d("compressImage:", "options" + options);
               image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
               options -= 10;//每次都减少10
            }

            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
			String base64 = bitmapToBase64(bitmap);
            map.putString("success", "true");
            map.putString("base64", base64);
            promise.resolve(map);
        } catch(Exception e) {
            Log.d("compressImage:", "Exception" + e);
            promise.reject("压缩异常", e);
        }
    }


	/**
	 * 根据路径 转bitmap
	 * @param urlpath
	 * @return
	 */
	private Bitmap getBitMBitmap(String urlpath) {

		Bitmap map = null;
		try {
			URL url = new URL(urlpath);
			URLConnection conn = url.openConnection();
			conn.connect();
			InputStream in;
			in = conn.getInputStream();
			map = BitmapFactory.decodeStream(in);
			// TODO Auto-generated catch block
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}


	/**
	 * bitmap转为base64
	 * @param bitmap
	 * @return
	 */
	private String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
