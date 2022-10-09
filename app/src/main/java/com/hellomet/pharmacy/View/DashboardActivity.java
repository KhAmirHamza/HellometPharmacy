package com.hellomet.pharmacy.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hellomet.pharmacy.Adapter.ViewPagerAdapter;
import com.hellomet.pharmacy.ApiClient;
import com.hellomet.pharmacy.ApiRequests;
import com.hellomet.pharmacy.BroadcastReceiver;
import com.hellomet.pharmacy.Location.DeviceLocation;
import com.hellomet.pharmacy.Model.FCM;
import com.hellomet.pharmacy.Model.Order;
import com.hellomet.pharmacy.MyFirebaseMessagingService;
import com.hellomet.pharmacy.R;
import com.hellomet.pharmacy.UI.SystemUI;
import com.hellomet.pharmacy.View.Fragment.ActiveOrderFragment;
import com.hellomet.pharmacy.View.Fragment.OnTheWayOrderFragment;
import com.hellomet.pharmacy.View.Fragment.PendingOrderFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.pharmacy.Constants.API_KEY;
import static com.hellomet.pharmacy.Constants.MAIN_URL;
import static com.hellomet.pharmacy.Constants.ORDER_ACTIVE;
import static com.hellomet.pharmacy.Constants.ORDER_ON_THE_WAY;
import static com.hellomet.pharmacy.Constants.ORDER_PENDING;
import static com.hellomet.pharmacy.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.hellomet.pharmacy.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;
import static com.hellomet.pharmacy.Constants.REQUEST_CHECK_SETTINGS;
import static com.hellomet.pharmacy.Constants.REQUEST_CHECK__MANUAL_SETTINGS;


