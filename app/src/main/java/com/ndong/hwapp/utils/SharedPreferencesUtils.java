package com.ndong.hwapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ndong.hwapp.api.ApiUtils;

import java.util.Base64;

public class SharedPreferencesUtils {
  private static final String PREFERENCES_NAME = "com.ndong.hwapp.preferences";

  private final Context context;
  private final SharedPreferences cache;

  public SharedPreferencesUtils(Context context){
    this.context = context;
    cache = this.context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
  }

  public boolean putBaseUrl(String url) {
    SharedPreferences.Editor editor = cache.edit();
    editor.putString("baseUrl", url);
    return editor.commit();
  }

  public Bitmap getImage() {
    String base64Image = cache.getString("image", null);
    if (base64Image == null) return null;
    byte[] bytes = Base64.getDecoder().decode(base64Image);
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
  }

  public boolean putByteArray(byte[] bytes) {
    SharedPreferences.Editor editor = cache.edit();
    String data = Base64.getEncoder().encodeToString(bytes);
    editor.putString("image", data);
    return editor.commit();
  }

  public String getBaseUrl() {
    return cache.getString("baseUrl", null);
  }


}
