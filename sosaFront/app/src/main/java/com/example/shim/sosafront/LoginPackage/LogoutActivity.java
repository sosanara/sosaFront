package com.example.shim.sosafront.LoginPackage;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import com.example.shim.sosafront.R;

public class LogoutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);


        Intent moveLoinIntet = new Intent(LogoutActivity.this, LoginActivity.class);
        startActivity(moveLoinIntet);
        LogoutActivity.this.finish();
    }

}
