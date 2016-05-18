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
import com.example.shim.sosafront.GraphPackage.GraphActivity;
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
            switch(v.getId()) {  //switch가 if문보다 빠름 몇개 없는데 if문이 나려나?
                case R.id.takePictureBtn:
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);
                    break;

                case R.id.galleryBtn:
                    intent = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(intent);
                    break;

                case R.id.historyBtn:
                    intent = new Intent(MainActivity.this, GraphActivity.class);
                    startActivity(intent);
                    break;

                case R.id.statisticsBtn:
                    intent = new Intent(MainActivity.this, StatisticActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

}





/*
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent cameraIntent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(cameraIntent);

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent signUpIntent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);

        } else if (id == R.id.nav_slideshow) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);

        } else if (id == R.id.nav_manage) {
            Intent userInfoIntent = new Intent(MainActivity.this, UserInfoActivity.class);
            startActivity(userInfoIntent);

        } else if (id == R.id.nav_share) {
            Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(historyIntent);

        } else if (id == R.id.nav_send) {
            Intent graphIntent = new Intent(MainActivity.this, GraphActivity.class);
            startActivity(graphIntent);

        } else if (id == R.id.nav_statistic) {
            Intent graphIntent = new Intent(MainActivity.this, StatisticActivity.class);
            startActivity(graphIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
*/
