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
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.MainPackage.MainActivity;
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

public class LoginActivity extends Activity {

    private LinearLayout networkCheckLayout;

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private EditText loginUserNameView;
    private EditText loginPawdView;

    private TextView signUp;
    private TextView findPawd;

    private Button loginBtn;
    private Button moveFindPassBtn;
    private Button moveSignUpBtn;

    private String errorUsername;
    private String errorPassword;

    DataStore dataStore;
    String authKey;

    LinearLayout wholeLayout;

    int userNameLength = 0;
    int passwordLength = 0;

    Context mContext;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //글씨체 통일
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        setContentView(R.layout.activity_login);

        Window window = this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.subTextColor));


        dataStore = new DataStore(this);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        wholeLayout = (LinearLayout) findViewById(R.id.wholeLayout);
        networkCheckLayout = (LinearLayout) findViewById(R.id.networkCheckLayout);
        loginUserNameView = (EditText) findViewById(R.id.loginUserNameView);
        loginPawdView = (EditText) findViewById(R.id.loginPawdView);

        signUp = (TextView) findViewById(R.id.signUp);
        findPawd = (TextView) findViewById(R.id.findPawd);
        /*moveFindPassBtn = (Button) findViewById(R.id.moveFindPass);
        moveSignUpBtn = (Button) findViewById(R.id.moveSignUp);*/

        signUp.setText(Html.fromHtml("<u>회원가입</u>"), TextView.BufferType.SPANNABLE);
        findPawd.setText(Html.fromHtml("<u>비밀번호찾기</u>"), TextView.BufferType.SPANNABLE);

        loginUserNameView.setText("qwer1234", TextView.BufferType.EDITABLE);
        loginPawdView.setText("zxcv1234", TextView.BufferType.EDITABLE);

        signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent moveSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(moveSignUp);

                }
        });

        findPawd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveFindPawd = new Intent(LoginActivity.this, FindPawdActivity.class);
                startActivity(moveFindPawd);

            }
        });

        wholeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });



        mContext = getApplicationContext();

        loginUserNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                loginBtn.setBackground(drawable);*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userNameLength =loginUserNameView.getText().toString().getBytes().length;
                passwordLength = loginPawdView.getText().toString().getBytes().length;

                if (userNameLength == 0 || passwordLength == 0) { //회색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                    loginBtn.setBackground(drawable);

                } else {  //초록색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_green);
                    loginBtn.setBackground(drawable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginPawdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                loginBtn.setBackground(drawable);*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                userNameLength =loginUserNameView.getText().toString().getBytes().length;
                passwordLength = loginPawdView.getText().toString().getBytes().length;

                if (userNameLength == 0 || passwordLength == 0) { //회색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_grey);
                    loginBtn.setBackground(drawable);

                } else { //초록색
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_border_green);
                    loginBtn.setBackground(drawable);
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

    // Triggers when LOGIN Button clicked
    public void startLogin(View arg0) {

        final String username = loginUserNameView.getText().toString();
        final String password = loginPawdView.getText().toString();

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(username, password);

    }

    private class AsyncLogin extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {  //작업처리중' 프로그레스 다이얼로그 자동 시작
            super.onPreExecute();

            ConnectivityManager manager =
                    (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mobile.isConnected() || wifi.isConnected()) {
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

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {  //프로그레스 다이얼로그 자동 종료 및 에러메시지 토스트보여줌
            //doInBackground()에서 에러발생시 하위 클래스의 onPostExecute()는 실행되지 않음
            try {
                // Enter URL address where your php file resides
                url = new URL("http://113.198.84.37/rest-auth/login/");
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

                //Request Header값 셋팅
                conn.setRequestProperty("Accept", "application/json");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true); // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션
                conn.setDoOutput(true); // InputStream으로 서버로 부터 응답을 받겠다는 옵션.

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream(); // Request Body에 Data를 담기위해 OutputStream 객체를 생성.
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));


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

                Log.d("LoginActivityLog", "LoginActivityLog 0-0 : " + conn.getResponseCode());


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

                    Log.d("LoginActivityLog", "LoginActivityLog 1 : " + result.toString());  // result.toString()

                    try {

                        String value = result.toString();
                        JSONObject testJson = new JSONObject(value);
                        authKey = (String) testJson.get("key");
                        dataStore.put("key", authKey);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return ("successful");

                } else {
                    InputStream errorInputStream = conn.getErrorStream();
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorInputStream));
                    StringBuilder errorResult = new StringBuilder();
                    String failLine;

                    while ((failLine = errorReader.readLine()) != null) {
                        errorResult.append(failLine);
                    }

                    String serverJsonValue = errorResult.toString();
                    JSONObject serverJsonObject = new JSONObject(serverJsonValue);

                    Log.d("LoginActivityLog", "LoginActivityLog 2-2 : " + errorResult.toString());

                    if(serverJsonValue.contains("Must include") || serverJsonValue.contains("non_field_errors")) {
                        if(serverJsonValue.contains("username"))

                        errorUsername = errorResult.toString();
                        errorPassword = errorResult.toString();
                    }

                    if (serverJsonValue.contains("username"))
                        errorUsername = serverJsonObject.getString("username");

                    if (serverJsonValue.contains("password"))
                        errorPassword = serverJsonObject.getString("password");




                    Log.d("LoginActivityLog", "LoginActivityLog 2-3 : " + errorUsername);
                    Log.d("LoginActivityLog", "LoginActivityLog 2-4 : " + errorPassword);

                    return ("unsuccessful");
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

            Log.d("LoginActivityLog", "LoginActivityLog 3-0 : " + result);
            pdLoading.dismiss();

            /*if (!validate()) {
                return;
            }*/

            if (result.equals("successful"))  //equals랑 같음(대소문자까지)
            {

                Intent moveMainIntent = new Intent(LoginActivity.this, MainActivity.class);
                moveMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                moveMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(moveMainIntent);

            } else {
                if (!validate()) {
                    return;
                }
            }

        }

    }

    public boolean validate() {
        boolean valid = true;

        //여기에 바뀐 style 다시적용

        if(TextUtils.isEmpty(errorUsername)){
            loginUserNameView.setBackgroundResource(R.drawable.text_border);
            loginUserNameView.setError(null);
        } else if (errorUsername.contains("This field may not be blank")) {
            loginUserNameView.setBackgroundResource(R.drawable.text_border_green);
            loginUserNameView.setText("");
            loginUserNameView.setHintTextColor(Color.parseColor("#2eb74f"));
            loginUserNameView.setBackgroundResource( R.drawable.text_border_green);
            loginUserNameView.setHint("아이디를 입력해 주세요.");
            valid = false;
        } else if (errorUsername.contains("Unable to log in with provided credentials")||
                errorUsername.contains("Must include")) {
            loginUserNameView.setBackgroundResource(R.drawable.text_border_green);
            loginUserNameView.setText("");
            loginUserNameView.setHintTextColor(Color.parseColor("#2eb74f"));
            loginUserNameView.setBackgroundResource( R.drawable.text_border_green);
            loginUserNameView.setHint("아이디를 다시 확인하세요.");
            valid = false;
        } else {
            loginUserNameView.setBackgroundResource(R.drawable.text_border_green);
            loginUserNameView.setText("");
            loginUserNameView.setHintTextColor(Color.parseColor("#2eb74f"));
            loginUserNameView.setBackgroundResource( R.drawable.text_border_green);
            loginUserNameView.setHint("아이디를 다시 확인하세요.");
            //여기에 원래 style 다시적용
            /*loginUserNameView.setError(null);*/
        }

        if(TextUtils.isEmpty(errorPassword)){
            loginPawdView.setBackgroundResource(R.drawable.text_border);
            loginPawdView.setError(null);
        } else if (errorPassword.contains("This field may not be blank")) {
            loginPawdView.setBackgroundResource(R.drawable.text_border_green);
            loginPawdView.setText("");
            loginPawdView.setHintTextColor(Color.parseColor("#2eb74f"));
            loginPawdView.setBackgroundResource(R.drawable.text_border_green);
            loginPawdView.setHint("패스워드를 입력해 주세요.");
            valid = false;
        } else if (errorPassword.contains("Unable to log in with provided credentials")||
                errorPassword.contains("Must include")) {
            loginPawdView.setBackgroundResource(R.drawable.text_border_green);
            loginPawdView.setText("");
            loginPawdView.setHintTextColor(Color.parseColor("#2eb74f"));
            loginPawdView.setBackgroundResource(R.drawable.text_border_green);
            loginPawdView.setHint("비밀번호를 다시 확인하세요.");
            valid = false;
        } else {
            loginPawdView.setBackgroundResource(R.drawable.text_border_green);
            loginPawdView.setText("");
            loginPawdView.setHintTextColor(Color.parseColor("#2eb74f"));
            loginPawdView.setBackgroundResource(R.drawable.text_border_green);
            loginPawdView.setHint("비밀번호를 다시 확인하세요.");
        }


        return valid;
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


}
