package com.ndong.hwapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ndong.hwapp.api.ApiUtils;
import com.ndong.hwapp.utils.ImageUtils;
import com.ndong.hwapp.utils.SharedPreferencesUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictCharacterActivity extends AppCompatActivity {
  private ProgressBar indicator;
  private TextView txtLabel;
  private Bitmap bitmap;
  private SharedPreferencesUtils cacheUtils;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_predict_character);
    final ImageView imageView = findViewById(R.id.imageView);
    txtLabel = findViewById(R.id.txtLabel);
    indicator = findViewById(R.id.indicator);

    Size screenSize = getIntent().getExtras().getSize("screenSize");
    int rectSize = getIntent().getExtras().getInt("rectSize");
    cacheUtils = new SharedPreferencesUtils(this);
    bitmap = cacheUtils.getImage();
    bitmap = ImageUtils.rotateBitmap(bitmap, 90);
    bitmap = ImageUtils.cropBitmapByRectangle(bitmap, screenSize, rectSize);
    ApiUtils.changeBaseUrl(cacheUtils.getBaseUrl());

    imageView.setImageBitmap(bitmap);

    final Button btnContinue = findViewById(R.id.btnContinue);
    btnContinue.setOnClickListener(v -> finish());

    callPrediction(bitmap);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
  }

  private static String getStringByIdName(Context context, String idName) {
    Resources res = context.getResources();
    return res.getString(res.getIdentifier(idName, "string", context.getPackageName()));
  }



  private void callPrediction(Bitmap bitmap) {
    File savedImage = ImageUtils.writeToCache(this, bitmap);
    assert savedImage != null;
    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), savedImage);
    MultipartBody.Part file = MultipartBody.Part.createFormData("image", savedImage.getName(), requestFile);
    try {
      ApiUtils.getPredictService();
    } catch(Exception e) {
      showErrorFragment();
      return;
    }
    ApiUtils.getPredictService().uploadImage(file).enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.body() == null) {
          showErrorFragment();
          return;
        }
        indicator.setVisibility(View.GONE);
        String label;
        try {
          int num = Integer.parseInt(response.body());
          label = "num_" + num;
        } catch (NumberFormatException e) {
          label = response.body();
        }
        txtLabel.setText(getStringByIdName(txtLabel.getContext(), label));
        txtLabel.setVisibility(View.VISIBLE);
      }
      @Override
      public void onFailure(Call<String> call, Throwable t) {
        Log.i("responseError", t.getMessage());
        showErrorFragment();
      }
    });
  }

  private void showErrorFragment() {
    FragmentManager fragmentManager = this.getSupportFragmentManager();
    ConnectionErrorFragment fragment = new ConnectionErrorFragment();
    fragment.setActivityCallback(newHost -> {
      if (!newHost.startsWith("http://")) newHost = "http://" + newHost;
      ApiUtils.changeBaseUrl(newHost);
      cacheUtils.putBaseUrl(newHost);
      callPrediction(bitmap);
    });
    FragmentTransaction ft = fragmentManager.beginTransaction();
    ft.add(R.id.rootContainer, fragment).commit();
  }

}