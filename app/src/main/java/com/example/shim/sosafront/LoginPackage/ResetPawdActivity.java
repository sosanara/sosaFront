package com.example.shim.sosafront.LoginPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ResetPawdActivity extends Activity {

    private LinearLayout networkCheckLayout;
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private EditText resetPawd1View;
    private EditText resetPawd2View;
    private EditText resetUid;
    private EditText resetToken;

    private String errorToken;
    private String errorUid;
    private String errorNewPawd1;
    private String errorNewPawd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pawd);

        //글씨체 통일
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        networkCheckLayout = (LinearLayout)findViewById(R.id.networkCheckLayout);
        resetPawd1View = (EditText) findViewById(R.id.resetPawd1View);
        resetPawd2View = (EditText) findViewById(R.id.resetPawd2View);
        resetUid = (EditText) findViewById(R.id.resetUid);
        resetToken = (EditText) findViewById(R.id.resetToken);

        /*resetPawd1View.setText("qwer1234", TextView.BufferType.EDITABLE);
        resetPawd2View.setText("qwer1234", TextView.BufferType.EDITABLE);*/

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

    // Triggers when changePawd Button clicked
    public void resetPawd(View arg0) {

        final String new_password1 = resetPawd1View.getText().toString();
        final String new_password2 = resetPawd2View.getText().toString();
        final String uid = resetUid.getText().toString();
        final String token = resetToken.getText().toString();

        // Initialize  AsyncLogin() class with email and password
        new AsyncChangePawd().execute(new_password1, new_password2, uid, token);

    }

    private class AsyncChangePawd extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ResetPawdActivity.this);
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
                url = new URL("http://113.198.84.37/rest-auth/password/reset/confirm/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("new_password1", params[0])
                        .appendQueryParameter("new_password2", params[1])
                        .appendQueryParameter("uid", params[2])
                        .appendQueryParameter("token", params[3]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Log.d("ResetPawdActivityLog", "ResetPawdActivityLog 0-1" + query);

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

                Log.d("ResetPawdActivityLog", "ResetPawdActivityLog 1-0 : " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("ResetPawdActivityLog", "ResetPawdActivityLog 1-1 : " + result.toString());

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("ResetPawdActivityLog", "ResetPawdActivityLog 1-3: " + result.toString());
                    Log.d("ResetPawdActivityLog", "ResetPawdActivityLog 1-4: " + reader);

                    // Pass data to onPostExecute method
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

                    Log.d("ResetPawdActivityLog", "ResetPawdActivityLog 2-0 : " + errorResult.toString());

                    if(serverJsonValue.contains("token"))
                        errorToken = serverJsonObject.getString("token");

                    if(serverJsonValue.contains("uid"))
                        errorUid = serverJsonObject.getString("uid");

                    if(serverJsonValue.contains("new_password1"))
                        errorNewPawd1 = serverJsonObject.getString("new_password1");

                    if(serverJsonValue.contains("new_password2"))
                        errorNewPawd2 = serverJsonObject.getString("new_password2");


                    Log.d("ResetPawdActivityLog", "ResetPawdActivityLog 2-1 : " + errorToken);
                    Log.d("ResetPawdActivityLog", "ResetPawdActivityLog 2-2 : " + errorUid);
                    Log.d("ResetPawdActivityLog", "ResetPawdActivityLog 2-3 : " + errorNewPawd1);
                    Log.d("ResetPawdActivityLog", "ResetPawdActivityLog 2-4 : " + errorNewPawd2);


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


            Log.d("ResetPawdActivity", "ResetPawdActivity 3-0 : " + result);

            pdLoading.dismiss();

            if(result.equals("successful")) {
                Toast.makeText(getApplicationContext(), "비밀번호 리셋 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ResetPawdActivity.this, LoginActivity.class);
                startActivity(intent);
                ResetPawdActivity.this.finish();
            }

            else {
                Toast.makeText(getApplicationContext(), "비밀번호 리셋 실패", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
