package com.jedit.kenklin.activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jedit.kenklin.R;

public class SplashActivity extends AppCompatActivity {

    //================================================ON CREATE=====================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new CountDownTimer(2500,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                toDashboard();
            }
        }.start();


    }
    //================================================ON CREATE=====================================

    void toDashboard(){
        Intent dashboard_intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(dashboard_intent);
        super.finish();
    }

}
