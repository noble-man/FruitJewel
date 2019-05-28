package com.fruitjewel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Hello");
        Log.i("testLog","un deux un deux");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            public void run() {
                Intent next = new Intent(getBaseContext(), Menu.class);
                startActivity(next);
                finish();
            }
        }, 2000);
    }
}
