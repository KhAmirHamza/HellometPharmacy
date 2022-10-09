package com.hellomet.pharmacy.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.hellomet.pharmacy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {
    private static final String TAG = "VerificationActivity";

    String OTP_CODE;
    Button btn_verify_number, btn_resend_code;
    TextView txtv3;
    PinView PinView;
    String phoneNumber;
    ProgressBar progressbar;
    LinearLayout lay_1,lay_5;
    String token = null;

    FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        Log.d(TAG, "onClick: phoneNumber: " + phoneNumber);

        btn_verify_number = findViewById(R.id.btn_verify_number);
        btn_resend_code = findViewById(R.id.btn_resend_code);

        txtv3 = findViewById(R.id.txtv3);
        PinView = findViewById(R.id.pinview);
        lay_1 = findViewById(R.id.lay_1);
        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.VISIBLE);


        txtv3.setText(phoneNumber);
        mAuth = FirebaseAuth.getInstance();
        sendVerificationCode(phoneNumber);


        btn_verify_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressbar.getVisibility() == View.VISIBLE) {
                    Toast.makeText(VerificationActivity.this, "Task is in progress", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressbar.setVisibility(View.VISIBLE);
                String otp = PinView.getText().toString();
                if (otp.length() == 6) {

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                    signInWithPhoneAuthCredential(credential);

                    //PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationOTP, otp);
                    //signInWithPhoneAuthCredential(credential);

                } else {
                    Toast.makeText(VerificationActivity.this, "Please insert 6 digit verification code", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                }
            }
        });

        btn_resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);
                sendVerificationCode(phoneNumber);
                btn_resend_code.setVisibility(View.GONE);
                lay_1.setVisibility(View.VISIBLE);
                PinView.setText("");
            }
        });

        PinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                OTP_CODE = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void sendVerificationCode(String phoneNumber) {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
               /* Log.d(TAG, "onVerificationFailed"+ e.getCause());
                Log.d(TAG, "onVerificationFailed"+ e.getLocalizedMessage());*/

                Log.d(TAG, "onVerificationFailed: PhoneNumber" + phoneNumber);
                Log.d(TAG, "onVerificationFailed: Message: " + e.getLocalizedMessage());
                //if (e.getCause() == null) {
                Toast.makeText(VerificationActivity.this, "Phone Number is invalid or try after sometime",Toast.LENGTH_SHORT).show();
                lay_1.setVisibility(View.INVISIBLE);
                btn_resend_code.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
                //}

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.d(TAG, "onVerificationFailed: FirebaseAuthInvalidCredentialsException: " + e.getLocalizedMessage());
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Log.d(TAG, "onVerificationFailed: FirebaseTooManyRequestsException: " + e.getLocalizedMessage());
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                progressbar.setVisibility(View.GONE);
                Toast.makeText(VerificationActivity.this, "Code Sent, Check your Mobile.", Toast.LENGTH_SHORT).show();
                // ...
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+"+phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressbar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //Toast.makeText(VerificationActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(VerificationActivity.this, ProfileSetUpActivity.class)
                            .putExtra("phoneNumber", phoneNumber));
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(VerificationActivity.this, "The verification code entered is invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

      });
    }
}