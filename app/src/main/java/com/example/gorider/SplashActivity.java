package com.example.gorider;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeImageTransform;
import android.transition.Explode;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private ImageView image;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////////////////  for transition
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        // set an exit transition
        getWindow().setExitTransition(new ChangeImageTransform());
        ////////////////  for transition
        setContentView(R.layout.activity_splash);


        ///////////////////// hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        image = findViewById(R.id.splash_car);
        name = findViewById(R.id.splash_tv);

        Animation animationName = AnimationUtils.loadAnimation(SplashActivity.this,R.anim.right);
        name.startAnimation(animationName);
        Animation animationimage = AnimationUtils.loadAnimation(SplashActivity.this,R.anim.left);
        image.startAnimation(animationimage);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this).toBundle());
                finish();
            }
        },4500);
    }
}