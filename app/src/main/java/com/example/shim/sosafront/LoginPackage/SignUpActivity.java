package com.example.shim.sosafront.LoginPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


public class SignUpActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private Button loginTest;

    private EditText signUpUserNameView;
    private EditText signUpEmailView;
    private EditText signUpPawd1View;
    private EditText signUpPawd2View;
    private EditText signUpAgeView;
    private EditText signUpNameView;
    private EditText signUpGenderView;

    private String errorUsername;
    private String errorEmail;
    private String errorPassword1;
    private String errorPassword2;
    private String errorAge;
    private String errorName;
    private String errorGender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Get Reference to variables
        signUpUserNameView = (EditText) findViewById(R.id.signUpUserNameView);
        signUpEmailView = (EditText) findViewById(R.id.signUpEmailView);
        signUpPawd1View = (EditText) findViewById(R.id.signUpPawd1View);
        signUpPawd2View = (EditText) findViewById(R.id.signUpPawd2View);
        signUpAgeView = (EditText) findViewById(R.id.signUpAgeView);
        signUpNameView = (EditText) findViewById(R.id.signUpNameView);
        signUpGenderView= (EditText) findViewById(R.id.signUpGenderView);

        loginTest = (Button) findViewById(R.id.loginTest);

        signUpUserNameView.setText("", TextView.BufferType.EDITABLE);
        signUpEmailView.setText("qwer1234@naver.com", TextView.BufferType.EDITABLE);
        signUpPawd1View.setText("qwer1234", TextView.BufferType.EDITABLE);
        signUpPawd2View.setText("qwer1234", TextView.BufferType.EDITABLE);

        signUpAgeView.setText("1992", TextView.BufferType.EDITABLE);
        signUpNameView.setText("심민호", TextView.BufferType.EDITABLE);
        signUpGenderView.setText("Man", TextView.BufferType.EDITABLE);

        loginTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    // Triggers when LOGIN Button clicked
    public void checkLogin(View arg0) {

        final String username = signUpUserNameView.getText().toString();
        final String email = signUpEmailView.getText().toString();
        final String password1 = signUpPawd1View.getText().toString();
        final String password2 = signUpPawd2View.getText().toString();

        final String age = signUpAgeView.getText().toString();
        final String name = signUpNameView.getText().toString();
        final String gender = signUpGenderView.getText().toString();

        String first_name = "";
        String last_name = "";
        //이름 성과 이름으로 나눔

        if(name.length() >= 1) {
            first_name = name.substring(0, 1);
            last_name = name.substring(1, name.length());
        }

        new AsyncSignUp().execute(username, email, password1, password2, age
                , first_name, last_name, gender);

    }

    private class AsyncSignUp extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(SignUpActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {  //Background 작업 시작전에 UI 작업을 진행
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {  //Background 작업을 진행
            try {

                // Enter URL address where your php file resides

                url = new URL("http://113.198.84.37/rest-auth/registration/");


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

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("password1", params[2])
                        .appendQueryParameter("password2", params[3])
                        .appendQueryParameter("birth", params[4])
                        .appendQueryParameter("first_name", params[5])
                        .appendQueryParameter("last_name", params[6])
                        .appendQueryParameter("gender", params[7]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                Log.d("SignUpActivityLog", "SignUpActivityLog 0-1 : " + query);

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

                int response_code = conn.getResponseCode();

                Log.d("SignUpActivityLog", "SignUpActivityLog 1-0 : " + conn.getResponseCode());

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_CREATED) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("SignUpActivityLog", "SignUpActivityLog 1-1 : " + result.toString());

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("SignUpActivityLog", "SignUpActivityLog 1-2 : " + result.toString());
                    Log.d("SignUpActivityLog", "SignUpActivityLog 1-3 : " + reader);
                    // Pass data to onPostExecute method
                    // Read data sent from server

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

                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-2 : " + errorResult.toString());

                    if(serverJsonValue.contains("username"))
                        errorUsername = serverJsonObject.getString("username");

                    if(serverJsonValue.contains("email"))
                        errorEmail = serverJsonObject.getString("email");

                    if(serverJsonValue.contains("password1"))
                        errorPassword1 = serverJsonObject.getString("password1");

                    if(serverJsonValue.contains("password2"))
                        errorPassword2 = serverJsonObject.getString("password2");

                    if(serverJsonValue.contains("age"))
                        errorAge = serverJsonObject.getString("age");

                    if(serverJsonValue.contains("first_name"))
                        errorName = serverJsonObject.getString("first_name");

                    if(serverJsonValue.contains("last_name"))
                        errorName = serverJsonObject.getString("last_name");

                    if(serverJsonValue.contains("gender"))
                        errorGender = serverJsonObject.getString("gender");

                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-3 : " + errorUsername);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-4 : " + errorEmail);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-5 : " + errorPassword1);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-6 : " + errorPassword2);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-7 : " + errorAge);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-8 : " + errorName);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-9 : " + errorGender);

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
        protected void onPostExecute(String result) {  //Background 작업이 끝난 후 UI 작업을 진행

            Log.d("SignUpActivityLog", "SignUpActivityLog 3-0 : " + result);
            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result.equals("successful")) {
                Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                SignUpActivity.this.finish();
            }

            else {
                Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
