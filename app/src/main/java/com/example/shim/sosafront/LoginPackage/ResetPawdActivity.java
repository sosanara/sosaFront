package com.example.shim.sosafront.LoginPackage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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

    private LinearLayout wholeLayout;
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

    private Button resetPawdBtn;

    String new_password1;
    String new_password2;
    String uid;
    String token;

    int uidLength = 0;
    int tokenLength = 0;
    int newPawd1Length = 0;
    int newPawd2Length = 0;

    Context mContext;

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

        wholeLayout = (LinearLayout) findViewById(R.id.wholeLayout);
        networkCheckLayout = (LinearLayout)findViewById(R.id.networkCheckLayout);
        resetPawd1View = (EditText) findViewById(R.id.resetPawd1View);
        resetPawd2View = (EditText) findViewById(R.id.resetPawd2View);
        resetUid = (EditText) findViewById(R.id.resetUid);
        resetToken = (EditText) findViewById(R.id.resetToken);
        resetPawdBtn = (Button) findViewById(R.id.resetPawdBtn);

        wholeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        mContext = getApplicationContext();
        resetPawd1View.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                loginBtn.setBackground(drawable);*/
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                uidLength = resetUid.getText().toString().getBytes().length;
                tokenLength = resetToken.getText().toString().getBytes().length;
                newPawd1Length = resetPawd1View.getText().toString().getBytes().length;
                newPawd2Length = resetPawd2View.getText().toString().getBytes().length;

                if (uidLength == 0 || newPawd1Length == 0 || newPawd2Length == 0
                || tokenLength == 0) { //회색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                    resetPawdBtn.setBackground(drawable);

                } else { //초록색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_green);
                    resetPawdBtn.setBackground(drawable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resetPawd2View.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                loginBtn.setBackground(drawable);*/
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                uidLength = resetUid.getText().toString().getBytes().length;
                tokenLength = resetToken.getText().toString().getBytes().length;
                newPawd1Length = resetPawd1View.getText().toString().getBytes().length;
                newPawd2Length = resetPawd2View.getText().toString().getBytes().length;

                if (uidLength == 0 || newPawd1Length == 0 || newPawd2Length == 0
                        || tokenLength == 0) { //회색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                    resetPawdBtn.setBackground(drawable);

                } else { //초록색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_green);
                    resetPawdBtn.setBackground(drawable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resetUid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                loginBtn.setBackground(drawable);*/
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                uidLength = resetUid.getText().toString().getBytes().length;
                tokenLength = resetToken.getText().toString().getBytes().length;
                newPawd1Length = resetPawd1View.getText().toString().getBytes().length;
                newPawd2Length = resetPawd2View.getText().toString().getBytes().length;

                if (uidLength == 0 || newPawd1Length == 0 || newPawd2Length == 0
                        || tokenLength == 0) { //회색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                    resetPawdBtn.setBackground(drawable);

                } else { //초록색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_green);
                    resetPawdBtn.setBackground(drawable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resetToken.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                loginBtn.setBackground(drawable);*/
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                uidLength = resetUid.getText().toString().getBytes().length;
                tokenLength = resetToken.getText().toString().getBytes().length;
                newPawd1Length = resetPawd1View.getText().toString().getBytes().length;
                newPawd2Length = resetPawd2View.getText().toString().getBytes().length;

                if (uidLength == 0 || newPawd1Length == 0 || newPawd2Length == 0
                        || tokenLength == 0) { //회색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                    resetPawdBtn.setBackground(drawable);

                } else { //초록색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_green);
                    resetPawdBtn.setBackground(drawable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

        new_password1 = resetPawd1View.getText().toString();
        new_password2 = resetPawd2View.getText().toString();
        uid = resetUid.getText().toString();
        token = resetToken.getText().toString();

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

           /* if (!validate()) {
                return;
            }*/

            if(result.equals("successful")) {
               /* Toast.makeText(getApplicationContext(), "비밀번호 리셋 성공", Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(ResetPawdActivity.this, LoginActivity.class);
                startActivity(intent);
                ResetPawdActivity.this.finish();
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

        /*private String errorToken;
        private String errorUid;
        private String er        rorNewPawd2;*/
        if(TextUtils.isEmpty(errorNewPawd1)){
            resetPawd1View.setBackgroundResource(R.drawable.text_border);
            resetPawd1View.setError(null);
        } else if (errorNewPawd1.contains("This field may not be blank")) {
            resetPawd1View.setBackgroundResource(R.drawable.text_border_green);
            resetPawd1View.setText("");
            resetPawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            resetPawd1View.setText("비밀번호를 입력해주세요");
            valid = false;
        } else if (errorNewPawd1.contains("The password is too similar to the username")) {
            resetPawd1View.setBackgroundResource(R.drawable.text_border_green);
            resetPawd1View.setText("");
            resetPawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            resetPawd1View.setText("기존 비밀번호와 같습니다. 다른 비밀번호를 입력해 주세요.");
            valid = false;
        } else if (errorNewPawd1.contains("This password is too short")){
            resetPawd1View.setBackgroundResource(R.drawable.text_border_green);
            resetPawd1View.setText("");
            resetPawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            resetPawd1View.setText("안전도가 너무 낮습니다. 다른 비밀번호를 입력해 주세요.");
            valid = false;
        } else if (errorNewPawd1.contains("This password is too common")){
            resetPawd1View.setBackgroundResource(R.drawable.text_border_green);
            resetPawd1View.setText("");
            resetPawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            resetPawd1View.setText("보안상의 이유로 한 문자로 연속된 비밀번호는 허용하지 않습니다.");
            valid = false;
        } else {
            //여기에 원래 style 다시적용
            resetPawd1View.setBackgroundResource(R.drawable.text_border_green);
            resetPawd1View.setText("");
            resetPawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            resetPawd1View.setText("다른 비밀번호를 입력해 주세요.");
            valid = false;
        }

        if(TextUtils.isEmpty(errorNewPawd2)){
            resetPawd2View.setBackgroundResource(R.drawable.text_border);
            resetPawd2View.setError(null);
        } else if (errorNewPawd2.contains("This field may not be blank")) {
            resetPawd2View.setBackgroundResource(R.drawable.text_border_green);
            resetPawd2View.setText("");
            resetPawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            resetPawd2View.setHint("비밀번호를 입력해주세요");
            valid = false;
        } else if (errorNewPawd2.contains("The password is too similar to the username")) {
            resetPawd2View.setBackgroundResource(R.drawable.text_border_green);
            resetPawd2View.setText("");
            resetPawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            resetPawd2View.setHint("기존 비밀번호와 같습니다. 다른 비밀번호를 입력해 주세요.");
            valid = false;
        } else if (errorNewPawd2.contains("This password is too short")){
            resetPawd2View.setBackgroundResource(R.drawable.text_border_green);
            resetPawd2View.setText("");
            resetPawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            resetPawd2View.setHint("안전도가 너무 낮습니다. 다른 비밀번호를 입력해 주세요.");
            valid = false;
        } else if (errorNewPawd2.contains("This password is too common")){
            resetPawd2View.setBackgroundResource(R.drawable.text_border_green);
            resetPawd2View.setText("");
            resetPawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            resetPawd2View.setHint("보안상의 이유로 한 문자로 연속된 비밀번호는 허용하지 않습니다.");
            valid = false;
        } else if (errorNewPawd2.contains("The two password fields didn't match")) {
            resetPawd2View.setBackgroundResource(R.drawable.text_border_green);
            resetPawd2View.setText("");
            resetPawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            resetPawd2View.setHint("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            valid = false;
        } else {
            resetPawd2View.setHint("다른 비밀번호를 입력해 주세요.");
            valid = false;
        }

        if(TextUtils.isEmpty(errorUid)){
            resetUid.setBackgroundResource(R.drawable.text_border);
            resetUid.setError(null);
        } else if (errorUid.contains("This field may not be blank")) {
            resetUid.setBackgroundResource(R.drawable.text_border_green);
            resetUid.setText("");
            resetUid.setHintTextColor(Color.parseColor("#2eb74f"));
            resetUid.setHint("Uid 값을 입력해 주세요.");
            valid = false;
        } else if (errorUid.contains("Invalid value")) {
            resetUid.setBackgroundResource(R.drawable.text_border_green);
            resetUid.setText("");
            resetUid.setHintTextColor(Color.parseColor("#2eb74f"));
            resetUid.setHint("Uid 값을 잘못 입력하셨습니다.");
            valid = false;
        } else {
            resetUid.setBackgroundResource(R.drawable.text_border_green);
            resetUid.setText("");
            resetUid.setHintTextColor(Color.parseColor("#2eb74f"));
            resetUid.setHint("UID 값을 다시 확인하세요.");
            valid = false;
        }

        if(TextUtils.isEmpty(errorToken)){
            resetToken.setBackgroundResource(R.drawable.text_border);
            resetToken.setError(null);
        } else if (errorToken.isEmpty() || errorToken.contains("This field may not be blank")) {
            resetToken.setBackgroundResource(R.drawable.text_border_green);
            resetToken.setText("");
            resetToken.setHintTextColor(Color.parseColor("#2eb74f"));
            resetToken.setHint("Token 값을 입력해 주세요.");
            valid = false;
        } else if (errorToken.contains("Invalid value")) {
            resetToken.setBackgroundResource(R.drawable.text_border_green);
            resetToken.setText("");
            resetToken.setHintTextColor(Color.parseColor("#2eb74f"));
            resetToken.setHint("Token 값을 잘못 입력하셨습니다.");
            valid = false;
        } else {
            resetToken.setBackgroundResource(R.drawable.text_border_green);
            resetToken.setText("");
            resetToken.setHintTextColor(Color.parseColor("#2eb74f"));
            resetToken.setHint("Token 값을 다시 확인하세요.");
            valid = false;
        }



        return valid;
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
