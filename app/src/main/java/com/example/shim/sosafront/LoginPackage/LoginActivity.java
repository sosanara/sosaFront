package com.example.shim.sosafront.LoginPackage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.MainPackage.MainActivity;
import com.example.shim.sosafront.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
    private EditText loginPawdView;

    private TextView signUp;
    private TextView findPawd;

    private Button moveFindPassBtn;
    private Button moveSignUpBtn;

    private String errorUsername;
    private String errorPassword;

    DataStore dataStore;
    String authKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.subTextColor));

        dataStore = new DataStore(this);


        // Get Reference to variables
        loginUserNameView = (EditText) findViewById(R.id.loginUserNameView);
        loginPawdView = (EditText) findViewById(R.id.loginPawdView);

        signUp = (TextView) findViewById(R.id.signUp);
        findPawd = (TextView) findViewById(R.id.findPawd);
        /*moveFindPassBtn = (Button) findViewById(R.id.moveFindPass);
        moveSignUpBtn = (Button) findViewById(R.id.moveSignUp);*/

        signUp.setText(Html.fromHtml("<u>회원가입</u>"), TextView.BufferType.SPANNABLE);
        findPawd.setText(Html.fromHtml("<u>비밀번호찾기</u>"), TextView.BufferType.SPANNABLE);

        loginUserNameView.setText("qwer1234", TextView.BufferType.EDITABLE);
        loginPawdView.setText("qwer1234", TextView.BufferType.EDITABLE);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(moveSignUp);

            }
        });

        findPawd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveFindPawd = new Intent(LoginActivity.this, FindPawdActivity.class);
                startActivity(moveFindPawd);

            }
        });
    }

    // Triggers when LOGIN Button clicked
    public void startLogin(View arg0) {

        final String username = loginUserNameView.getText().toString();
        final String password = loginPawdView.getText().toString();

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(username, password);

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
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {  //프로그레스 다이얼로그 자동 종료 및 에러메시지 토스트보여줌
                                                                //doInBackground()에서 에러발생시 하위 클래스의 onPostExecute()는 실행되지 않음
            try {
                // Enter URL address where your php file resides
                url = new URL("http://113.198.84.37/rest-auth/login/");
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

                //Request Header값 셋팅
                conn.setRequestProperty("Accept", "application/json");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true); // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션
                conn.setDoOutput(true); // InputStream으로 서버로 부터 응답을 받겠다는 옵션.

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream(); // Request Body에 Data를 담기위해 OutputStream 객체를 생성.
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));


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

                Log.d("LoginActivityLog", "LoginActivityLog 0-0 : " + conn.getResponseCode());


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

                    Log.d("LoginActivityLog", "LoginActivityLog 1 : " + result.toString());  // result.toString()

                    try {

                        String value = result.toString();
                        JSONObject testJson = new JSONObject(value);
                        authKey = (String) testJson.get("key");
                        dataStore.put("key", authKey);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return("successful");

                } else {
                    InputStream errorInputStream = conn.getErrorStream();
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorInputStream));
                    StringBuilder errorResult = new StringBuilder();
                    String failLine;

                    while ((failLine = errorReader.readLine()) != null) {
                        errorResult.append(failLine);
                    }

                    String serverJsonValue = errorResult.toString();
                    JSONObject serverJsonObject = new JSONObject(serverJsonValue);

                    Log.d("LoginActivityLog", "LoginActivityLog 2-2 : " + errorResult.toString());

                    if(serverJsonValue.contains("This field may not be blank."))
                        errorUsername = serverJsonObject.getString("password");

                    if(serverJsonValue.contains("non_field_errors"))
                        errorPassword = serverJsonObject.getString("non_field_errors");


                    Log.d("LoginActivityLog", "LoginActivityLog 2-3 : " + errorUsername);
                    Log.d("LoginActivityLog", "LoginActivityLog 2-3 : " + errorPassword);

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

            Log.d("LoginActivityLog", "LoginActivityLog 3-0 : " + result);
            pdLoading.dismiss();

            if(result.equals("successful"))  //equals랑 같음(대소문자까지)
            {
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_login), Toast.LENGTH_SHORT).show();

                Intent moveMainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(moveMainIntent);
                LoginActivity.this.finish();

            } else {
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.fail_login), Toast.LENGTH_SHORT).show();

            }

        }

    }
}


