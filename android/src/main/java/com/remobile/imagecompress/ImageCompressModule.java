package com.remobile.imagecompress;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import com.facebook.react.bridge.Promise;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class ImageCompressModule extends BaseModule {

    public ImageCompressModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    public String getName() {
        return "ImageCompress";
    }

    @ReactMethod
    public void compressImage(final String imgSrc, final int maxFileSize, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap image =  getBitMBitmap(imgSrc);
                    WritableMap map = Arguments.createMap();
                    if (image == null) {
                        map.putString("success", "false");
                        promise.resolve(map);
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 90, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                    int options = 100;
                    while (baos.toByteArray().length > maxFileSize && options > 0) { //循环判断如果压缩后图片是否大于maxFileSize,大于继续压缩
                        options *= 0.6;//每次60%
                        baos.reset();//重置baos即清空baos
                        Log.d("compressImage:", "options" + options);
                        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

                    }
                    String filename = "image-" + UUID.randomUUID().toString() + ".jpg";
                    File f = new File(context.getCacheDir(), filename);
                    FileOutputStream fo;
                    try {
                        fo = new FileOutputStream(f);
                        try {
                            fo.write(baos.toByteArray());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (image != null) {
                         image.recycle();
                         image = null;
                     }
                    map.putString("success", "true");
                    map.putString("file", f.getPath());
                    promise.resolve(map);
                } catch(Exception e) {
                    Log.d("compressImage:", "Exception" + e);
                    promise.reject("压缩异常", e);
                }
            }
        }).start();
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
