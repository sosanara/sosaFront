package com.example.shim.sosafront.UserInfoPackage;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ChangeUserInfoActivity extends Activity {

    private LinearLayout wholeLayout;
    private LinearLayout networkCheckLayout;
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private EditText nameView;
    private EditText birthView;
    private EditText GenderView;

    private String authKey;
    DataStore dataStore;


    private String userName;
    private String email;

    private String errorEmail;
    private String errorFirstName;
    private String errorLastName;
    private String errorBirth;
    private String errorGender;
    private String gender;
    private String birth;
    private TextView userNameView;
    private TextView emailAddressView;

    private Spinner ageSpinner;

    RadioButton maleRadioBtn;
    RadioButton femaleRadioBtn;
    RadioGroup genderRadioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);


        //글씨체 통일
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        userName = getIntent().getExtras().getString("userName");
        email = getIntent().getExtras().getString("email");

        wholeLayout = (LinearLayout) findViewById(R.id.wholeLayout);
        networkCheckLayout = (LinearLayout)findViewById(R.id.networkCheckLayout);
        userNameView = (TextView) findViewById(R.id.userNameView);
        emailAddressView = (TextView) findViewById(R.id.emailAddressView);
        nameView = (EditText) findViewById(R.id.nameView);
        ageSpinner = (Spinner) findViewById(R.id.ageSpinner);

        genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
        maleRadioBtn = (RadioButton) findViewById(R.id.maleRadioBtn);
        femaleRadioBtn = (RadioButton) findViewById(R.id.femaleRadioBtn);

        userNameView.setText(userName);
        emailAddressView.setText(email);
        /*emailAddressView.setText("uiop@naver.com", TextView.BufferType.EDITABLE);
        nameView.setText("mienhwr", TextView.BufferType.EDITABLE);*/

        changeGenderFunc();


        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");
        initAgeListSpinner();

        wholeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    //글씨체 통일
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // Triggers when changePawd Button clicked
    public void changeUserInfo(View arg0) {

        final String email = emailAddressView.getText().toString();
        final String name = nameView.getText().toString();
        birth = ageSpinner.getSelectedItem().toString();

        String first_name = "";
        String last_name = "";
        //이름 성과 이름으로 나눔

        if(name.length() >= 1) {
            first_name = name.substring(0, 1);
            last_name = name.substring(1, name.length());
        }



        // Initialize  AsyncLogin() class with email and password
        new AsyncChangeUserInfo().execute(email, first_name, last_name, birth, gender);

    }

    private class AsyncChangeUserInfo extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ChangeUserInfoActivity.this);
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

            } else {
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
                url = new URL("http://113.198.84.37/rest-auth/user/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }

            try {

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Authorization", " Token " + authKey);

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", params[0])
                        .appendQueryParameter("first_name", params[1])
                        .appendQueryParameter("last_name", params[2])
                        .appendQueryParameter("birth", params[3])
                        .appendQueryParameter("gender", params[4]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 0-0" + query);

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

                Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-0: " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-1: " + result.toString());

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    String value = result.toString();
                    JSONObject jsonObject = new JSONObject(value);

                    Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-2: " + result.toString());
                    Log.d("ChangeUserInfoLog", "ChangeUserInfoLog 1-3: " + reader);

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

                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-2 : " + errorResult.toString());

                    if(serverJsonValue.contains("first_name"))
                        errorFirstName = serverJsonObject.getString("first_name");

                    if(serverJsonValue.contains("last_name"))
                        errorLastName = serverJsonObject.getString("last_name");

                    if(serverJsonValue.contains("age"))
                        errorGender = serverJsonObject.getString("age");


                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-3 : " + errorEmail);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-4 : " + errorFirstName);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-5 : " + errorLastName);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-6 : " + errorBirth);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-7 : " + errorGender);

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
                /*Toast.makeText(getApplicationContext(), "내정보 수정 성공", Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(ChangeUserInfoActivity.this, UserInfoActivity.class);
                finish();
                startActivity(intent);
            }

            else {
                if (!validate()) {
                    return;
                }
                /*Toast.makeText(getApplicationContext(), "내정보 수정 실패", Toast.LENGTH_SHORT).show();*/
            }

        }

    }

    private void initAgeListSpinner() {

        // Custom choices
        List<CharSequence> choices = new ArrayList<CharSequence>();


        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(getAssets().open("ageSpinner/age.txt"), "euc_kr"));
            String line = in.readLine();

            while (line != null) {
                /*line = URLEncoder.encode(line, "KSC5601");*/
                choices.add(line);
                line = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Create an ArrayAdapter with custom choices
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, choices);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to th spinner
        ageSpinner.setAdapter(adapter);
        /*birth = ageSpinner.getSelectedItem().toString();*/
    }



    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        /*Toast.makeText(this, selectionSpinner.getSelectedItem() + " selected", Toast.LENGTH_SHORT).show();*/
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    void changeGenderFunc() {
        maleRadioBtn.setChecked(true);
        gender = "Man";
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                switch (checkedId) {
                    case R.id.maleRadioBtn:
                        // do operations specific to this selection
                        gender = "Man";
                        break;

                    case R.id.femaleRadioBtn:
                        // do operations specific to this selection
                        gender = "Women";
                        break;
                }
            }
        });

    }

    public boolean validate() {
        boolean valid = true;

        //여기에 바뀐 style 다시적용

        if(TextUtils.isEmpty(errorFirstName)){
            nameView.setBackgroundResource(R.drawable.text_border);
            nameView.setError(null);
        } else if (errorFirstName.contains("blank")) {
            nameView.setBackgroundResource(R.drawable.text_border_green);
            nameView.setText("");
            nameView.setHintTextColor(Color.parseColor("#2eb74f"));
            nameView.setHint("이름을 입력해 주세요.");
            valid = false;
        } else {
            nameView.setBackgroundResource(R.drawable.text_border_green);
            nameView.setText("");
            nameView.setHintTextColor(Color.parseColor("#2eb74f"));
            nameView.setHint("다른 이름을 입력해 주세요.");
            valid = false;
        }

        if(TextUtils.isEmpty(errorLastName)){
            nameView.setBackgroundResource(R.drawable.text_border_green);
            nameView.setText("");
            nameView.setHintTextColor(Color.parseColor("#2eb74f"));
            nameView.setBackgroundResource(R.drawable.text_border);
            nameView.setError(null);
        } else if (errorLastName.contains("blank")) {
            nameView.setBackgroundResource(R.drawable.text_border_green);
            nameView.setText("");
            nameView.setHintTextColor(Color.parseColor("#2eb74f"));
            nameView.setHint("이름을 입력해 주세요.");
            valid = false;
        } else {
            nameView.setBackgroundResource(R.drawable.text_border_green);
            nameView.setText("");
            nameView.setHintTextColor(Color.parseColor("#2eb74f"));
            nameView.setHint("다른 이름을 입력해 주세요.");
            valid = false;
        }

        return valid;
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

