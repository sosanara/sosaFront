package com.example.shim.sosafront.StartScreenPackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.shim.sosafront.LoginPackage.LoginActivity;
import com.example.shim.sosafront.R;

public class StartScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        Handler h = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                startActivity(new Intent(StartScreen.this, LoginActivity.class));
                finish();
            }
        };
        /*h.sendEmptyMessageDelayed(0, 1);*/
        h.sendEmptyMessageDelayed(0, 1500);
    }
}

