package com.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.frpc.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Set;



/**
 * Created by lyf on 2016/6/22.
 */
public class SplashActivity extends Activity {
    private static final String TAG = "SpalshActivity";
    Handler mHandler = new Handler();
    private Intent websocketServiceIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);

    }
}
