package com.example.shim.sosafront.LoginPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.shim.sosafront.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LogoutActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
    }

    // Triggers when LOGIN Button clicked
    public void logOut(View arg0) {

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute();

    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(LogoutActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {  //작업처리중' 프로그레스 다이얼로그 자동 시작
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {  //프로그레스 다이얼로그 자동 종료 및 에러메시지 토스트보여줌
            //doInBackground()에서 에러발생시 하위 클래스의 onPostExecute()는 실행되지 않음
            try {

                // Enter URL address where your php file resides
                url = new URL("http://113.198.84.37/rest-auth/logout/");
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception fail!";
            }

            try {

                SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
                String authKey = prefs.getString("key", "");
                Log.d("tmdlsk", "비밀번호 수정 테스트0" + authKey);

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", " Token " + authKey);

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL

                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                //여기서 로그인 페이지로 이동
                int response_code = conn.getResponseCode();

                Log.d("logoutTest", "로그아웃 받는거0-0: " + conn.getResponseCode());
                Log.d("logoutTest", "로그아웃 받는거0-1: " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("logoutTest", "로그아웃 받는거 1: " + result.toString());
                    Log.d("logoutTest", "로그아웃 받는거 1-1: " + reader);

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("logoutTest", "로그아웃 받는거 2: " + result.toString());  // result.toString()
                    Log.d("logoutTest", "로그아웃 받는거 2-1: " + reader);    //java.io.BufferedReader@5015f88


                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            //여기 처리 생각해야함
            Log.d("loginTest", "로그아웃 테스트4-1 : " + result);
            Log.d("loginTest", "로그아웃 테스트4-2 : " + result);

            Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
            startActivity(intent);
            LogoutActivity.this.finish();

            Toast.makeText(LogoutActivity.this, "로그아웃 되었습니다", Toast.LENGTH_LONG);

            pdLoading.dismiss();
        }

    }
}



