package com.hellomet.pharmacy.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.hellomet.pharmacy.Model.Repository.UploadImageRepo;

import okhttp3.MultipartBody;

public class UploadImageVM extends AndroidViewModel {
    private static final String TAG = "UploadImageVM";
    UploadImageRepo uploadImageRepo;
    MutableLiveData<JsonObject> imageMutableLiveData;
    public UploadImageVM(@NonNull Application application) {
        super(application);
        uploadImageRepo = new UploadImageRepo();
        //imageMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<JsonObject> uploadImageToGenerateUrl(MultipartBody.Part multipartImageFile){
        Log.d(TAG, "uploadImageToGenerateUrl: Called 1");
        if (imageMutableLiveData == null)
        imageMutableLiveData = uploadImageRepo.uploadImageToGenerateUrl(multipartImageFile);
        return imageMutableLiveData;


        /*if(companyEntityLiveData==null)
        companyEntityLiveData= companyRepo.getSection();
    return companyEntityLiveData;*/
    }
}
