package com.example.shim.sosafront.GalleryPackage.GalleryNextPagePackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.GalleryPackage.GalleryActivity;
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

public class GalleryItemClickActivity extends Activity {

    private static String IP_ADDRESS ;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;


    private static String type;
    private static String originImage;
    private static String binaryImage;
    private static String percentage;

    private static String created_date;
    private static String success;

    private TextView type_textview;
    private TextView percent_textview;
    private TextView created_date_textview;
    private ImageView original_image;
    private ImageView binary_image;

    Activity act = GalleryItemClickActivity.this;

    Bitmap testBitmap;

//    private RecyclerView mRecyclerView;
//    private HistoryAdapter mAdapter;

    public JSONObject json_object;

    String authKey;
    DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery_item_click);

        type_textview = (TextView) findViewById(R.id.item_click_type);
        percent_textview = (TextView) findViewById(R.id.item_click_percent);
        created_date_textview = (TextView) findViewById(R.id.item_click_created_data);

        original_image = (ImageView) findViewById(R.id.original_image);
        binary_image = (ImageView) findViewById(R.id.binary_image);



        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

        Intent intent = getIntent();
        IP_ADDRESS = intent.getStringExtra("url");

        new AsyncItemClick().execute();
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
                Log.d("HistoryClickLog", "HistoryClickLog 0-0: " + IP_ADDRESS + "/");
                url = new URL(IP_ADDRESS);

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

                Log.d("HistoryClickLog", "HistoryClickLog 2-0: " + conn.getResponseCode());
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
                    JSONObject jo = new JSONObject(value);
                    String test = jo.getString("value").toString();

                    json_object = new JSONObject(test);

                    type = json_object.getString("type");
                    created_date = json_object.getString("created_date");

                    if(type.equals("0")) {
                        type = "정상인형";
                    } else  if(type.equals("1")) {
                        type = "앞머리형";
                    } else  if(type.equals("2")) {
                        type = "뒷머리형";
                    } else  if(type.equals("3")) {
                        type = "가르마형";
                    } else  if(type.equals("4")) {
                        type = "비정상형";
                    }

                    String buf[] = created_date.split("-");
                    String temp = buf[0]+"."+buf[1]+"."+buf[2];
                    created_date = temp.substring(0,10);

                    originImage = json_object.getString("name");
                    binaryImage = json_object.getString("name");

                    displayOriginalImageView(originImage);
                    displayBinaryImageView(binaryImage);


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

            type_textview.setText(type);
            percent_textview.setText(type);
            created_date_textview.setText(created_date);
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
}
