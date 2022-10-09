package com.hellomet.pharmacy.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.hbb20.CountryCodePicker;
import com.hellomet.pharmacy.Model.Pharmacy;
import com.hellomet.pharmacy.R;

import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.hellomet.pharmacy.ViewModel.PharmacyVM;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignUpActivity";
    TextInputLayout text_input_layout_phone_number;
    MaterialButton btn_sign_up,btn_goto_sign_in;
    ProgressBar progressbar;
    PharmacyVM pharmacyVM;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressbar = findViewById(R.id.progressbar);
        text_input_layout_phone_number = findViewById(R.id.text_input_layout_phone_number);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_goto_sign_in = findViewById(R.id.btn_goto_sign_in);

        btn_sign_up.setOnClickListener(this);
        btn_goto_sign_in.setOnClickListener(this);
        countryCodePicker = findViewById(R.id.ccp);

    }


    @Override
    public void onClick(View v) {
        if (v.getId()==text_input_layout_phone_number.getId()){
            text_input_layout_phone_number.getEditText().setCursorVisible(true);
        }else if (v.getId()==btn_sign_up.getId()){

            String phoneNumber = countryCodePicker.getSelectedCountryCode()+text_input_layout_phone_number.getEditText().getText().toString();

            if (!text_input_layout_phone_number.getEditText().getText().toString().isEmpty()){
                progressbar.setVisibility(View.VISIBLE);

                pharmacyVM = ViewModelProviders.of(this).get(PharmacyVM.class);
                pharmacyVM.getPharmacyUsingPhoneNumber(phoneNumber).observe(this, new Observer<Pharmacy>() {
                            @Override
                            public void onChanged(Pharmacy pharmacyData) {
                                if (pharmacyData!=null){
                                    Toast.makeText(SignUpActivity.this, "An Account is already created with this phone number,Try another.", Toast.LENGTH_SHORT).show();
                                }else {
                                    progressbar.setVisibility(View.GONE);
                                    startActivity(new Intent(SignUpActivity.this,VerificationActivity.class).putExtra("phoneNumber",phoneNumber));
                                }
                            }
                        });
            }
            else {
                Toast.makeText(this, "Please insert valid phone number", Toast.LENGTH_SHORT).show();
            }

        } else if (v.getId() == R.id.btn_goto_sign_in) {
            startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            // finish();
        }
    }}