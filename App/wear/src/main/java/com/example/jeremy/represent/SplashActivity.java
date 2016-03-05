package com.example.jeremy.represent;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d("Test", "splash");
    }

}