public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    DeviceLocation deviceLocation;
    boolean locationPermissionGranted = false;
    String PHARMACY_ID, PHARMACY_NAME, PHARMACY_PHONE_NUMBER, PHARMACY_PASSWORD;
    ProgressBar progressbar;
    MaterialToolbar main_toolbar;

    SwipeRefreshLayout swipeRefreshLayout;

    TabLayout tab_layout;
    ViewPager view_pager;
    List<Fragment> fragmentList = new ArrayList<>();
    MyFirebaseMessagingService myFirebaseMessagingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        main_toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(main_toolbar);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressbar = findViewById(R.id.progressbar);
        tab_layout = findViewById(R.id.tab_layout);
        view_pager = findViewById(R.id.view_pager);


        SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        PHARMACY_ID = sharedPreferences.getString("PHARMACY_ID", "null");
        PHARMACY_NAME = sharedPreferences.getString("PHARMACY_NAME", "null");
        PHARMACY_PHONE_NUMBER = sharedPreferences.getString("PHARMACYPHONE_NUMBER", "null");
        PHARMACY_PASSWORD = sharedPreferences.getString("PHARMACY_PASSWORD", "null");


        SharedPreferences sp_location = getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
        String latitude = sp_location.getString("Lat","null");
        String longitude = sp_location.getString("Lng","null");

        setUpViewPagerWithFragmentAndAdapter(view_pager);
        tab_layout.setupWithViewPager(view_pager);



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //finish();
                //startActivity(getIntent());

                //select current tab
                TabLayout.Tab tab = tab_layout.getTabAt(tab_layout.getSelectedTabPosition());
                tab.select();

                // Reload current fragment
                Fragment frg = null;
                frg = fragmentList.get(tab_layout.getSelectedTabPosition());
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        requestDeviceLocation();

        setUpFirebaseMessagingInstanceAndNotificationChannel();
    }

    private void sendRegistrationTokenToServer(String token, String id){
        Log.d(TAG, "onComplete: Current Token: " + token);
        Log.d(TAG, "onComplete: id: "+id);
        if (!id.equalsIgnoreCase("null")) {
            if (token == null) {
                Log.d(TAG, "onComplete: token is null!");
            } else {
                ApiClient.getInstance(MAIN_URL).getFcmToken(id).enqueue(new Callback<FCM>() {
                    @Override
                    public void onResponse(Call<FCM> call, Response<FCM> response) {
                        if (response.isSuccessful()){
                            if (response.body() == null) {
                                addToken(id, token);
                            }else{
                                updateToken(id, token);
                            }
                        }else{
                            Log.d(TAG, "onResponse: "+response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<FCM> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t.getMessage());
                    }
                });
            }
        }
    }

    private void addToken(String id, String token) {
        Log.d(TAG, "addToken: called");
        FCM fcm = new FCM(id, token);
        ApiRequests apiRequests = ApiClient.getInstance(MAIN_URL);
        apiRequests.createFcmToken(fcm).enqueue(new Callback<FCM>() {
            @Override
            public void onResponse(Call<FCM> call, Response<FCM> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: token: " + response.body().getToken());
                } else {
                    Log.d(TAG, "onResponse: token: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<FCM> call, Throwable t) {
                Log.d(TAG, "onFailure: fcm : " + t.getMessage());
            }
        });
    }

    private void updateToken(String id, String token) {
        Log.d(TAG, "updateToken: called");
        FCM fcm = new FCM(id, token);
        ApiRequests apiRequests = ApiClient.getInstance(MAIN_URL);
        apiRequests.updateFcmToken(id, fcm).enqueue(new Callback<FCM>() {
            @Override
            public void onResponse(Call<FCM> call, Response<FCM> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.body().getToken());
                } else {
                    Log.d(TAG, "onResponse: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<FCM> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    private void setUpFirebaseMessagingInstanceAndNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                       // String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, token);
                        //Toast.makeText(DashboardActivity.this, token, Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
                        String id = sharedPreferences.getString("PHARMACY_ID", "null");
                        sendRegistrationTokenToServer(token, id);
                    }
                });
        myFirebaseMessagingService = new MyFirebaseMessagingService();
        Intent mServiceIntent = new Intent(this, myFirebaseMessagingService.getClass());
        startService(mServiceIntent);
    }

    private void setUpViewPagerWithFragmentAndAdapter(ViewPager view_pager){
        fragmentList.add( new PendingOrderFragment());
        fragmentList.add( new ActiveOrderFragment());
        fragmentList.add( new OnTheWayOrderFragment());
       // fragmentList.add( new CancelledOrderFragment());

        List<String> titleList = new ArrayList<>();
        titleList.add(ORDER_PENDING);
        titleList.add(ORDER_ACTIVE);
        titleList.add(ORDER_ON_THE_WAY);
      //  titleList.add("Cancelled");

        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragmentManager, DashboardActivity.this, titleList, fragmentList);
        view_pager.setAdapter(viewPagerAdapter);
    }

    public void requestDeviceLocation() {
        deviceLocation = new DeviceLocation(DashboardActivity.this, REQUEST_CHECK_SETTINGS,
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION, API_KEY);

        DeviceLocation.RequestCurrentDeviceLocation requestCurrentDeviceLocation =
                new DeviceLocation.RequestCurrentDeviceLocation() {
                    @Override
                    public void onSuccess(Location location) {

                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());

                        SharedPreferences sharedPreferences = getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("Lat", latitude);
                        editor.putString("Lng", longitude);
                        editor.apply();
                    }

                    @Override
                    public void currentDeviceAddress(String address) {
                        //Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }

                    @Override
                    public void locationRequestError(Exception e) {

                    }
                };

        deviceLocation.requestDeviceLocation(requestCurrentDeviceLocation);
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
            //Toast.makeText(this, "Well,All Location settings are satisfied now!", Toast.LENGTH_SHORT).show();
            locationPermissionGranted = true;
            requestDeviceLocation();
        }
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Please turn on Location with High Accuracy", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, REQUEST_CHECK__MANUAL_SETTINGS);
        }

        if (requestCode == REQUEST_CHECK__MANUAL_SETTINGS && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                   // Toast.makeText(this, "Everything is ok", Toast.LENGTH_SHORT).show();
                    locationPermissionGranted = true;
                    requestDeviceLocation();
                } else {
                    Toast.makeText(this, "High Accuracy Location permision required", Toast.LENGTH_SHORT).show();
                    requestDeviceLocation();
                }
            } else {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationPermissionGranted = true;
                    //Toast.makeText(this, "Everything is ok", Toast.LENGTH_SHORT).show();
                    requestDeviceLocation();
                } else {
                    Toast.makeText(this, "High Accuracy Location permision required", Toast.LENGTH_SHORT).show();
                    requestDeviceLocation();
                }
            }
        }
        if (requestCode == REQUEST_CHECK__MANUAL_SETTINGS && resultCode == RESULT_CANCELED) {
            requestDeviceLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
                    requestDeviceLocation();
                }
            }
        }
    }





    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            new SystemUI(DashboardActivity.this).hideSystemUI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile) {
            startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, BroadcastReceiver.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

}