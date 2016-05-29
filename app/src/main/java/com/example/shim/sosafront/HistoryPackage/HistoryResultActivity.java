package com.example.shim.sosafront.HistoryPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.MainPackage.MainActivity;
import com.example.shim.sosafront.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HistoryResultActivity extends AppCompatActivity {

    private LinearLayout networkCheckLayout;
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private ImageView firstImageView;
    private ImageView beforeImageView;
    private ImageView currentImageView;

    private TextView firstPercentView;
    private TextView beforePercentView;
    private TextView currentPercentView;
    private TextView firstCreateDateView;
    private TextView beforeCreateDateView;
    private TextView currentCreateDateView;
    private TextView onlyOneDataView;

    private LinearLayout compareCreateDateLayout;
    private LinearLayout comparePercentLayout;

    private String getIndex;
    private Bitmap testBitmap;
    private String authKey;
    private DataStore dataStore;

    private String firstImagePath;
    private String beforeImagePath;
    private String currentImagePath;
    private String currentPercent;
    private String firstPercent;
    private String beforePercent;
    private String currentCreateDate;
    private String firstCreateDate;
    private String beforeCreateDate;
    private String resultType;
    private String userName;

    private int firstTakePicture = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_result);

        //글씨체 통일
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        networkCheckLayout = (LinearLayout)findViewById(R.id.networkCheckLayout);
        firstPercentView = (TextView) findViewById(R.id.firstPercentView);
        beforePercentView = (TextView) findViewById(R.id.beforePercentView);
        currentPercentView = (TextView) findViewById(R.id.currentPercentView);
        firstCreateDateView = (TextView) findViewById(R.id.firstCreateDateView);
        beforeCreateDateView = (TextView) findViewById(R.id.beforeCreateDateView);
        currentCreateDateView = (TextView) findViewById(R.id.currentCreateDateView);
        onlyOneDataView = (TextView) findViewById(R.id.onlyOneDataView);

        compareCreateDateLayout = (LinearLayout) findViewById(R.id.compareCreateDateLayout);
        comparePercentLayout = (LinearLayout) findViewById(R.id.comparePercentLayout);

        getIndex = getIntent().getExtras().getString("historyIndex");

        Log.d("HistoryResultLog", "HistoryResultLog 0-0-0: " + getIndex);

        new AsyncHistoryResult().execute();

        /*downloadTask.execute("http://113.198.84.37/" + resultImagePath);*/

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        Drawable home = getResources().getDrawable(R.drawable.toolbar_home);
        Drawable resizeHome = resize(home);
        toolbar.setNavigationIcon(resizeHome);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void networkCheck(View v) {

        switch (v.getId()) {
            case R.id.networkCheckBtn:
                finish();
                startActivity(getIntent());
                break;
        }
    }


    private class AsyncHistoryResult extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(HistoryResultActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {  //작업처리중' 프로그레스 다이얼로그 자동 시작
            super.onPreExecute();

            ConnectivityManager manager =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mobile.isConnected() || wifi.isConnected()){
                /*Toast.makeText(getApplicationContext(), "연결성공", Toast.LENGTH_LONG).show();*/
            } else {
                /*Toast.makeText(getApplicationContext(), "연결실패", Toast.LENGTH_LONG).show();*/
                networkCheckLayout.setVisibility(View.VISIBLE);
            }

            firstImageView = (ImageView) findViewById(R.id.firstImageView);
            beforeImageView = (ImageView) findViewById(R.id.beforeImageView);
            currentImageView = (ImageView) findViewById(R.id.currentImageView);

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }


        /*protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(String.valueOf(values));
            Log.i("makemachine", "onProgressUpdate(): " + String.valueOf(values[0]));

            firstPercentView.setText("");
            beforePercentView.setText("");
            currentPercentView.setText("");
            firstCreateDateView.setText("");
            beforeCreateDateView.setText("");
            currentCreateDateView.setText("");
            onlyOneDataView.setText("");

        }*/

        @Override
        protected String doInBackground(String... params) {  //프로그레스 다이얼로그 자동 종료 및 에러메시지 토스트보여줌
            //doInBackground()에서 에러발생시 하위 클래스의 onPostExecute()는 실행되지 않음
            try {

                // Enter URL address where your php file resides

                Log.d("HistoryResult", "HistoryResult 0-0-1: " + getIndex);
                url = new URL("http://113.198.84.37/api/v1/userInfo/history/" + getIndex);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception fail!";
            }

            try {


                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", " Token " + authKey);

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);

                // Append parameters to URL

                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                Log.d("HistoryResultLog", "HistoryResultLog 0-0: " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("HistoryResultLog", "HistoryResultLog 2: " + result.toString());  // result.toString()
                    Log.d("HistoryResultLog", "HistoryResultLog 2-1: " + reader);    //java.io.BufferedReader@5015f88

                    String value = result.toString();

                    /*JSONObject serverJsonObject = new JSONObject(value);*/
                    JSONObject serverJsonObject = new JSONObject(value);

                    /*String test = jsonObject.getString("value").toString();*/

                    JSONObject subJsonObject = new JSONObject(String.valueOf(serverJsonObject.get("value")));
                    JSONObject currentJsonObject = new JSONObject(String.valueOf(subJsonObject.get("current")));

                    if (String.valueOf(serverJsonObject.get("value")).contains("first") || String.valueOf(serverJsonObject.get("value")).contains("before")) {
                        JSONObject firstJsonObject = new JSONObject(String.valueOf(subJsonObject.get("first")));
                        JSONObject beforeJsonObject = new JSONObject(String.valueOf(subJsonObject.get("before")));

                        Log.d("HistoryResultLog", "HistoryResultLog 2-1: " + String.valueOf(serverJsonObject.get("value")));
                        Log.d("HistoryResultLog", "HistoryResultLog 2-2: " + subJsonObject);
                        Log.d("HistoryResultLog", "HistoryResultLog 2-3: " + currentJsonObject);
                        Log.d("HistoryResultLog", "HistoryResultLog 2-4: " + firstJsonObject);
                        Log.d("HistoryResultLog", "HistoryResultLog 2-5: " + beforeJsonObject);

                        Log.d("HistoryResultLog", "HistoryResultLog 2-6-1: " + currentJsonObject.getString("origin_image").toString());
                        Log.d("HistoryResultLog", "HistoryResultLog 2-6-2: " + currentJsonObject.getString("percentage").toString());
                        Log.d("HistoryResultLog", "HistoryResultLog 2-6-3: " + currentJsonObject.getString("created_date").toString());

                        Log.d("HistoryResultLog", "HistoryResultLog 2-7-1: " + firstJsonObject.getString("origin_image").toString());
                        Log.d("HistoryResultLog", "HistoryResultLog 2-7-2: " + firstJsonObject.getString("percentage").toString());
                        Log.d("HistoryResultLog", "HistoryResultLog 2-7-3: " + firstJsonObject.getString("created_date").toString());

                        Log.d("HistoryResultLog", "HistoryResultLog 2-8-1: " + beforeJsonObject.getString("origin_image").toString());
                        Log.d("HistoryResultLog", "HifirstImageViewstoryResultLog 2-8-2: " + beforeJsonObject.getString("percentage").toString());
                        Log.d("HistoryResultLog", "HistoryResultLog 2-8-3: " + beforeJsonObject.getString("created_date").toString());

                        firstImagePath = firstJsonObject.getString("origin_image").toString();
                        firstPercent = firstJsonObject.getString("percentage").toString();
                        firstCreateDate =  firstJsonObject.getString("created_date").toString();
                        beforeImagePath = beforeJsonObject.getString("origin_image").toString();
                        beforePercent = beforeJsonObject.getString("percentage").toString();
                        beforeCreateDate = beforeJsonObject.getString("created_date").toString();

                        displayImageView(firstImagePath, 1);
                        displayImageView(beforeImagePath, 2);
                        firstTakePicture = 1;
                    }

                    currentImagePath = currentJsonObject.getString("origin_image").toString();
                    currentPercent = currentJsonObject.getString("percentage").toString();
                    currentCreateDate =currentJsonObject.getString("created_date").toString();
                    displayImageView(currentImagePath, 3);


                    if(firstPercent == null && beforePercent == null) {
                        currentCreateDate =  currentCreateDate.substring(0, 10);// ex) 2016-04-01
                        currentPercent = String.format("%.2f", Double.parseDouble(currentPercent)) + "%";

                    }

                    else {
                        firstCreateDate =  firstCreateDate.substring(0, 10);
                        beforeCreateDate =  firstCreateDate.substring(0, 10);
                        currentCreateDate =  firstCreateDate.substring(0, 10);

                        firstPercent = String.format("%.2f", Double.parseDouble(firstPercent)) + "%";
                        beforePercent = String.format("%.2f", Double.parseDouble(beforePercent)) + "%";
                        currentPercent = String.format("%.2f", Double.parseDouble(currentPercent)) + "%";

                        if(!firstPercent.contains("-"))
                            firstPercent = "+" + firstPercent;

                        if(!beforePercent.contains("-"))
                            beforePercent = "+" + beforePercent;

                    }
                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
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
            super.onPostExecute(result);
            Log.d("HistoryResultLog", "HistoryResultLog 3-1 : " + result);

            if(firstTakePicture == 0) {
                Drawable drawable = getResources().getDrawable(R.drawable.history_no_image);
                firstImageView.setImageDrawable(drawable);
                beforeImageView.setImageDrawable(drawable);

                onlyOneDataView.setVisibility(View.VISIBLE);
                comparePercentLayout.setVisibility(View.GONE);
                compareCreateDateLayout.setVisibility(View.GONE);
                /*firstPercentView.setText("");
                beforePercentView.setText("");
                firstCreateDateView.setText("");
                beforeCreateDateView.setText("");*/



               /* onlyOneDataView.setVisibility(View.VISIBLE);
                firstPercentView.setVisibility(View.GONE);
                beforePercentView.setVisibility(View.GONE);
                firstCreateDateView.setVisibility(View.GONE);
                beforeCreateDateView.setVisibility(View.GONE);*/
            }

            else {
                firstPercentView.setText(firstPercent);
                beforePercentView.setText(beforePercent);
                firstCreateDateView.setText(firstCreateDate);
                beforeCreateDateView.setText(beforeCreateDate);
            }
            currentPercentView.setText(currentPercent);
            currentCreateDateView.setText((currentCreateDate));

            pdLoading.dismiss();



        }

    }

    public void displayImageView(String ImagePath, int i) {
        try {


            //웹사이트에 접속 (사진이 있는 주소로 접근)
            URL Url = new URL("http://113.198.84.37/" + ImagePath);
            Log.d("HistoryResultLog","HistoryResultLog 0-0 : " + "http://113.198.84.37/" + ImagePath);
            // 웹사이트에 접속 설정
            URLConnection urlcon = Url.openConnection();
            // 연결하시오
            urlcon.connect();
            // 이미지 길이 불러옴
            int imagelength = urlcon.getContentLength();
            // 스트림 클래스를 이용하여 이미지를 불러옴
            BufferedInputStream bis = new BufferedInputStream(urlcon.getInputStream(), imagelength);
            // 스트림을 통하여 저장된 이미지를 이미지 객체에 넣어줌
            testBitmap = BitmapFactory.decodeStream(bis);

            /*if(ImagePath.contains("myResult"))
                resultImageView.setImageBitmap(testBitmap);

            else
                sendImageView.setImageBitmap(testBitmap);*/

            if(i==1)
                firstImageView.setImageBitmap(testBitmap);
            else if(i==2)
                beforeImageView.setImageBitmap(testBitmap);
            else
                currentImageView.setImageBitmap(testBitmap);

            bis.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 96, 77, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

}





