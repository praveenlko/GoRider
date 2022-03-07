package com.example.gorider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MobileLoginActivity extends AppCompatActivity {
    private EditText mobile;
    private ProgressBar progressBar;
    private PinView pinView;
    private LinearLayout layout,pinLayout;
    private static final int CREDENTIAL_PICKER_REQUEST = 120;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_login);

        mobile = findViewById(R.id.mobile_no);
        progressBar = findViewById(R.id.progressBar);
        pinView = findViewById(R.id.firstPinView);
        layout = findViewById(R.id.layout);
        pinLayout = findViewById(R.id.pin_layout);
        mAuth = FirebaseAuth.getInstance();

        if(mobile.getText().toString().equals("")){
            progressBar.setVisibility(View.GONE);
        }

        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 10) {
                    sendOtp();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 6) {
                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,pinView.getText().toString().trim());
                    signInWithAuthCredential(credential);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        ////////////////////////  auto phone select api
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();


        PendingIntent intent = Credentials.getClient(MobileLoginActivity.this).getHintPickerIntent(hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0, new Bundle());
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
        ////////////////////////  auto phone select api

        ////////////////////////  otp callback

        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    pinView.setText(code);
                    signInWithAuthCredential(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                pinLayout.setVisibility(View.GONE);

                Toast.makeText(MobileLoginActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                mVerificationId = verificationId;
                mResendToken = forceResendingToken;

                Toast.makeText(MobileLoginActivity.this, "6 digit otp sent", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                pinLayout.setVisibility(View.VISIBLE);

            }
        };

        ////////////////////////  otp callback

    }


    private void sendOtp() {
        progressBar.setVisibility(View.VISIBLE);

        String mob = "+91"+mobile.getText().toString();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mob)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {
            // Obtain the phone number from the result
            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);
            /* EditText.setText(credentials.getId().substring(3));*/ //get the selected phone number
            //Do what ever you want to do with your selected phone number here

            mobile.setText(credentials.getId().substring(3));

        } else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE) {
            // *** No phone numbers available ***
            Toast.makeText(MobileLoginActivity.this, "No phone numbers found", Toast.LENGTH_LONG).show();
        }
    }

    private void signInWithAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
           if (task.isSuccessful()){

               Toast.makeText(MobileLoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(MobileLoginActivity.this,HomeActivity.class);
               startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MobileLoginActivity.this).toBundle());
               finish();

           }   else{
               Toast.makeText(MobileLoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(MobileLoginActivity.this,LoginActivity.class);
               startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MobileLoginActivity.this).toBundle());
               finish();
           }
            }
        });
    }
}