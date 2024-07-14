package com.example.retrofitimageupload;

import static com.example.retrofitimageupload.ApiHandler.getRetrofitInstance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ShapeableImageView imgProfile;
    Button btnUploadImage, btnPickImage;
    private static final int REQUEST_IMAGE_PICK = 1;
    private Uri selectedImageUri;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading Data....");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);


        imgProfile = findViewById(R.id.imgProfile);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnPickImage = findViewById(R.id.btnPickImage);

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    mProgress.show();
                    uploadImage(selectedImageUri);
                } else {
                    Toast.makeText(MainActivity.this, "Please pick an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfile.setImageURI(selectedImageUri);
        }
    }

    private void uploadImage(Uri imageUri) {

        String id = "7";
        String name = "mamta123";
        String email = "mamta123@gmail.com";

        File file = new File(getRealPathFromURI(imageUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // Here the "image" , is the key so always check the key and use the same name here...
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);


        RequestBody requestId = RequestBody.create(MediaType.parse("mutipart/form-data"), id);
        RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), name);
        RequestBody requestEmail = RequestBody.create(MediaType.parse("multipart/form-data"), email);


        ApiInterface apiInterface = getRetrofitInstance().create(ApiInterface.class);


        Call<UpdateResponse> call = apiInterface.updateUserProfile(requestId, requestName, requestEmail, body);

        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                if (response.isSuccessful()) {

                    String contentType = response.headers().get("Content-Type");
                    if (contentType != null) {
                        Log.e("API Response", "Content-Type: " + contentType);
                    } else {
                        Log.e("API Response", "Content-Type header is not present.");
                    }

                    UpdateResponse updateResponse = response.body();

                    String message = updateResponse.getMessage();
                    mProgress.dismiss();
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful response
                    try {
                        // Log the error response for debugging
                        String errorBody = response.errorBody().string();
                        Log.e("path", "Error response: " + errorBody);
                        Toast.makeText(MainActivity.this, "not success " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                mProgress.dismiss();
                Toast.makeText(MainActivity.this, "Code: ", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("path", t.getLocalizedMessage());
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        cursor.close();
        return filePath;
    }

}
