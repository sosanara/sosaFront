package com.example.shim.sosafront.LoginPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shim.sosafront.MainPackage.MainActivity;
import com.example.shim.sosafront.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private EditText loginUserNameView;
    private EditText loginEmailView;
    private EditText loginPawdView;

    private Button moveFindPass;
    private Button moveSignUp;

    private String cookies;

    private String loginResponse;
    InputStream           loginIs   = null;
    ByteArrayOutputStream loginBaos = null;
    String testAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get Reference to variables
        loginUserNameView = (EditText) findViewById(R.id.loginUserNameView);
        loginEmailView = (EditText) findViewById(R.id.loginEmailView);
        loginPawdView = (EditText) findViewById(R.id.loginPawdView);

        moveFindPass = (Button) findViewById(R.id.moveFindPass);
        moveSignUp = (Button) findViewById(R.id.moveSignUp);

        loginUserNameView.setText("minhoshim", TextView.BufferType.EDITABLE);
        loginEmailView.setText("shim5365@naver.com", TextView.BufferType.EDITABLE);
        loginPawdView.setText("qwer1234", TextView.BufferType.EDITABLE);


        moveFindPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveFindPass = new Intent(LoginActivity.this, ChangePawdActivity.class);
                startActivity(moveFindPass);
            }
        });

        moveSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(moveSignUp);
            }
        });

    }

    // Triggers when LOGIN Button clicked
    public void startLogin(View arg0) {

        final String username = loginUserNameView.getText().toString();
        final String email = loginEmailView.getText().toString();
        final String password = loginPawdView.getText().toString();


        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(username, email, password);

    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
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
                url = new URL("http://192.168.0.2:8000/rest-auth/login/");
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
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/json");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("password", params[2]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Log.d("loginTest", "로그인 테스트1 " + query);
                Log.d("loginTest", "로그인 테스트2-0 " + writer);
                Log.d("loginTest", "로그인 테스트2-1 " + os);
                Log.d("loginTest", "로그인 테스트2-2 " + cookies);
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                //여기서 로그인 페이지로 이동
                int response_code = conn.getResponseCode();

                Log.d("loginTest", "로그인 받는거0-0: " + conn.getResponseCode());
                Log.d("loginTest", "로그인 받는거0-1: " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("loginTest", "로그인 받는거 1: " + result.toString());
                    Log.d("loginTest", "로그인 받는거 1-1: " + reader);

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("loginTest", "로그인 받는거 2: " + result.toString());  // result.toString()
                    Log.d("loginTest", "로그인 받는거 2-1: " + reader);    //java.io.BufferedReader@5015f88

                    /*loginIs = conn.getInputStream();
                    loginBaos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLength = 0;
                    while((nLength = loginIs.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        loginBaos.write(byteBuffer, 0, nLength);
                    }
                    byteData = loginBaos.toByteArray();

                    loginResponse = new String(byteData);

                    JSONObject responseJSON = null;
                    try {
                        responseJSON = new JSONObject(loginResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        testAge = (String) responseJSON.get("key");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/

                    Log.d("loginTest", "로그인 받는거 3: " + loginResponse);
                    /*Log.d("loginTest", "로그인 받는거 3: " + responseJSON);*/
                    Log.d("loginTest", "로그인 받는거 3: " + testAge);
                    /*  Boolean result = (Boolean) responseJSON.get("result");
                        String age = (String) responseJSON.get("age");
                        String job = (String) responseJSON.get("job");
                         
                        Log.i(TAG, "DATA response = " + loginResopnse);*/


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
/*
            JSONObject responseJSON = null;
            try {
                responseJSON = new JSONObject(loginResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                testAge = (String) responseJSON.get("key");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d("loginTest", "로그인 받는거 3: " + loginResponse);
            Log.d("loginTest", "로그인 받는거 3: " + responseJSON);
            Log.d("loginTest", "로그인 받는거 3: " + testAge);*/

            //여기에 Json 받는거
            //this method will be running on UI thread



            //여기 처리 생각해야함
            Log.d("loginTest", "로그인 테스트4-1 : " +  result);
            Log.d("loginTest", "로그인 테스트4-2 : " +  result);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();

            pdLoading.dismiss();
            Log.d("loginTest", "로그인 테스트5 : 여기 들어와지나?" ); //여기까진 들어옴
            if(result.equalsIgnoreCase("true"))  //equals랑 같음(대소문자까지)
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Log.d("loginTest", "로그인 테스트5-1 : 화면 이동" );
                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_LONG);
                //다른 화면이동
                Intent moveMainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(moveMainIntent);
                LoginActivity.this.finish();

            }else if (result.equalsIgnoreCase("false")){
                Log.d("loginTest", "로그인 테스트5-2 : 화면 이동 실패1" );
                // If username and password does not match display a error message
                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_LONG);

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Log.d("loginTest", "로그인 테스트5-3 : 화면 이동 실패2" );
                Toast.makeText(LoginActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG);

            }
        }

    }
}


