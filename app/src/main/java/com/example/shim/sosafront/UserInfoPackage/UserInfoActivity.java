package com.example.shim.sosafront.UserInfoPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.LoginPackage.ChangePawdActivity;
import com.example.shim.sosafront.LoginPackage.LogoutActivity;
import com.example.shim.sosafront.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class UserInfoActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private TextView userInfoUserNameView;
    private TextView userInfoEmailView;
    private TextView userInfoAgeView;
    private TextView userInfoNameView;
    private TextView userInfoGenderView;

    private String userInfoUserName;
    private String userInfoGender;
    private String userInfoName;
    private String userInfoAge;
    private String userInfoEmail;

    private String authKey;
    private DataStore dataStore;

    private Button userInfoChangeBtn;
    private Button pswdChangeBtn;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Get Reference to variables
        userInfoUserNameView = (TextView) findViewById(R.id.userInfoUserNameView);
        userInfoEmailView = (TextView) findViewById(R.id.userInfoEmailView);
        userInfoAgeView = (TextView) findViewById(R.id.userInfoAgeView);
        userInfoNameView = (TextView) findViewById(R.id.userInfoNameView);
        userInfoGenderView = (TextView) findViewById(R.id.userInfoGenderView);

        userInfoChangeBtn = (Button) findViewById(R.id.userInfoChangeBtn);
        pswdChangeBtn = (Button) findViewById(R.id.pswdChangeBtn);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

        new AsynUserInfo().execute();

        userInfoChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveChangeUserInfo = new Intent(UserInfoActivity.this, ChangeUserInfoActivity.class);
                moveChangeUserInfo.putExtra("userName", userInfoUserName);
                startActivity(moveChangeUserInfo);
                UserInfoActivity.this.finish();
            }
        });

        pswdChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveChangePawd = new Intent(UserInfoActivity.this, ChangePawdActivity.class);
                startActivity(moveChangePawd);
                UserInfoActivity.this.finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveLogout = new Intent(UserInfoActivity.this, LogoutActivity.class);
                startActivity(moveLogout);
                UserInfoActivity.this.finish();
            }
        });
    }

    private class AsynUserInfo extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(UserInfoActivity.this);
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
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", " Token " + authKey);
                conn.setDoInput(true);
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                Log.d("UserInfoLog", "UserInfoLog 0-0: " + conn.getResponseCode());
                Log.d("UserInfoLog", "UserInfoLog 0-1: " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("UserInfoActivity", "UserInfoActivity 1-1: " + result.toString());
                    Log.d("UserInfoActivity", "UserInfoActivity 1-2:" + reader);

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    String value = result.toString();
                    JSONObject jsonObject = new JSONObject(value);

                    userInfoUserName = jsonObject.getString("username").toString();
                    userInfoGender = jsonObject.getString("gender").toString();
                    userInfoName = jsonObject.getString("first_name").toString() + jsonObject.getString("last_name").toString();
                    userInfoAge = jsonObject.getString("birth").toString();
                    userInfoEmail = jsonObject.getString("email").toString();

                    Log.d("UserInfoLog", "UserInfoLog 2-1: " + userInfoUserName);
                    Log.d("UserInfoLog", "UserInfoLog 2-2: " + userInfoGender);
                    Log.d("UserInfoLog", "UserInfoLog 2-3: " + userInfoName);
                    Log.d("UserInfoLog", "UserInfoLog 2-4: " + userInfoAge);
                    Log.d("UserInfoLog", "UserInfoLog 2-5: " + userInfoEmail);

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

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

            userInfoUserNameView.setText(userInfoUserName);
            userInfoEmailView.setText(userInfoEmail);
            userInfoAgeView.setText(userInfoAge);
            userInfoNameView.setText(userInfoName);

            if(userInfoGender.contains("Man"))
                userInfoGenderView.setText("남성");

            else
                userInfoGenderView.setText("여성");

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
