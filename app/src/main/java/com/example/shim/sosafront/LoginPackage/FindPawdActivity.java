package com.example.shim.sosafront.LoginPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FindPawdActivity extends AppCompatActivity {

    private LinearLayout networkCheckLayout;
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private EditText emailView;
    private Button findPawdBtn;
    private String errorEmail;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pawd);

        networkCheckLayout = (LinearLayout)findViewById(R.id.networkCheckLayout);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        findPawdBtn =(Button) findViewById(R.id.findPawdBtn);
        emailView = (EditText) findViewById(R.id.emailView);

        Typeface tf=Typeface.createFromAsset(getAssets(), "fonts/NotoSansKR-Bold.otf");
        findPawdBtn.setTypeface(tf);


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void networkCheck(View v) {

        switch (v.getId()) {
            case R.id.networkCheckBtn:
                finish();
                startActivity(getIntent());
                break;
        }
    }

    // Triggers when findPawd Button clicked
    public void findPawd(View arg0) {

        final String email = emailView.getText().toString();


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

            ConnectivityManager manager =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mobile.isConnected() || wifi.isConnected()){
                /*Toast.makeText(getApplicationContext(), "연결성공", Toast.LENGTH_LONG).show();*/
            } else {
                /*Toast.makeText(getApplicationContext(), "연결실패", Toast.LENGTH_LONG).show();*/
                networkCheckLayout.setVisibility(View.VISIBLE);
            }

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
                Log.d("FindPawdActivityLog", "FindPawdActivityLog1-0 " + query);
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

                    Log.d("FindPawdActivityLog", "FindPawdActivityLog 2-0 " + conn.getResponseCode());

                    // Check if successful connection made

                    if (response_code == HttpURLConnection.HTTP_OK) {

                        // Read data sent from server
                        InputStream input = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        StringBuilder result = new StringBuilder();
                        String line;
                        Log.d("FindPawdActivityLog", "FindPawdActivityLog 4-2 " + result.toString());
                        Log.d("FindPawdActivityLog", "FindPawdActivityLog 4-3 " + reader);

                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        Log.d("FindPawdActivityLog", "FindPawdActivityLog 4-4 " + result);
                        Log.d("FindPawdActivityLog", "FindPawdActivityLog 4-5 " + reader);

                        // Pass data to onPostExecute method
                        /*return(result.toString());*/
                        return("successful");

                    }else{
                        InputStream errorInputStream = conn.getErrorStream();
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorInputStream));
                        StringBuilder errorResult = new StringBuilder();
                        String failLine;

                        while ((failLine = errorReader.readLine()) != null) {
                            errorResult.append(failLine);
                        }

                        String serverJsonValue = errorResult.toString();
                        JSONObject serverJsonObject = new JSONObject(serverJsonValue);

                        Log.d("FindPawdActivityLog", "FindPawdActivityLog 2-2 : " + errorResult.toString());
                        if(serverJsonValue.contains("email"))
                            errorEmail = serverJsonObject.getString("email");

                        Log.d("FindPawdActivityLog", "FindPawdActivityLog 2-2 : " + errorEmail);

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

            Log.d("FindPawdActivityLog", "FindPawdActivityLog 3-0 : " + result);
            //this method will be running on UI thread

            pdLoading.dismiss();

            if (result.equals("successful")) {
                Toast.makeText(getApplicationContext(), "이메일 일치합니다.", Toast.LENGTH_SHORT).show();
                Intent resetIntent = new Intent(FindPawdActivity.this, ResetPawdActivity.class);
                startActivity(resetIntent);
                FindPawdActivity.this.finish();
            } else {
                Toast.makeText(getApplicationContext(), "일치하지 않은 이메일 입니다.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}

