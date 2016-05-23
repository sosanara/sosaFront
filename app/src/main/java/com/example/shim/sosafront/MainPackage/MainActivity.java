package com.example.shim.sosafront.MainPackage;


/* super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_main);*/


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.shim.sosafront.CameraPackage.CameraActivity;
import com.example.shim.sosafront.GalleryPackage.GalleryActivity;
import com.example.shim.sosafront.HistoryPackage.HistoryActivity;
import com.example.shim.sosafront.R;
import com.example.shim.sosafront.StatisticPackage.StatisticActivity;

public class MainActivity extends AppCompatActivity {

    Button takePictureBtn;
    ImageButton galleryBtn;
    ImageButton historyBtn;
    ImageButton statisticsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);*/
        /*getSupportActionBar().setDisplayShowTitleEnabled(false);*/




        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        takePictureBtn = (Button) findViewById(R.id.takePictureBtn);
        galleryBtn = (ImageButton) findViewById(R.id.galleryBtn);
        historyBtn = (ImageButton) findViewById(R.id.historyBtn);
        statisticsBtn = (ImageButton) findViewById(R.id.statisticsBtn);

        takePictureBtn.setOnClickListener(menuClick);
        galleryBtn.setOnClickListener(menuClick);
        historyBtn.setOnClickListener(menuClick);
        statisticsBtn.setOnClickListener(menuClick);
    }

    private View.OnClickListener menuClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent;

            switch(v.getId()) {
                case R.id.takePictureBtn:
                    intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.galleryBtn:
                    intent = new Intent(MainActivity.this, GalleryActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.historyBtn:
                    intent = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.statisticsBtn:
                    intent = new Intent(MainActivity.this, StatisticActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

}


