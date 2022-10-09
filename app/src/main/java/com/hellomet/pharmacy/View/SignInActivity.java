package com.hellomet.pharmacy.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.hellomet.pharmacy.ApiClient;
import com.hellomet.pharmacy.Model.Pharmacy;
import com.hellomet.pharmacy.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.pharmacy.Constants.MAIN_URL;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    TextInputLayout textInputLayout_phone_number,textInputLayout_password;
    MaterialButton btn_sign_in,btn_goto_sign_up;
    ProgressBar progressbar;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseApp.initializeApp(this);
        textInputLayout_phone_number = findViewById(R.id.textInputLayout_phone_number);
        textInputLayout_password = findViewById(R.id.textInputLayout_password);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_goto_sign_up = findViewById(R.id.btn_goto_sign_up);
        progressbar = findViewById(R.id.progressbar);
        countryCodePicker = findViewById(R.id.ccp);

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = countryCodePicker.getSelectedCountryCode()+textInputLayout_phone_number.getEditText().getText().toString();
                String password = textInputLayout_password.getEditText().getText().toString();

                if (textInputLayout_phone_number.getEditText().getText().toString().isEmpty()){
                    textInputLayout_phone_number.setError("Required.");
                    textInputLayout_phone_number.requestFocus();
                    return;
                }
                if (password.isEmpty()){
                    textInputLayout_password.setError("Required.");
                    textInputLayout_password.requestFocus();
                    return;
                }

                //todo......
                progressbar.setVisibility(View.VISIBLE);
                ApiClient.getInstance(MAIN_URL+"pharmacy/").authenticatePharmacy(phoneNumber,password).
                        enqueue(new Callback<Pharmacy>() {
                    @Override
                    public void onResponse(Call<Pharmacy> call, Response<Pharmacy> response) {
                        progressbar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {

                            if (response.body()!=null) {
                            String id = response.body().getId();
                            String name = response.body().getMeta_data().getName();
                            String phone_number = response.body().getMeta_data().getPhone_number();

                                SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
                                sharedPreferences.edit().putString("PHARMACY_PHONE_NUMBER", phone_number).apply();
                                sharedPreferences.edit().putString("PHARMACY_ID", id).apply();
                                sharedPreferences.edit().putString("PHARMACY_NAME", name).apply();
                                startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
                                finish();
                            }else {
                                Toast.makeText(SignInActivity.this, "Phone Number or Password does not matched.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onResponse: authenticatePharmacy: response null.");
                            }

                        } else {
                            Toast.makeText(SignInActivity.this, "Phone number or password does not matched!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: Code: "+response.code());

                        }
                    }

                    @Override
                    public void onFailure(Call<Pharmacy> call, Throwable t) {
                        progressbar.setVisibility(View.GONE);
                        Toast.makeText(SignInActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


                //finish();
            }
        });

        btn_goto_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        //sharedPreferences.edit().clear().commit();
        /*FirebaseApp.initializeApp(this);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()!=null
                && sharedPreferences.getString("PHARMACY_PHONE_NUMBER","null").equalsIgnoreCase("null")){
            startActivity(new Intent(SignInActivity.this, ProfileSetUpActivity.class)
                    .putExtra("phoneNumber", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
            finish();
            Toast.makeText(this, "User Sign in", Toast.LENGTH_SHORT).show();
        }else*/
        if(!sharedPreferences.getString("PHARMACY_PHONE_NUMBER","null").equalsIgnoreCase("null") &&
                sharedPreferences.getString("PHARMACY_PHONE_NUMBER","null")!=null){
            startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
            finish();
            Toast.makeText(this, "User Sign in", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "user not sign in", Toast.LENGTH_SHORT).show();
        }
    }

}