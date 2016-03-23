package com.example.shim.sosafront.LoginPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.shim.sosafront.R;


public class ChangePawdActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private Button changePawdBtn;

    private EditText changePawd1View;
    private EditText changePawd2View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pawd);

        // Get Reference to variables
        changePawd1View = (EditText) findViewById(R.id.changePawd1View);
        changePawd2View = (EditText) findViewById(R.id.changePawd2View);

        changePawdBtn = (Button) findViewById(R.id.changePawdBtn);



        /*changePawdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindPawdActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });*/

    }

    // Triggers when LOGIN Button clicked
    public void changePawd(View arg0) {

        final String password1 = changePawd1View.getText().toString();
        final String password2 = changePawd2View.getText().toString();

        // Initialize  AsyncLogin() class with email and password
        //*new AsyncLogin().execute(email,password);*/
        new AsyncLogin().execute(password1,password2);

    }

    private class AsyncLogin extends AsyncTask<String, String, String>
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

                url = new URL("http://192.168.0.2:8000/rest-auth/password/change/");

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
                        .appendQueryParameter("password1", params[0])
                        .appendQueryParameter("password2", params[1]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Log.d("tmdlsk", "테스트1" + query);
                Log.d("tmdlsk", "테스트2" + writer);
                Log.d("tmdlsk", "테스트2" + os);
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

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("receiveServer", "받는거1: " + result.toString());
                    Log.d("receiveServer", "받는거3: " + reader);

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
