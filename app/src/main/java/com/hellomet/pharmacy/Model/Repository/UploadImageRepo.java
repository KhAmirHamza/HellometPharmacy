package com.hellomet.pharmacy.Model.Repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.hellomet.pharmacy.ApiClient;
import com.hellomet.pharmacy.ApiRequests;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.pharmacy.Constants.MAIN_URL;

public class UploadImageRepo {
    private static final String TAG = "UploadImageRepo";
    ApiRequests apiRequests;
    MutableLiveData<JsonObject> imageMutableLiveData = new MutableLiveData<>();

    public UploadImageRepo() {
        apiRequests = ApiClient.getInstance(MAIN_URL);
    }

    public MutableLiveData<JsonObject> uploadImageToGenerateUrl(MultipartBody.Part multipartImageFile){
        Log.d(TAG, "uploadImageToGenerateUrl: called 2");
        apiRequests.uploadImageToGenerateUrl(multipartImageFile).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    if (response.body() != null) {
                        imageMutableLiveData.postValue(response.body());
                        Log.d(TAG, "onResponse: "+response.body().toString());
                    }else{
                        Log.d(TAG, "onResponse: nothing found!");
                        imageMutableLiveData.postValue(null);
                    }
                }else{
                    Log.d(TAG, "onResponse: "+ response.errorBody().toString());
                    imageMutableLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: Upload Image: "+t.getLocalizedMessage()  );
                imageMutableLiveData.postValue(null);
            }
        });

        return imageMutableLiveData;
    }
}
