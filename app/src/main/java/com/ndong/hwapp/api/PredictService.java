package com.ndong.hwapp.api;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PredictService {
  @Multipart
  @POST("/predict")
  Call<String> uploadImage(@Part MultipartBody.Part filePart);

}
