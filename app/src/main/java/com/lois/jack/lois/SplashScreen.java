package com.lois.jack.lois;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        /** This splash screen is controversial. But the main thread is held to display image and let the app load in background **/
        startActivity(new Intent(SplashScreen.this, LandingActivity.class));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // close splash activity
        finish();
    }
}

