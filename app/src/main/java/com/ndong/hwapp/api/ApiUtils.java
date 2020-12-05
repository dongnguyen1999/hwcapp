package com.ndong.hwapp.api;

import android.util.Log;

import retrofit2.Retrofit;

public class ApiUtils {
  private static String baseUrl = "http://127.0.0.1";


  public static void changeBaseUrl(String baseUrl) {
    ApiUtils.baseUrl = baseUrl;
  }

  public static PredictService getPredictService() {
    return RetrofitClient.createNewClient(baseUrl).create(PredictService.class);
  }
}
