package com.hellomet.pharmacy.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hellomet.pharmacy.Adapter.OrderListAdapter;
import com.hellomet.pharmacy.ApiClient;
import com.hellomet.pharmacy.ApiRequests;
import com.hellomet.pharmacy.Model.Auth;
import com.hellomet.pharmacy.Model.Order;
import com.hellomet.pharmacy.Model.Pharmacy;
import com.hellomet.pharmacy.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.hellomet.pharmacy.ViewModel.PharmacyVM;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.pharmacy.Constants.MAIN_URL;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    MaterialToolbar profile_toolbar;
    ImageView imgv_profile_image;
    SharedPreferences sharedPreferences;
    TextView txtv_email;
    TextView txtv_name;
    TextView txtv_phone_number;
    RecyclerView recy_all_order;
    ProgressBar progressbar;
    Switch switch_status;
    PharmacyVM pharmacyVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("AUTHENTICATION", 0);
        imgv_profile_image = findViewById(R.id.imgv_profile_image);
        txtv_name = findViewById(R.id.txtv_name);
        txtv_email = findViewById(R.id.txtv_email);
        txtv_phone_number = findViewById(R.id.txtv_phone_number);
        progressbar = findViewById(R.id.progressbar);
        profile_toolbar = (MaterialToolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(profile_toolbar);
        setTitle((CharSequence) "Profile");

        switch_status = findViewById(R.id.switch_status);

        ApiRequests apiRequests = ApiClient.getInstance(MAIN_URL);

        String pharmacyPhoneNumber = sharedPreferences.getString("PHARMACY_PHONE_NUMBER", "null");
        Log.d(TAG, "onCreate: pharmacyPhoneNumber: " + pharmacyPhoneNumber);
        if (pharmacyPhoneNumber.equalsIgnoreCase("null")) {
            Log.d(TAG, "onCreateView: pharmacyPhoneNumber: " + pharmacyPhoneNumber);
        }
//        else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            pharmacyPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
//            pharmacyPhoneNumber = pharmacyPhoneNumber != null ? pharmacyPhoneNumber.substring(1) : "null";
//            Toast.makeText(ProfileActivity.this, pharmacyPhoneNumber, Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "onCreateView: Firebase: pharmacyPhoneNumber: " + pharmacyPhoneNumber);
//        }

        if (!pharmacyPhoneNumber.equalsIgnoreCase("null")) {
            progressbar.setVisibility(View.VISIBLE);
            Log.d(TAG, "onCreateView: Phone Number: " + pharmacyPhoneNumber);

            Pharmacy.MetaData metaData = new Pharmacy.MetaData(
                    "...", "...", "...",
                    "...", "...", "...", "...", "...");

            Auth auth = new Auth("...", "...");
            Pharmacy pharmacy = new Pharmacy(metaData);
            pharmacy.setAuth(auth);
            pharmacyVM = ViewModelProviders.of(this).get(PharmacyVM.class);
            pharmacyVM.getPharmacyUsingPhoneNumber(pharmacyPhoneNumber).observe(this, new Observer<Pharmacy>() {
                        @Override
                        public void onChanged(Pharmacy pharmacyData) {
                            if (pharmacyData.getMeta_data().getImage_url() != null) {
                                //Log.d(TAG, "onResponse: "+pharmacyLiveData.getValue().getMeta_data().getImage_url());
                                String status = pharmacyData.getMeta_data().getStatus();
                                if (status != null) {
                                    switch_status.setEnabled(true);
                                    if (status.equalsIgnoreCase("On")) {
                                        switch_status.setChecked(true);
                                    } else {
                                        switch_status.setChecked(false);
                                    }
                                    updateActiveStatus(pharmacyData);
                                }

                                Picasso.with(ProfileActivity.this)
                                        .load(pharmacyData.getMeta_data().getImage_url())
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .placeholder(R.drawable.person)
                                        .into(imgv_profile_image);


                                txtv_name.setText(pharmacyData.getMeta_data().getName());
                                //txtv_email.setText(response.body().getMeta_data().get());
                                txtv_phone_number.setText(pharmacyData.getMeta_data().getPhone_number());
                            }
                        }
                    });
        }

        sharedPreferences = getSharedPreferences("AUTHENTICATION", 0);
        String pharmacyID = sharedPreferences.getString("PHARMACY_ID", "null");
        getAllOrderThenSet(pharmacyID);
    }

    private void updateActiveStatus(Pharmacy pharmacy) {

        switch_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = switch_status.isChecked()?"On":"Off";
                Toast.makeText(ProfileActivity.this, status, Toast.LENGTH_SHORT).show();
                pharmacy.getMeta_data().setStatus(status);
                ApiClient.getInstance(MAIN_URL).updatePharmacyStatus(pharmacy.getId(), pharmacy)
                        .enqueue(new Callback<Pharmacy>() {
                            @Override
                            public void onResponse(Call<Pharmacy> call, Response<Pharmacy> response) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        switch_status.setChecked(switch_status.isChecked());
                                    } else {
                                        switch_status.setChecked(!switch_status.isChecked());
                                    }
                                }else {
                                    Log.d(TAG, "onResponse: Erorr:" +response.errorBody().toString());
                                    switch_status.setChecked(!switch_status.isChecked());
                                }
                            }

                            @Override
                            public void onFailure(Call<Pharmacy> call, Throwable t) {
                                Log.d(TAG, "onFailure: Error: "+t.getMessage());
                                switch_status.setChecked(!switch_status.isChecked());
                            }
                        });
            }
        });
        switch_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    private void getAllOrderThenSet(String pharmacyID) {
        recy_all_order = findViewById(R.id.recy_all_order);
        recy_all_order.setHasFixedSize(true);
        ApiRequests completedOrderApiRequest = ApiClient.getInstance(MAIN_URL);
        if (pharmacyID.equalsIgnoreCase("null")) {
            Toast.makeText(ProfileActivity.this, "Please Sign in.", Toast.LENGTH_SHORT).show();
        } else {
            progressbar.setVisibility(View.VISIBLE);
            completedOrderApiRequest.getOrdersById(pharmacyID).enqueue(new Callback<List<Order>>() {
                @Override
                public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                    progressbar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {

                            OrderListAdapter completedOrderListAdapter = new OrderListAdapter(ProfileActivity.this, response.body());
                            recy_all_order.setAdapter(completedOrderListAdapter);
                            //scrollView.scrollTo(0, scrollView.getBottom());
                            //scrollView.fullScroll(View.FOCUS_UP);

                            completedOrderListAdapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position, int[] itemHeight) {

                                }
                            });
                        } else {
                            Toast.makeText(ProfileActivity.this, "Nothing Found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //Toast.makeText(ProfileActivity.this, "Response Error: "+ response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: Error: " + response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<List<Order>> call, Throwable t) {
                    progressbar.setVisibility(View.GONE);
                    //Toast.makeText(ProfileActivity.this, "Error: "+ t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: Error: " + t.getLocalizedMessage());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);

        if (item.getItemId() == R.id.sign_out) {

            /*if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                if (!sharedPreferences.getString("PHARMACY_PHONE_NUMBER", "null").equalsIgnoreCase("null")) {
                    sharedPreferences.edit().remove("PHARMACY_PHONE_NUMBER").apply();
                    startActivity(new Intent(ProfileActivity.this, SignInActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    startActivity(new Intent(ProfileActivity.this, SignInActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            } else*/
                if (!sharedPreferences.getString("PHARMACY_PHONE_NUMBER", "null").equalsIgnoreCase("null")) {
                sharedPreferences.edit().clear().apply();
                startActivity(new Intent(ProfileActivity.this, SignInActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } else {
                Toast.makeText(ProfileActivity.this, "You are not registered!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, SignInActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
        return super.onOptionsItemSelected(item);
    }

}