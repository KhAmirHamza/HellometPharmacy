package com.hellomet.pharmacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hellomet.pharmacy.Adapter.OrderListAdapter;
import com.hellomet.pharmacy.Model.FCM;
import com.hellomet.pharmacy.View.DisplayOrderActivity;
import com.hellomet.pharmacy.View.SignInActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.pharmacy.Constants.MAIN_URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    MaterialToolbar toolbar_dashboard;
    SwipeRefreshLayout swipe_refresh_dashboard;
    RecyclerView recy_order_list;
    LinearLayout linear_layout_new_order, linear_layout_active_order, linear_layout_completed_order, linear_layout_cancelled_order;
    SharedPreferences sharedPreferences;
    ProgressBar progressbar_dashboard;
    OrderListAdapter orderListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar_dashboard = findViewById(R.id.toolbar_dashboard);
        setSupportActionBar(toolbar_dashboard);

        linear_layout_new_order = findViewById(R.id.linear_layout_new_order);
        linear_layout_active_order = findViewById(R.id.linear_layout_active_order);
        linear_layout_completed_order = findViewById(R.id.linear_layout_completed_order);
        linear_layout_cancelled_order = findViewById(R.id.linear_layout_cancelled_order);
        progressbar_dashboard = findViewById(R.id.progressbar_dashboard);
        recy_order_list = findViewById(R.id.recy_order_list);


        linear_layout_new_order.setOnClickListener(this);
        linear_layout_active_order.setOnClickListener(this);
        linear_layout_completed_order.setOnClickListener(this);
        linear_layout_cancelled_order.setOnClickListener(this);

        sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);

        swipe_refresh_dashboard = findViewById(R.id.swipe_refresh_dashboard);
        swipe_refresh_dashboard.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity.this, "Refresh", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_refresh_dashboard.setRefreshing(false);
                        //todo...
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                }, 2000);
            }
        });

        //Firebase Cloud Messaging
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
                        Log.d(TAG, "onComplete: " + token);

                        SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
                        String id = sharedPreferences.getString("PHARMACY_ID", "null");
                        sendRegistrationTokenToServer(token, id);

                    }
                });




     /*   Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hellomet-health-98.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        String pharmacy_phone_number = sharedPreferences.getString("PHARMACY_PHONE_NUMBER", "null");
        if (pharmacy_phone_number == null || pharmacy_phone_number.equalsIgnoreCase("null")) {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiRequests apiRequests = retrofit.create(ApiRequests.class);
        apiRequests.getOrders(pharmacy_phone_number).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                progressbar_dashboard.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        Toast.makeText(MainActivity.this, "Order not Found", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (response.body().size() < 1) {
                        Toast.makeText(MainActivity.this, "Order not Found", Toast.LENGTH_SHORT).show();
                    }
                    //todo...
                    Toast.makeText(MainActivity.this, "Initial Height: " + recy_order_list.getLayoutParams().height, Toast.LENGTH_SHORT).show();
                    orderListAdapter = new OrderListAdapter(MainActivity.this, response.body());
                    recy_order_list.setHasFixedSize(true);
                    recy_order_list.setAdapter(orderListAdapter);

                    orderListAdapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, int[] itemHeight) {
                            Toast.makeText(MainActivity.this, "Modified Height: " + itemHeight[0], Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Log.d(TAG, "onResponse: ResponseError: " + response.errorBody().toString());
                    Toast.makeText(MainActivity.this, "ResponseError: " + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                progressbar_dashboard.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: Error: " + t.getMessage());
            }
        });
*/
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                if (!sharedPreferences.getString("PHARMACY_PHONE_NUMBER", "null").equalsIgnoreCase("null")) {
                    sharedPreferences.edit().remove("PHARMACY_PHONE_NUMBER").apply();
                    startActivity(new Intent(MainActivity.this, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }

            } else if (!sharedPreferences.getString("PHARMACY_PHONE_NUMBER", "null").equalsIgnoreCase("null")) {
                sharedPreferences.edit().remove("PHARMACY_PHONE_NUMBER").apply();
                startActivity(new Intent(MainActivity.this, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } else {
                Toast.makeText(MainActivity.this, "You are not registered!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
        return true;
    }

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
            getWindow().getDecorView().setSystemUiVisibility(
                    /*View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            //      | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |*/ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            //View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            //| View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    /*            private void hideSystemUI() {
                    // Enables regular immersive mode.
                    // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
                    // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    View decorView = getWindow().getDecorView();
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_IMMERSIVE
                                    // Set the content to appear under the system bars so that the
                                    // content doesn't resize when the system bars hide and show.
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    // Hide the nav bar and status bar
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
                }*/
    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.linear_layout_new_order) {
            Intent intent = new Intent(MainActivity.this, DisplayOrderActivity.class);
            intent.putExtra("ACTION", "new");
            startActivity(intent);

        } else if (v.getId() == R.id.linear_layout_active_order) {
            Intent intent = new Intent(MainActivity.this, DisplayOrderActivity.class);
            intent.putExtra("ACTION", "active");
            startActivity(intent);

        } else if (v.getId() == R.id.linear_layout_completed_order) {
            Intent intent = new Intent(MainActivity.this, DisplayOrderActivity.class);
            intent.putExtra("ACTION", "completed");
            startActivity(intent);

        } else if (v.getId() == R.id.linear_layout_cancelled_order) {
            Intent intent = new Intent(MainActivity.this, DisplayOrderActivity.class);
            intent.putExtra("ACTION", "cancelled");
            startActivity(intent);
        }
    }
}