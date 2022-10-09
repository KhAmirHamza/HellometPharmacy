package com.hellomet.pharmacy.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hellomet.pharmacy.ApiClient;
import com.hellomet.pharmacy.ApiRequests;
import com.hellomet.pharmacy.Location.CurrentLocation;
import com.hellomet.pharmacy.Model.Auth;
import com.hellomet.pharmacy.Model.Pharmacy;
import com.hellomet.pharmacy.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hellomet.pharmacy.ViewModel.PharmacyVM;
import com.hellomet.pharmacy.ViewModel.UploadImageVM;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hellomet.pharmacy.Constants.MAIN_URL;

public class ProfileSetUpActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 999;
    Location userLocation = null;
    private static final int REQUEST_CHECK_SETTINGS = 1;
    private static final int REQUEST_CHECK_MANUAL_SETTINGS = 2;
    private boolean locationPermissionGranted = false;

    private static final String TAG = "ProfileSetUpActivity";
    public static final String apiKey = "AIzaSyDRvBYRB7bG5CSzq2SL2fv2EuGG9ti5EyA";
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    private PlacesClient placesClient;

    EditText edt_pharmacy_name, edt_founder_name, edt_address, edt_phone_number,edt_password;
    String pharmacy_name, founder_name, address, phone_number, password;
    Button btn_choose_image,btn_submit_profile;
    CardView cardview_location;
    TextView txtv_current_location;
    ImageView imgv_pharmacy_image;
    ProgressBar progressbar_add_pharmacy;
    PharmacyVM pharmacyVM;
    UploadImageVM uploadImageVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_set_up);

        imgv_pharmacy_image = findViewById(R.id.imgv_pharmacy_image);
        btn_choose_image = findViewById(R.id.btn_choose_image);
        edt_pharmacy_name = findViewById(R.id.edt_pharmacy_name);
        edt_founder_name = findViewById(R.id.edt_founder_name);
        edt_address = findViewById(R.id.edt_address);
        edt_phone_number = findViewById(R.id.edt_phone_number);
        edt_password = findViewById(R.id.edt_password);
        btn_submit_profile = findViewById(R.id.btn_submit_profile);
        progressbar_add_pharmacy = findViewById(R.id.progressbar_add_pharmacy);
        cardview_location = findViewById(R.id.cardview_location);
        txtv_current_location = findViewById(R.id.txtv_current_location);

        progressbar_add_pharmacy.setVisibility(View.VISIBLE);
        edt_phone_number.setText(getIntent().getStringExtra("phoneNumber"));
        //edt_phone_number.setText("8801705187083");
        edt_phone_number.setFocusable(false);

        getLocationPermission();

        // Initialize Places.
        Places.initialize(ProfileSetUpActivity.this, apiKey);
        // Create a new Places client instance.
        placesClient = Places.createClient(ProfileSetUpActivity.this);
    }

    protected void createLocationRequest() {
        Log.d(TAG, "createLocationRequest: called");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Toast.makeText(ProfileSetUpActivity.this, "Location Enabled", Toast.LENGTH_SHORT).show();
                getDeviceLocation();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(ProfileSetUpActivity.this,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERMISSIONS_REQUEST_ENABLE_GPS) {
            if (locationPermissionGranted) {
                //   checkLocationAndGPS();
            } else {
                //  checkLocationAndGPS();
            }
        }

        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            Toast.makeText(this, "Well,All Location settings are satisfied now!", Toast.LENGTH_SHORT).show();
            locationPermissionGranted = true;
            getDeviceLocation();
        }
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_CANCELED) {

            Toast.makeText(this, "Please turn on Location with High Accuracy", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, REQUEST_CHECK_MANUAL_SETTINGS);
        }

        if (requestCode == REQUEST_CHECK_MANUAL_SETTINGS && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Everything is ok", Toast.LENGTH_SHORT).show();
                    locationPermissionGranted = true;
                    getDeviceLocation();
                } else {
                    Toast.makeText(this, "High Accuracy Location permision required", Toast.LENGTH_SHORT).show();
                    createLocationRequest();
                }
            } else {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationPermissionGranted = true;
                    Toast.makeText(this, "Everything is ok", Toast.LENGTH_SHORT).show();
                    getDeviceLocation();
                } else {
                    Toast.makeText(this, "High Accuracy Location permision required", Toast.LENGTH_SHORT).show();
                    createLocationRequest();
                }
            }
        }
        if (requestCode == REQUEST_CHECK_MANUAL_SETTINGS && resultCode == RESULT_CANCELED) {
            createLocationRequest();
        }

        receiveImageFromDeviceRequest(requestCode,resultCode,data);
    }


    private void receiveImageFromDeviceRequest(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imgv_pharmacy_image.setImageURI(imageUri);

            try {
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);

                btn_submit_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        pharmacy_name = edt_pharmacy_name.getText().toString();
                        founder_name = edt_founder_name.getText().toString();
                        address = edt_address.getText().toString();
                        phone_number = edt_phone_number.getText().toString();
                        password = edt_password.getText().toString();

                        if (pharmacy_name.isEmpty()) {
                            edt_pharmacy_name.setError("Pharmacy name required!");
                            edt_pharmacy_name.requestFocus();
                        } else if (founder_name.isEmpty()) {
                            edt_founder_name.setError("Founder name required!");
                            edt_founder_name.requestFocus();
                        } else if (address.isEmpty()) {
                            edt_address.setError("Address required!");
                            edt_address.requestFocus();
                        } else if (phone_number.isEmpty()) {
                            edt_phone_number.setError("Phone Number required!");
                            edt_phone_number.requestFocus();
                        } else if (password.isEmpty()) {
                            edt_password.setError("Password required!");
                            edt_password.requestFocus();
                        } else {

                            MultipartBody.Part multipartImageFile = convertBitmapToMultipartBodyPart(selectedImageBitmap);
                            if (multipartImageFile!=null){
                                addPharmacy(multipartImageFile);
                            }else{
                                Toast.makeText(ProfileSetUpActivity.this, "Small size image is needed!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private MultipartBody.Part convertBitmapToMultipartBodyPart(Bitmap bitmap){
        MultipartBody.Part multipartImageFile = null;
        File filesDir = getApplicationContext().getFilesDir();
        File file = new File(filesDir, "Image_" + System.currentTimeMillis() + ".jpg");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapdata = byteArrayOutputStream.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            multipartImageFile = MultipartBody.Part.createFormData("uploadImage", file.getName(), reqFile);
            return multipartImageFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return multipartImageFile;
        }

    private void addPharmacy(MultipartBody.Part multipartImageFile) {
        progressbar_add_pharmacy.setVisibility(View.VISIBLE);
        Log.d(TAG, "addPharmacy: called");

            //Upload image to database for generate image url...
            uploadImageVM = ViewModelProviders.of(ProfileSetUpActivity.this).get(UploadImageVM.class);
            uploadImageVM.uploadImageToGenerateUrl(multipartImageFile).observe(this, new Observer<JsonObject>() {
                @Override
                public void onChanged(JsonObject jsonObjectData) {
                    Log.d(TAG, "onChanged: called 1");
                    if (jsonObjectData != null) {
                        Toast.makeText(ProfileSetUpActivity.this, "Image Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonObjectData.toString());
                            String image_url = jsonObject.getString("url");
                            //todo.....
                            Auth auth = new Auth(phone_number, password);
                            Log.d(TAG, "onChanged: Image Url: "+image_url);
                            Log.d(TAG, "phone_number: "+ phone_number);
                            Pharmacy.MetaData metaData = new Pharmacy.MetaData(pharmacy_name, founder_name, image_url, address, phone_number, String.valueOf(userLocation.getLatitude()), String.valueOf(userLocation.getLongitude()),"Off");
                            Pharmacy pharmacy = new Pharmacy(metaData);
                            pharmacy.setAuth(auth);

                            // upload complete pharmacy to database...
                            pharmacyVM = ViewModelProviders.of(ProfileSetUpActivity.this).get(PharmacyVM.class);
                            pharmacyVM.createPharmacy(pharmacy).observe(ProfileSetUpActivity.this, new Observer<Pharmacy>() {
                                @Override
                                public void onChanged(Pharmacy pharmacy) {
                                    Log.d(TAG, "onChanged: called 2");
                                    progressbar_add_pharmacy.setVisibility(View.GONE);
                                    if (pharmacy != null) {
                                        Toast.makeText(ProfileSetUpActivity.this, "Pharmacy Successfully Added", Toast.LENGTH_SHORT).show();
                                        String id = pharmacy.getId();
                                        String name = pharmacy.getMeta_data().getName();
                                        if (id != null && name != null && phone_number != null) {
                                            // Save Pharmacy Authentication Data SharedPreference...
                                            SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
                                            sharedPreferences.edit().putString("PHARMACY_PHONE_NUMBER", phone_number).apply();
                                            sharedPreferences.edit().putString("PHARMACY_ID", id).apply();
                                            sharedPreferences.edit().putString("PHARMACY_NAME", pharmacy_name).apply();
                                            startActivity(new Intent(ProfileSetUpActivity.this, DashboardActivity.class));
                                            finish();
                                        }
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            progressbar_add_pharmacy.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }else {
                        Log.d(TAG, "onChanged: jsonObjectData is null");
                    }
                }
            });

    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
            createLocationRequest();
        } else {
            Toast.makeText(this, "False", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
                createLocationRequest();
            }
        }
    }

    public String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = null;
            if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                // progressbar_add_pharmacy.setVisibility(View.GONE);
                return address;
            } else {
                Toast.makeText(this, "Addresses is empty", Toast.LENGTH_SHORT).show();
                // progressbar_add_pharmacy.setVisibility(View.GONE);
                return address;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: called");
        progressbar_add_pharmacy.setVisibility(View.GONE);
        CurrentLocation.LocationResult locationResult = new CurrentLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                //Got the location!
                Log.d(TAG, "gotLocation: called");
                if (location == null) {
                    Log.d(TAG, "gotLocation: location is null");
                    //then start from first...
                    getLocationPermission();
                    return;
                }

                String currentAddress = getAddressFromLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                if (currentAddress!=null){
                    userLocation = location;
                    txtv_current_location.setText(currentAddress);
                }else {
                    txtv_current_location.setText("Can not get address.");
                    //then start from first...
                    getLocationPermission();
                }

                btn_choose_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                    }
                });

            }

            @Override
            public void somethingWentsWrong(String errorMessage) {
                //An Error Occured.
                //Try Again to get Device Location...
                Log.d(TAG, "somethingWentsWrong: "+errorMessage);
                 //checkLocationAndGPS();

            }
        };

        CurrentLocation currentLocation = new CurrentLocation();
        currentLocation.getLocation(this, locationResult);
    }

   /* public void checkLocationAndGPS(){
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
              getLocationPermission();
        }
    }*/




    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }


    public void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                //      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                //      | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                //View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

}