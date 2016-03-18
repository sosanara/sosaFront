package com.example.shim.sosafront.LoginPackage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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



public class SignUpActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText etEmail;
    private EditText etPassword;

    private EditText username_view;
    private EditText email_view;
    private EditText pswd_view;
    private EditText pswd2_view;
    private EditText age_view;
    private EditText firstName_view;
    private EditText lastName_view;
    private EditText gender_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Get Reference to variables
        username_view = (EditText) findViewById(R.id.username_view);
        email_view = (EditText) findViewById(R.id.email_view);
        pswd_view = (EditText) findViewById(R.id.pswd_view);
        pswd2_view = (EditText) findViewById(R.id.pswd2_view);
        age_view = (EditText) findViewById(R.id.age_view);
        firstName_view = (EditText) findViewById(R.id.firstName_view);
        lastName_view = (EditText) findViewById(R.id.lastName_view);
        gender_view= (EditText) findViewById(R.id.gender_view);

        username_view.setText("minhoShimm", TextView.BufferType.EDITABLE);
        email_view.setText("shim5369@naver.com", TextView.BufferType.EDITABLE);
        pswd_view.setText("test1234", TextView.BufferType.EDITABLE);
        pswd2_view.setText("test1234", TextView.BufferType.EDITABLE);

        age_view.setText("25", TextView.BufferType.EDITABLE);
        firstName_view.setText("minho", TextView.BufferType.EDITABLE);
        lastName_view.setText("shim", TextView.BufferType.EDITABLE);
        gender_view.setText("Man", TextView.BufferType.EDITABLE);


    }

    // Triggers when LOGIN Button clicked
    public void checkLogin(View arg0) {

        final String username = username_view.getText().toString();
        final String email = email_view.getText().toString();
        final String password1 = pswd_view.getText().toString();
        final String password2 = pswd2_view.getText().toString();

        final String age = age_view.getText().toString();
        final String first_name = firstName_view.getText().toString();
        final String last_name = lastName_view.getText().toString();
        final String gender = gender_view.getText().toString();




        // Initialize  AsyncLogin() class with email and password
        //*new AsyncLogin().execute(email,password);*/
        new AsyncLogin().execute(username,email,password1,password2, age
                ,first_name, last_name, gender);

    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(SignUpActivity.this);
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

                url = new URL("http://192.168.0.2:8003/rest-auth/registration/");

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
                        .appendQueryParameter("age", params[4])
                        .appendQueryParameter("first_name", params[5])
                        .appendQueryParameter("last_name", params[6])
                        .appendQueryParameter("gender", params[7]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Log.d("tmdlsk", "테스트1" + query);
                Log.d("tmdlsk", "테스트2" + writer);
                Log.d("tmdlsk", "테스트3" + os);
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

                Log.d("receiveServer", "받는거0-0: " + conn.getResponseCode());
                Log.d("receiveServer", "받는거0-1: " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_CREATED) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("receiveServer", "받는거1: " + result.toString());
                    Log.d("receiveServer", "받는거3: " + reader);

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

            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
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


