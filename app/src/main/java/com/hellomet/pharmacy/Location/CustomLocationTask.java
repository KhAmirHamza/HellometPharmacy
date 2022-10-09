package com.hellomet.pharmacy.Location;

import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;


public class CustomLocationTask {
    private static final String TAG = "LocationPermission";
    Activity activity;
    LocationRequestResult locationRequestResult;
    private PlacesClient placesClient;

    public CustomLocationTask(Activity activity, String apiKey) {
        this.activity = activity;
        // Initialize Places.
        Places.initialize(activity, apiKey);
        // Create a new Places client instance.
        placesClient = Places.createClient(activity);
    }

    // Check Location Permission
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
    }

    // Request user to grant Location Permission to Grant
    public void requestLocationPermission(int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
            Toast.makeText(activity, "False", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }


//After location permission granted, request to enable location option...
    protected void createLocationRequest(LocationRequestResult locationRequestResult, int REQUEST_CHECK_SETTINGS) {
        this.locationRequestResult = locationRequestResult;
        Log.d(TAG, "createLocationRequest: called");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Toast.makeText(activity, "Location Enabled", Toast.LENGTH_SHORT).show();
            locationRequestResult.locationEnabled(locationSettingsResponse);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
//                    ResolvableApiException resolvable = (ResolvableApiException) e;
//                    resolvable.startResolutionForResult(activity,
//                            REQUEST_CHECK_SETTINGS);
                    ApiException apiException = (ApiException) e;
                    apiException.getStatus().startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                    locationRequestResult.locationRequestError(e);
                }
            }
        });
    }


    public static abstract class LocationRequestResult{
        public abstract void locationEnabled(LocationSettingsResponse locationSettingsResponse);
        public abstract void locationRequestError(Exception e);
    }

}
