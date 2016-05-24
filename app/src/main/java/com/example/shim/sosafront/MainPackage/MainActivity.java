package com.example.shim.sosafront.MainPackage;


/* super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_main);*/


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.shim.sosafront.CameraPackage.CameraActivity;
import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.GalleryPackage.GalleryActivity;
import com.example.shim.sosafront.HistoryPackage.HistoryActivity;
import com.example.shim.sosafront.R;
import com.example.shim.sosafront.StatisticPackage.StatisticActivity;
import com.example.shim.sosafront.TutorialPackage.TutorialActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    Button takePictureBtn;
    ImageButton galleryBtn;
    ImageButton historyBtn;
    ImageButton statisticsBtn;

    String name;
    String tutorial;

    String authKey;
    DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       /* this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.subTextColor));

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

        takePictureBtn = (Button) findViewById(R.id.takePictureBtn);
        galleryBtn = (ImageButton) findViewById(R.id.galleryBtn);
        historyBtn = (ImageButton) findViewById(R.id.historyBtn);
        statisticsBtn = (ImageButton) findViewById(R.id.statisticsBtn);

        takePictureBtn.setOnClickListener(menuClick);
        galleryBtn.setOnClickListener(menuClick);
        historyBtn.setOnClickListener(menuClick);
        statisticsBtn.setOnClickListener(menuClick);

        new AsyncMain().execute();

    }

    private class AsyncMain extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://113.198.84.37/api/v1/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", " Token " + authKey);

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);

                // Append parameters to URLㅁ

                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                //여기서 로그인 페이지로 이동
                int response_code = conn.getResponseCode();

                Log.d("FindPawdActivityLog", "FindPawdActivityLog 2-0 " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("MainLog", "MainLog 4-2 " + result.toString());
                    Log.d("MainLog", "MainLog 4-3 " + reader);

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("MainLog", "MainLog 4-4 " + result);

                    while ((line = reader.readLine()) != null) {
                        result.append(line);    //서버에서 온 데이터를 계속 붙임
                    }


                    String serverJsonValue = result.toString();
                    Log.d("MainLog", "MainLog 2-0 : " + serverJsonValue);
                    JSONObject serverJsonObject = new JSONObject(serverJsonValue);
                    Log.d("MainLog", "MainLog 2-1 : " + serverJsonObject);
                    JSONObject subJsonObject = new JSONObject(String.valueOf(serverJsonObject.get("value")));
                    Log.d("MainLog", "MainLog 2-2 : " + result.toString());

                    name = subJsonObject.getString("name").toString();
                    tutorial = subJsonObject.getString("tutorial").toString();

                    Log.d("MainLog", "MainLog 2-2 : " + name);
                    Log.d("MainLog", "MainLog 2-2 : " + tutorial);

                    // Pass data to onPostExecute method
                        /*return(result.toString());*/
                    return("successful");

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } catch (JSONException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }


        @Override
        protected void onPostExecute(String result) {

            Log.d("MainLog", "MainLog 3-0 : " + result);
            //this method will be running on UI thread

            pdLoading.dismiss();



            if (result.equals("successful")) {
                if(tutorial.equals("true")) {
                    Log.d("MainLog", "MainLog 3-2 : " + "들어와지나?");
                    Intent intentToTutorial = new Intent(MainActivity.this, TutorialActivity.class);
                    intentToTutorial.putExtra("TrueFalse", true);
                    startActivity(intentToTutorial);
                }

            } else {
                Toast.makeText(getApplicationContext(), "사용자 인증 실패", Toast.LENGTH_SHORT).show();
            }
        }

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


