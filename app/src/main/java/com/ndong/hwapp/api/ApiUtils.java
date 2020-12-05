package com.ndong.hwapp.api;

public class ApiUtils {

  public static final String BASE_URL = "http://76ec84480dbd.ngrok.io";

  public static PredictService getPredictService() {
    return RetrofitClient.getClient(BASE_URL).create(PredictService.class);
  }
}
