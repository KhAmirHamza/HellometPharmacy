package com.hellomet.pharmacy.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hellomet.pharmacy.Model.Pharmacy;
import com.hellomet.pharmacy.Model.Repository.PharmacyRepo;

public class PharmacyVM extends AndroidViewModel {
    private static final String TAG = "PharmacyVM";
    MutableLiveData<Pharmacy> pharmacyMutableLiveData;
    PharmacyRepo pharmacyRepo;

    public PharmacyVM(@NonNull Application application) {
        super(application);
        pharmacyRepo = new PharmacyRepo();
        //pharmacyMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Pharmacy> getPharmacyUsingPhoneNumber(String phone_number){
        if(pharmacyMutableLiveData==null)
            pharmacyMutableLiveData= pharmacyRepo.getPharmacyUsingPhoneNumber(phone_number);
        return pharmacyMutableLiveData;
    }
    public MutableLiveData<Pharmacy> createPharmacy(Pharmacy pharmacy){
        if(pharmacyMutableLiveData==null)
        pharmacyMutableLiveData= pharmacyRepo.createPharmacy(pharmacy);
        return pharmacyMutableLiveData;
    }

}
