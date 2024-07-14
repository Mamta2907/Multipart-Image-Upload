package com.example.retrofitimageupload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHandler {

    private static final String BASE_URL = "https://rndtd.com/demos/scrapbazar/api/";

    //public static final String UPDATE_PROFILE = BASE_URL + "updateprofile";

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {

        if(retrofit == null){

            OkHttpClient client = new OkHttpClient.Builder().build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }

}
