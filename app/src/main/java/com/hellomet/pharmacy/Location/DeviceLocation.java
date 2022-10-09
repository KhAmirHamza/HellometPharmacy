package com.hellomet.pharmacy.Location;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class DeviceLocation {
    private static final String TAG = "DeviceLocation";

    Activity activity;
    int REQUEST_CHECK_SETTINGS;
    boolean locationPermissionValue;
    CustomLocationTask customLocationTask;

    CustomLocationTask.LocationRequestResult locationRequestResult;
    RequestCurrentDeviceLocation requestCurrentDeviceLocation;

    int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    String apiKey;

    public DeviceLocation(Activity activity, int REQUEST_CHECK_SETTINGS,int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION, String apiKey) {
        this.activity = activity;
        this.apiKey = apiKey;
        this.REQUEST_CHECK_SETTINGS = REQUEST_CHECK_SETTINGS;
        this.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

        //generate CustomLocation instance to access his property...
        customLocationTask = new CustomLocationTask(activity, apiKey);

    }
    //After location enabled, it will call getDeviceLocation() to generate device Current Location...
    public void requestDeviceLocation(RequestCurrentDeviceLocation requestCurrentDeviceLocation){
        this.requestCurrentDeviceLocation = requestCurrentDeviceLocation;
        locationRequestResult = new CustomLocationTask.LocationRequestResult() {
            @Override
            public void locationEnabled(LocationSettingsResponse locationSettingsResponse) {
                // location option enabled...
                getDeviceLocation();
            }

            @Override
            public void locationRequestError(Exception e) {
                requestCurrentDeviceLocation.locationRequestError(e);
            }
        };

        locationPermissionValue = customLocationTask.checkLocationPermission();

        if (locationPermissionValue){
            Log.d(TAG, "DeviceLocation: locationPermissionValue: "+locationPermissionValue);
            customLocationTask.createLocationRequest(locationRequestResult, REQUEST_CHECK_SETTINGS);
        }else {
            Log.d(TAG, "DeviceLocation: locationPermissionValue: "+locationPermissionValue);
            new CustomLocationTask(activity,apiKey).requestLocationPermission(PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // generate Device Current Location...
    public void getDeviceLocation() {
        CurrentLocation.LocationResult locationResult = new CurrentLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                // location got...
                if (location!=null){
                    requestCurrentDeviceLocation.onSuccess(location);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    getAddressFromLatLng(latLng);
                }else {
                    requestCurrentDeviceLocation.onFailure("Check Internet Connection & Try Again.");
                }
            }

            @Override
            public void somethingWentsWrong(String errorMessage) {
                //An Error Occured.
                //Try Again to get Device Location...
                // checkLocationAndGPS();
                requestCurrentDeviceLocation.onFailure(errorMessage);
            }
        };

        //Call getlocation() to active "CurrentLocation"'s override methods
        CurrentLocation currentLocation = new CurrentLocation();
        currentLocation.getLocation(activity, locationResult);
    }

    //Get Location Address from GeoCoder using latitude, longitude...
    public void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = null;
            if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                // progressbar_add_pharmacy.setVisibility(View.GONE);
                requestCurrentDeviceLocation.currentDeviceAddress(address);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // RequestCurrentDeviceLocation's abstract methods, these will override where RequestCurrentDeviceLocation called.
        public static abstract class RequestCurrentDeviceLocation{
            public abstract void onSuccess(Location location);
            public abstract void currentDeviceAddress(String address);
            public abstract void onFailure(String errorMessage);
            public abstract void locationRequestError(Exception e);
        }
}
