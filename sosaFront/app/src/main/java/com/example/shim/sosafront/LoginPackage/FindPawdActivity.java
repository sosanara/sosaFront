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


public class FindPawdActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private EditText findPassEmailView;
    private Button resetPawdBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pawd);

        // Get Reference to variables
        findPassEmailView = (EditText) findViewById(R.id.findPassEmailView);
        findPassEmailView.setText("shim5365@naver.com", TextView.BufferType.EDITABLE);


    }

    // Triggers when findPawd Button clicked
    public void findPawd(View arg0) {

        final String email = findPassEmailView.getText().toString();


        // Initialize  AsyncLogin() class with email and password
        //*new AsyncLogin().execute(email,password);*/
        new AsyncChangePawd().execute(email);

    }

    private class AsyncChangePawd extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(FindPawdActivity.this);
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
                url = new URL("http://113.198.84.37/rest-auth/password/reset/");

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
                        .appendQueryParameter("email", params[0]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Log.d("tmdlsk", "비밀번호찾기 테스트1-0 " + query);
                Log.d("tmdlsk", "비밀번호찾기 테스트1-1 " + writer);
                Log.d("tmdlsk", "비밀번호찾기 테스트1-2 " + os);
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

                    Log.d("receiveServer", "비밀번호찾기 테스트4-0 " + conn.getResponseCode());
                    Log.d("receiveServer", "비밀번호찾기 테스트4-1 " + conn.getResponseCode());

                    // Check if successful connection made

                    if (response_code == HttpURLConnection.HTTP_OK) {

                        // Read data sent from server
                        InputStream input = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        StringBuilder result = new StringBuilder();
                        String line;
                        Log.d("receiveServer", "비밀번호찾기 테스트4-2 " + result.toString());
                        Log.d("receiveServer", "비밀번호찾기 테스트4-3 " + reader);

                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        Log.d("receiveServer", "비밀번호찾기 테스트4-4: " + result);
                        Log.d("receiveServer", "비밀번호찾기 테스트4-5: " + reader);

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

            Intent resetIntent = new Intent(FindPawdActivity.this, ResetPawdActivity.class);
            startActivity(resetIntent);

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true"))
            {
                Log.d("tmdlsk", "테스트 테스트 테스트");
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

