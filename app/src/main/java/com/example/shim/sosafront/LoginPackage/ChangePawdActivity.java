package com.example.shim.sosafront.LoginPackage;

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


public class ChangePawdActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private EditText oldPawdView;
    private EditText changePawd1View;
    private EditText changePawd2View;

    private String authKey;
    private DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pawd);

        // Get Reference to variables
        oldPawdView = (EditText) findViewById(R.id.oldPawdView);
        changePawd1View = (EditText) findViewById(R.id.changePawd1View);
        changePawd2View = (EditText) findViewById(R.id.changePawd2View);

        oldPawdView.setText("test12345", TextView.BufferType.EDITABLE);
        changePawd1View.setText("qwer1234", TextView.BufferType.EDITABLE);
        changePawd2View.setText("qwer1234", TextView.BufferType.EDITABLE);

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");


    }

    // Triggers when changePawd Button clicked
    public void changePawd(View arg0) {

        final String old_password = oldPawdView.getText().toString();
        final String new_password1 = changePawd1View.getText().toString();
        final String new_password2 = changePawd2View.getText().toString();

        // Initialize  AsyncLogin() class with email and password
        new AsyncChangePawd().execute(old_password, new_password1, new_password2);

    }

    private class AsyncChangePawd extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ChangePawdActivity.this);
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
                url = new URL("http://113.198.84.37/rest-auth/password/change/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }

            try {

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", " Token " + authKey);

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("old_password", params[0])
                        .appendQueryParameter("new_password1", params[1])
                        .appendQueryParameter("new_password2", params[2]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Log.d("ChangePawdActivityLog", "ChangePawdActivityLog 1-0" + query);
                Log.d("ChangePawdActivityLog", "ChangePawdActivityLog 1-1" + writer);
                Log.d("ChangePawdActivityLog", "ChangePawdActivityLog 1-2" + os);
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

                Log.d("ChangePawdActivityLog", "ChangePawdActivityLog 0-0: " + conn.getResponseCode());
                Log.d("ChangePawdActivityLog", "ChangePawdActivityLog 0-1: " + conn.getResponseCode());

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

                    Log.d("ChangePawdActivityLog", "ChangePawdActivityLog1-1: " + result.toString());


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
