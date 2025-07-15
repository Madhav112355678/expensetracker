package com.example.trackmymoney;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Handler;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


//first activity that launches after user opens this application
//shows splash screen on android 12- devices
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);


        //get animation
        new Handler().postDelayed(()->{
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (isLoggedIn) {
                Toast.makeText(SplashActivity.this, "Welcome Back!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
            } else {
                Toast.makeText(SplashActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish();
        },3000);

    }
}