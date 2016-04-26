package com.example.shim.sosafront.UserInfoPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shim.sosafront.DatabasePackage.DataStore;
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



public class ChangeUserInfoActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private EditText emailAddressView;
    private EditText nameView;
    private EditText birthView;
    private EditText GenderView;


    private String authKey;

    DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);

        // Get Reference to variables
        emailAddressView = (EditText) findViewById(R.id.emailAddressView);
        nameView = (EditText) findViewById(R.id.nameView);
        birthView = (EditText) findViewById(R.id.birthView);
        GenderView = (EditText) findViewById(R.id.GenderView);

        emailAddressView.setText("uiop@naver.com", TextView.BufferType.EDITABLE);
        nameView.setText("mienhwr", TextView.BufferType.EDITABLE);

        birthView.setText("2000", TextView.BufferType.EDITABLE);
        GenderView.setText("Man", TextView.BufferType.EDITABLE);


        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");
    }

    // Triggers when changePawd Button clicked
    public void changeUserInfoBtn(View arg0) {

        final String email = emailAddressView.getText().toString();
        final String name = nameView.getText().toString();
        final String birth = birthView.getText().toString();
        final String gender = GenderView.getText().toString();

        final String first_name ="민";
        final String last_name = "호";



        // Initialize  AsyncLogin() class with email and password
        new AsyncChangeUserInfo().execute(email, first_name, last_name, birth, gender);

    }

    private class AsyncChangeUserInfo extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ChangeUserInfoActivity.this);
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
                url = new URL("http://113.198.84.37/rest-auth/user/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }

            try {

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Authorization", " Token " + authKey);

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", params[0])
                        .appendQueryParameter("first_name", params[1])
                        .appendQueryParameter("last_name", params[2])
                        .appendQueryParameter("birth", params[3])
                        .appendQueryParameter("gender", params[4]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-0" + query);
                Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-1" + writer);
                Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-2" + os);
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

                Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-3: " + conn.getResponseCode());
                Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-4: " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-5: " + result.toString());
                    Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-6: " + reader);

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-7: " + result.toString());
                    Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-8: " + reader);

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

                /*Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                SignUpActivity.this.finish();*/

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
               /* Toast.makeText(SignUpActivity.this, "Invalid email or password", Toast.LENGTH_LONG);*/

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                /*Toast.makeText(SignUpActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG);*/

            }
        }

    }
}

