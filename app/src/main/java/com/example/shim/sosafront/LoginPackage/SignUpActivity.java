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

    String testSignUp;


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

        //이름 성과 이름으로 나눔
        final String first_name = name.substring(0, 1);
        final String last_name = name.substring(1, name.length());

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
                Log.e("SignUpActivityLog", "SignUpActivityLog 1- 4" + e.getLocalizedMessage());
                e.printStackTrace();
                return "exception";
            } catch (IOException e) {
                Log.e("SignUpActivityLog", "SignUpActivityLog 1-  5" + e.getLocalizedMessage());
                e.printStackTrace();
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
                Log.d("SignUpActivityLog", "SignUpActivityLog 0-2 : " + writer);
                Log.d("SignUpActivityLog", "SignUpActivityLog 0-3 : " + os);


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

                InputStream inputStream = conn.getErrorStream();
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result2 = new StringBuilder();
                String line2;
                Log.d("SignUpActivityLog", "SignUpActivityLog 1 : " + result2.toString());

                while ((line2 = reader2.readLine()) != null) {
                    result2.append(line2);
                }

                Log.d("SignUpActivityLog", "SignUpActivityLog 1-1 : " + result2.toString());
                Log.d("SignUpActivityLog", "SignUpActivityLog 1-2 : " + reader2);
                // Pass data to onPostExecute method
                // Read data sent from server

                int response_code = conn.getResponseCode();

                Log.d("SignUpActivityLog", "SignUpActivityLog 0-5 : " + conn.getResponseCode());

                // Check if successful connection made


                if (response_code == HttpURLConnection.HTTP_CREATED) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("SignUpActivityLog", "SignUpActivityLog 1 : " + result.toString());

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("SignUpActivityLog", "SignUpActivityLog 1-1 : " + result.toString());
                    Log.d("SignUpActivityLog", "SignUpActivityLog 1-2 : " + reader);
                    // Pass data to onPostExecute method
                    // Read data sent from server


                    return(result.toString());

                }else{

                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2 : " + result.toString());


                   Log.d("SignUpActivityLog", "SignUpActivityLog 2-1 : " + result.toString());

                    // Pass data to onPostExecute method
                    // Read data sent from server


                    return(result.toString());


               /*     return("unsuccessful");*/
                }

            } catch (IOException e) {
                Log.e("SignUpActivityLog", "SignUpActivityLog 2-5" + e.getLocalizedMessage());
                        e.printStackTrace();
                return "exception";
            } finally {

                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {  //Background 작업이 끝난 후 UI 작업을 진행

            Log.d("SignUpActivityLog", "SignUpActivityLog 2-0 : " + result);
            //this method will be running on UI thread

            pdLoading.dismiss();


            //회원가입 성공하면 Key값 Json형식으로 받으면 로그인 페이지 이동
            if(result.contains("key")) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                SignUpActivity.this.finish();
            }

            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                SignUpActivity.this.finish();

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(SignUpActivity.this, "Invalid email or password", Toast.LENGTH_LONG);

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(SignUpActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG);

            }
        }

    }
}
