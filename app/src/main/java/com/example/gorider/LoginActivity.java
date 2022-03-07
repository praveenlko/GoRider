package com.example.gorider;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeImageTransform;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    private Button mobileLogin, googleLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////////////////  for transition
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        // set an exit transition
        getWindow().setExitTransition(new ChangeImageTransform());
        ////////////////  for transition
        setContentView(R.layout.activity_login);

        mobileLogin = findViewById(R.id.mobile_login);
        googleLogin = findViewById(R.id.google_login);

        mobileLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MobileLoginActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}