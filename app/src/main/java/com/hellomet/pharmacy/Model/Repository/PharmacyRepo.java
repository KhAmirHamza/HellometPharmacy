package com.hellomet.pharmacy.Model.Repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hellomet.pharmacy.ApiClient;
import com.hellomet.pharmacy.ApiRequests;
import com.hellomet.pharmacy.Model.Pharmacy;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.pharmacy.Constants.MAIN_URL;

public class PharmacyRepo {
    private static final String TAG = "PharmacyRepo";
    ApiRequests apiRequests;
    MutableLiveData<Pharmacy> pharmacyMutableLiveData;
    public PharmacyRepo(){
        apiRequests = ApiClient.getInstance(MAIN_URL);
        pharmacyMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Pharmacy> getPharmacyUsingPhoneNumber(String phone_number){
        Log.d(TAG, "getPharmacyUsingPhoneNumber: called");
        apiRequests.getPharmacyUsingPhoneNumber(phone_number).enqueue(new Callback<Pharmacy>() {
            @Override
            public void onResponse(Call<Pharmacy> call, Response<Pharmacy> response) {
                if (response.isSuccessful()){
                    if (response.body() != null) {

                        pharmacyMutableLiveData.postValue(response.body());
                        Log.d(TAG, "onResponse: "+response.body().toString());
                    }else{
                        Log.d(TAG, "onResponse: nothing found!");
                        pharmacyMutableLiveData.postValue(null);
                    }
                }else{
                    Log.d(TAG, "onResponse: "+ response.errorBody().toString());
                    pharmacyMutableLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Pharmacy> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                pharmacyMutableLiveData.postValue(null);
            }
        });


        return pharmacyMutableLiveData;
    }

    public MutableLiveData<Pharmacy> createPharmacy(Pharmacy pharmacy){
        Log.d(TAG, "createPharmacy: "+pharmacy.getMeta_data().getImage_url());
        apiRequests.createPharmacy(pharmacy).enqueue(new Callback<Pharmacy>() {
            @Override
            public void onResponse(Call<Pharmacy> call, Response<Pharmacy> response) {
                if (response.isSuccessful()){
                    if (response.body() != null) {
                        if (response.body().getMeta_data() == null) {
                            Log.d(TAG, "onResponse: Create Pharmacy Response Data is null");
                            return;
                        }
                        pharmacyMutableLiveData.postValue(response.body());
                        Log.d(TAG, "onResponse: "+response.body().getMeta_data().getImage_url());
                    }else{
                        Log.d(TAG, "onResponse: nothing found!");
                        pharmacyMutableLiveData.postValue(null);
                    }
                }else{
                    Log.d(TAG, "onResponse: "+ response.errorBody().toString());
                    pharmacyMutableLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Pharmacy> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                pharmacyMutableLiveData.postValue(null);
            }
        });
        return pharmacyMutableLiveData;
    }
}
