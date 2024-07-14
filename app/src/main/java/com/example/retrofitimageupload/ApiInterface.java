package com.example.retrofitimageupload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
             @Multipart
             @POST("updateprofile")
            Call<UpdateResponse> updateUserProfile(
            @Part("id") RequestBody id,
            @Part("first_name") RequestBody  userName,
            @Part("email") RequestBody  email,
            @Part MultipartBody.Part image
          // @Part("image") RequestBody file
     );

}
