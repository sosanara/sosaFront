package com.example.shim.sosafront.LoginPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


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

    LinearLayout wholeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pawd);

        //글씨체 통일
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        wholeLayout = (LinearLayout) findViewById(R.id.wholeLayout);
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


        wholeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    public void networkCheck(View v) {

        switch (v.getId()) {
            case R.id.networkCheckBtn:
                finish();
                startActivity(getIntent());
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
                Intent intent = new Intent(ChangePawdActivity.this, LoginActivity.class);
                startActivity(intent);
                ChangePawdActivity.this.finish();
            }

            else {
                if (!validate()) {
                    return;
                }
            }
        }

    }

    public boolean validate() {
        boolean valid = true;

        //여기에 바뀐 style 다시적용

        if (TextUtils.isEmpty(errorOldPawd)) {
            oldPawdView.setBackgroundResource(R.drawable.text_border);
            oldPawdView.setError(null);
        } else if (errorOldPawd.contains("This field may not be blank")) {
            oldPawdView.setBackgroundResource(R.drawable.text_border_green);
            oldPawdView.setText("");
            oldPawdView.setHintTextColor(Color.parseColor("#2eb74f"));
            oldPawdView.setHint("현재 비밀번호를 입력해 주세요.");
            valid = false;
        } else if (errorOldPawd.contains("The two password fields didn't match")) {
            oldPawdView.setBackgroundResource(R.drawable.text_border_green);
            oldPawdView.setText("");
            oldPawdView.setHintTextColor(Color.parseColor("#2eb74f"));
            oldPawdView.setHint("현재 비밀번호가 일치하지 않습니다.");
            valid = false;
        } else if (errorOldPawd.contains("Password must be a minimum of 8 characters.")) {
            oldPawdView.setBackgroundResource(R.drawable.text_border_green);
            oldPawdView.setText("");
            oldPawdView.setHintTextColor(Color.parseColor("#2eb74f"));
            oldPawdView.setHint("8자리 이상으로 입력해주세요.");
            valid = false;
        } else {
            oldPawdView.setHint("현재 비밀번호를 입력해 주세요.");
            oldPawdView.setBackgroundResource(R.drawable.text_border_green);
            oldPawdView.setText("");
            oldPawdView.setHintTextColor(Color.parseColor("#2eb74f"));
            valid = false;
        }


        if(TextUtils.isEmpty(errorNewPawd1)){
            changePawd1View.setBackgroundResource(R.drawable.text_border);
            changePawd1View.setError(null);
        } else if (errorNewPawd1.contains("This field may not be blank")) {
            changePawd1View.setBackgroundResource(R.drawable.text_border_green);
            changePawd1View.setText("");
            changePawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd1View.setHint("새 비밀번호를 입력해주세요");
            valid = false;
        } else if (errorNewPawd1.contains("The password is too similar to the username")) {
            changePawd1View.setBackgroundResource(R.drawable.text_border_green);
            changePawd1View.setText("");
            changePawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd1View.setHint("기존 비밀번호와 같습니다. 다른 비밀번호를 입력해 주세요.");
            valid = false;
        } else if (errorNewPawd1.contains("This password is too short")){
            changePawd1View.setBackgroundResource(R.drawable.text_border_green);
            changePawd1View.setText("");
            changePawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd1View.setHint("안전도가 너무 낮습니다. 다른 비밀번호를 입력해 주세요.");
            valid = false;
        } else if (errorNewPawd1.contains("This password is too common")){
            changePawd1View.setBackgroundResource(R.drawable.text_border_green);
            changePawd1View.setText("");
            changePawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd1View.setHint("보안상의 이유로 한 문자로 연속된 비밀번호는 허용하지 않습니다.");
            valid = false;
        } else {
            changePawd1View.setBackgroundResource(R.drawable.text_border_green);
            changePawd1View.setText("");
            changePawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd1View.setHint("다른 비밀번호를 입력해 주세요.");
            valid = false;
        }

        if(TextUtils.isEmpty(errorNewPawd2)){
            changePawd2View.setBackgroundResource(R.drawable.text_border);
            changePawd2View.setError(null);
        } else if (errorNewPawd2.contains("This field may not be blank")) {
            changePawd2View.setBackgroundResource(R.drawable.text_border_green);
            changePawd2View.setText("");
            changePawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd2View.setHint("새 비밀번호 확인을 입력해주세요");
            valid = false;
        } else if (errorNewPawd2.contains("The password is too similar to the username")) {
            changePawd2View.setBackgroundResource(R.drawable.text_border_green);
            changePawd2View.setText("");
            changePawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd2View.setHint("기존 비밀번호와 같습니다.");
            changePawd1View.setText("");
            changePawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd1View.setHint("기존 비밀번호와 같습니다.");
            valid = false;
        } else if (errorNewPawd2.contains("This password is too short")){
            changePawd2View.setBackgroundResource(R.drawable.text_border_green);
            changePawd2View.setText("");
            changePawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd2View.setHint("안전도가 너무 낮습니다.");
            valid = false;
        } else if (errorNewPawd2.contains("This password is too common")){
            changePawd2View.setBackgroundResource(R.drawable.text_border_green);
            changePawd2View.setText("");
            changePawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd2View.setHint("연속된 비밀번호는 허용하지 않습니다.");
            valid = false;
        } else if (errorNewPawd2.contains("The two password fields didn't match")) {
            changePawd2View.setBackgroundResource(R.drawable.text_border_green);
            changePawd2View.setText("");
            changePawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd2View.setHint("새 비밀번호와 비밀번호 확인이 다릅니다.");
            valid = false;
        } else {
            changePawd2View.setBackgroundResource(R.drawable.text_border_green);
            changePawd2View.setText("");
            changePawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            changePawd2View.setHint("다른 비밀번호를 입력해 주세요.");
            valid = false;
        }

        return valid;
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
