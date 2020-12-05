package com.ndong.hwapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Size;

import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@SuppressWarnings("ALL")
public class ImageUtils {
  public static Bitmap convertCaptureToBitmap(ImageProxy image) {
    byte[] bytes = convertCaptureToByteArray(image);
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
  }

  public static byte[] convertCaptureToByteArray(ImageProxy image) {
    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
    byte[] bytes = new byte[buffer.remaining()];
    buffer.get(bytes);
    image.close();
    return bytes;
  }

  public static Bitmap rotateBitmap(Bitmap source, float angle) {
    Matrix matrix = new Matrix();
    matrix.postRotate(angle);
    return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
  }

  public static Bitmap cropBitmapByRectangle(Bitmap bitmap, Size screenSize, int rectSize) {
    float dw = (float) bitmap.getWidth() / screenSize.getWidth();
    float dh = (float) bitmap.getHeight() / screenSize.getHeight();
    int padding = (int) (0.08*screenSize.getWidth());
    float x = (float) ((screenSize.getWidth() / 2.0) - rectSize/2.0);
    float y = (float) ((screenSize.getHeight() / 2.0) - rectSize/2.0);
    return Bitmap.createBitmap(bitmap, Math.round(x*dw+padding), Math.round(y*dh), Math.round(rectSize*dw-2*padding) , Math.round(rectSize*dh));
  }

  public static File writeToCache(Context context, Bitmap bitmap) {
    String filename = System.currentTimeMillis()+".jpg";
    File file = new File(context.getCacheDir(), filename);
    try {
      file.createNewFile();
      //Convert bitmap to byte array
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
      byte[] bitmapdata = byteArrayOutputStream.toByteArray();

      //write the bytes in file
      FileOutputStream fileOutputStream = null;

      fileOutputStream = new FileOutputStream(file);
      fileOutputStream.write(bitmapdata);
      fileOutputStream.flush();
      fileOutputStream.close();
      return file;

    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }


}
