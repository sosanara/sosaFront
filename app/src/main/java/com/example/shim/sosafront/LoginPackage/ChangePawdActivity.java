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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shim.sosafront.DatabasePackage.DataStore;
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


public class ChangePawdActivity extends Activity {

    private LinearLayout networkCheckLayout;
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private EditText oldPawdView;
    private EditText changePawd1View;
    private EditText changePawd2View;

    private Button changePawdBtn;

    private String authKey;
    private DataStore dataStore;

    private String errorOldPawd;
    private String errorNewPawd1;
    private String errorNewPawd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pawd);

        networkCheckLayout = (LinearLayout)findViewById(R.id.networkCheckLayout);
        oldPawdView = (EditText) findViewById(R.id.oldPawdView);
        changePawd1View = (EditText) findViewById(R.id.changePawd1View);
        changePawd2View = (EditText) findViewById(R.id.changePawd2View);
        changePawdBtn = (Button) findViewById(R.id.changePawdBtn);

        /*oldPawdView.setText("test12345", TextView.BufferType.EDITABLE);
        changePawd1View.setText("qwer1234", TextView.BufferType.EDITABLE);
        changePawd2View.setText("qwer1234", TextView.BufferType.EDITABLE);*/

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

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

            ConnectivityManager manager =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mobile.isConnected() || wifi.isConnected()){
                Toast.makeText(getApplicationContext(), "연결성공", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "연결실패", Toast.LENGTH_LONG).show();
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

                    Log.d("ChangePawdActivityLog", "ChangePawdActivityLog 2-0 : " + errorResult.toString());

                    if(serverJsonValue.contains("old_password"))
                        errorOldPawd = serverJsonObject.getString("old_password");

                    if(serverJsonValue.contains("new_password1"))
                        errorNewPawd1 = serverJsonObject.getString("new_password1");

                    if(serverJsonValue.contains("new_password2"))
                        errorNewPawd2 = serverJsonObject.getString("new_password2");

                    Log.d("ChangePawdActivityLog", "ChangePawdActivityLog 2-1 : " + errorOldPawd);
                    Log.d("ChangePawdActivityLog", "ChangePawdActivityLog 2-2 : " + errorNewPawd1);
                    Log.d("ChangePawdActivityLog", "ChangePawdActivityLog 2-3 : " + errorNewPawd2);

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

            Log.d("SignUpActivityLog", "SignUpActivityLog 3-0 : " + result);

            pdLoading.dismiss();

            if(result.equals("successful")) {
                Toast.makeText(getApplicationContext(), "비밀번호 수정 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChangePawdActivity.this, LoginActivity.class);
                startActivity(intent);
                ChangePawdActivity.this.finish();
            }

            else {
                Toast.makeText(getApplicationContext(), "비밀번호 수정 실패", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
