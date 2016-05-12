package com.example.shim.sosafront.UserInfoPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.LoginPackage.LoginActivity;
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


public class ChangeUserInfoActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private EditText emailAddressView;
    private EditText nameView;
    private EditText birthView;
    private EditText GenderView;

    private String authKey;
    DataStore dataStore;

    private String errorEmail;
    private String errorFirstName;
    private String errorLastName;
    private String errorBirth;
    private String errorGender;
    private String gender;
    private String birth;

    private Spinner ageSpinner;

    RadioButton maleRadioBtn;
    RadioButton femaleRadioBtn;
    RadioGroup genderRadioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);

        // Get Reference to variables
        emailAddressView = (EditText) findViewById(R.id.emailAddressView);
        nameView = (EditText) findViewById(R.id.nameView);
        ageSpinner = (Spinner) findViewById(R.id.ageSpinner);

        genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
        maleRadioBtn = (RadioButton) findViewById(R.id.maleRadioBtn);
        femaleRadioBtn = (RadioButton) findViewById(R.id.femaleRadioBtn);

        emailAddressView.setText("uiop@naver.com", TextView.BufferType.EDITABLE);
        nameView.setText("mienhwr", TextView.BufferType.EDITABLE);

        changeGenderFunc();


        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");
        initAgeListSpinner();
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

                    if(serverJsonValue.contains("username"))
                        errorEmail = serverJsonObject.getString("username");

                    if(serverJsonValue.contains("email"))
                        errorFirstName = serverJsonObject.getString("email");

                    if(serverJsonValue.contains("password1"))
                        errorLastName = serverJsonObject.getString("password1");

                    if(serverJsonValue.contains("password2"))
                        errorBirth = serverJsonObject.getString("password2");

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
                Toast.makeText(getApplicationContext(), "내정보 수정 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChangeUserInfoActivity.this, LoginActivity.class);
                startActivity(intent);
                ChangeUserInfoActivity.this.finish();
            }

            else {
                Toast.makeText(getApplicationContext(), "내정보 수정 실패", Toast.LENGTH_SHORT).show();
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

}

