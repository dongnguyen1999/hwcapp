package com.ndong.hwapp;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ndong.hwapp.api.ApiUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictCharacterActivity extends AppCompatActivity {
  private LinearLayout mainContainer;
  private ProgressBar indicator;
  private TextView txtLabel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_predict_character);
    final ImageView imageView = findViewById(R.id.imageView);
    txtLabel = findViewById(R.id.txtLabel);
    indicator = findViewById(R.id.indicator);
    mainContainer = findViewById(R.id.mainContainer);

    byte[] byteArray = getIntent().getByteArrayExtra("image");
    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

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
    ApiUtils.getPredictService().uploadImage(file).enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        mainContainer.removeView(indicator);
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
      }
    });
  }

}