package com.example.shim.sosafront.GalleryPackage.GalleryNextPagePackage;

import android.app.Activity;
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
import com.example.shim.sosafront.GalleryPackage.GalleryActivity;
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

public class GalleryItemClickActivity extends AppCompatActivity {

    private LinearLayout networkCheckLayout;

    private static String IP_ADDRESS ;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;


    /*private static String type;
    private static String originImage;
    private static String binaryImage;
    private static String percentage;*/

    private static String created_date;
    private static String success;

    private TextView baldTypeView;
    private TextView baldProgressView;
    private TextView createDateView;
    private ImageView original_image;
    private ImageView binary_image;


    private String firstImagePath;
    private String changeImagePath;
    private String myType;
    private String percent;
    private String userName;
    private String createDate;

    Activity act = GalleryItemClickActivity.this;

    Bitmap testBitmap;

    String authKey;
    DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_item_click);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        //글씨체 통일
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        Drawable home = getResources().getDrawable(R.drawable.toolbar_home);
        Drawable resizeHome = resize(home);
        toolbar.setNavigationIcon(resizeHome);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GalleryItemClickActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



        /*baldTypeView = (TextView) findViewById(R.id.baldTypeView);
        baldProgressView = (TextView) findViewById(R.id.baldProgressView);
        createDateView = (TextView) findViewById(R.id.createDateView);*/

        networkCheckLayout = (LinearLayout)findViewById(R.id.networkCheckLayout);
        original_image = (ImageView) findViewById(R.id.original_image);
        binary_image = (ImageView) findViewById(R.id.binary_image);

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

        Intent intent = getIntent();
        IP_ADDRESS = intent.getStringExtra("url");

        new AsyncItemClick().execute();
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

    public void delete_image(View v) {

        new AsyncDeleteClick().execute();

    }


    //다시 갑니다~
    //Server Connect class
    private class AsyncItemClick extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(GalleryItemClickActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            baldTypeView = (TextView) findViewById(R.id.baldTypeView);
            baldProgressView = (TextView) findViewById(R.id.baldProgressView);
            createDateView = (TextView) findViewById(R.id.createDateView);
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

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                // Enter URL address where your php file resides
                Log.d("HistoryClickLog", "HistoryClickLog 0-0: " + IP_ADDRESS + "/");
                url = new URL(IP_ADDRESS+"/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("HistoryClickActivity", "HistoryClickActivity Connection Test : 접근실패");
                return "exception";
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                /*conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );*/
               /* conn.setRequestProperty("Authorization", " Token " + authKey);
*/
                /*conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");*/

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", " Token " + authKey);

                conn.setDoInput(true);
                /*conn.setDoOutput(true);*/
                /*conn.setDoOutput(true);
                conn.setRequestProperty(
                        "Content-Type", "application/x-www-form-urlencoded");*/
                /*conn.setRequestProperty("Authorization", " Token " + authKey);*/
                /*conn.setRequestMethod("DELETE");*/

                conn.connect();


            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                //Data를 받음
                int response_code = conn.getResponseCode();

                Log.d("HistoryClickLog", "HistoryClickLog 2-0 : " + conn.getResponseCode());
                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);    //서버에서 온 데이터를 계속 붙임
                    }

                    String value = result.toString();
                    Log.d("HistoryClickLog", "HistoryClickLog 2-0: " + value);
                    JSONObject serverJsonObject = new JSONObject(value);
                    JSONObject subJsonObject = new JSONObject(String.valueOf(serverJsonObject.get("value")));
                    Log.d("HistoryClickLog", "HistoryClickLog 2-1: " + subJsonObject);
                    firstImagePath = subJsonObject.getString("origin_image_name").toString();
                    changeImagePath = subJsonObject.getString("change_image_name").toString();
                    myType = subJsonObject.getString("type").toString();
                    percent = subJsonObject.getString("percentage").toString();
                    userName = subJsonObject.getString("user").toString();
                    createDate = subJsonObject.getString("created_date").toString();

                    Log.d("HistoryClickLog", "HistoryClickLog 2-1: " + firstImagePath);
                    Log.d("HistoryClickLog", "HistoryClickLog 2-2: " + changeImagePath);
                    Log.d("HistoryClickLog", "HistoryClickLog 2-3: " + myType);
                    Log.d("HistoryClickLog", "HistoryClickLog 2-4: " + percent);
                    Log.d("HistoryClickLog", "HistoryClickLog 2-5: " + userName);

                    if(myType.equals("0")) {
                        myType = "Normal";
                    } else  if(myType.equals("1")) {
                        myType = "Forward";
                    } else  if(myType.equals("2")) {
                        myType = "Backward";
                    } else  if(myType.equals("3")) {
                        myType = "Karma";
                    } else  if(myType.equals("4")) {
                        myType = "Bald";
                    }

                    String buf[] = createDate.split("-");
                    String temp = buf[0]+"."+buf[1]+"."+buf[2];
                    createDate = temp.substring(0, 10);



                    displayOriginalImageView(firstImagePath);
                    displayBinaryImageView(changeImagePath);


                    return (result.toString());

                } else {
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } catch (JSONException e) {
                return "exception";
            } finally {
                conn.disconnect();
            }
        }


        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();

            percent = String.format("%.2f", Double.parseDouble(percent)) + "%";

            createDateView.setText(createDate);
            baldTypeView.setText(myType);
            baldProgressView.setText(percent);

        }
    }

    //Server Connect class
    private class AsyncDeleteClick extends AsyncTask<String, String, String> {
        /*ProgressDialog pdLoading = new ProgressDialog(HistoryItemClickActivity.this);*/
        HttpURLConnection conn2;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Enter URL address where your php file resides
                url = new URL(IP_ADDRESS + "/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("HistoryItemClickLog", "HistoryItemClickLog Connection Test : 접근실패");
                return "exception";
            }


            try {

                conn2 = (HttpURLConnection) url.openConnection();
                conn2.setRequestProperty("Content-Type",
                        "application/json");
                conn2.setRequestProperty("Authorization", " Token " + authKey);
                conn2.setRequestMethod("DELETE");

                conn2.setDoInput(true);


                conn2.connect();


            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                //Data를 받음
                int response_code = conn2.getResponseCode();

                Log.d("HistoryItemClickLog", "HistoryItemClickLog 4-0: " + conn2.getResponseCode());
                // Check if successful connection made


                if (response_code == HttpURLConnection.HTTP_NO_CONTENT) { //204리퀘스트 받음
                    return ("successful");
                } else {
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }  finally {
                conn2.disconnect();
            }
        }



        @Override
        protected void onPostExecute(String result) {
            /*pdLoading.dismiss();*/

            Intent userInfoIntent = new Intent(GalleryItemClickActivity.this, GalleryActivity.class);
            startActivity(userInfoIntent);
            finish();
        }
    }
    public void displayOriginalImageView(String ImagePath) {
        try {

            //웹사이트에 접속 (사진이 있는 주소로 접근)
            URL Url = new URL("http://113.198.84.37/uploads/" + ImagePath);
            Log.d("SendImageActivity","SendImageActivity 0-0 : " + "http://113.198.84.37/uploads/" + ImagePath);
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


            original_image.setImageBitmap(testBitmap);

            bis.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void displayBinaryImageView(String ImagePath) {
        try {

            //웹사이트에 접속 (사진이 있는 주소로 접근)
            URL Url = new URL("http://113.198.84.37/uploads/" + ImagePath);
            Log.d("SendImageActivity","SendImageActivity 0-0 : " + "http://113.198.84.37/uploads/" + ImagePath);
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


            binary_image.setImageBitmap(testBitmap);

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
