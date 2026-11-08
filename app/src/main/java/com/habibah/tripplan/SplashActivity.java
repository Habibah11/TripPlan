package com.habibah.tripplan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            PrefsManager p = new PrefsManager(this);
            Class<?> next = p.isOnboardingDone() ? MainActivity.class : OnboardingActivity.class;
            startActivity(new Intent(this, next));
            finish();
        }, 1500);
    }
}
