package com.example.shim.sosafront.LoginPackage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import butterknife.Bind;
import fr.ganfra.materialspinner.MaterialSpinner;


public class SignUpActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    String username;
    String email;
    String password1;
    String password2;
    String birth;
    String name;
    String gender;

    private String errorUsername;
    private String errorEmail;
    private String errorPassword1;
    private String errorPassword2;
    private String errorAge;
    private String errorName;
    private String errorGender;

    RadioButton maleRadioBtn;
    RadioButton femaleRadioBtn;
    RadioGroup genderRadioGroup;

    Toolbar toolbar;
    private ArrayAdapter<String> adapter;

    private static final String ERROR_MSG = "Very very very long error message to get scrolling or multiline animation when the error button is clicked";
    MaterialSpinner android_material_design_spinner;

    @Bind(R.id.signUpUserNameView) EditText signUpUserNameView;
    @Bind(R.id.signUpEmailView) EditText signUpEmailView;
    @Bind(R.id.signUpPawd1View) EditText signUpPawd1View;
    @Bind(R.id.signUpPawd2View) EditText signUpPawd2View;
    @Bind(R.id.signUpAgeView) EditText signUpAgeView;
    @Bind(R.id.signUpNameView) EditText signUpNameView;
    @Bind(R.id.signUpGenderView) EditText signUpGenderView;
    @Bind(R.id.loginBtn) Button loginBtn;

    String[] SPINNERLIST =
            {"1910", "1911", "1912", "1913", "1914", "1915", "1916", "1917", "1918", "1919",
            "1920", "1921", "1922", "1923", "1924", "1925", "1926", "1927", "1928", "1929",
            "1930", "1931", "1932", "1933", "1934", "1935", "1936", "1937", "1938", "1939",
            "1940", "1941", "1942", "1943", "1944", "1945", "1946", "1947", "1948", "1949",
            "1950", "1951", "1952", "1953", "1954", "1955", "1956", "1957", "1958", "1959",
            "1960", "1961", "1962", "1963", "1964", "1965", "1966", "1967", "1968", "1969",
            "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979",
            "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989",
            "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999",
            "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009",
            "2010", "2011", "2012", "2013", "2014", "2015", "2016"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        // Get Reference to variables
        signUpUserNameView = (EditText) findViewById(R.id.signUpUserNameView);
        signUpEmailView = (EditText) findViewById(R.id.signUpEmailView);
        signUpPawd1View = (EditText) findViewById(R.id.signUpPawd1View);
        signUpPawd2View = (EditText) findViewById(R.id.signUpPawd2View);
        signUpAgeView = (EditText) findViewById(R.id.signUpAgeView);
        signUpNameView = (EditText) findViewById(R.id.signUpNameView);
        signUpGenderView= (EditText) findViewById(R.id.signUpGenderView);

        genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
        maleRadioBtn = (RadioButton) findViewById(R.id.maleRadioBtn);
        femaleRadioBtn = (RadioButton) findViewById(R.id.femaleRadioBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        maleRadioBtn.setChecked(true);
        changeGenderFunc();
        signUpUserNameView.setText("", TextView.BufferType.EDITABLE);
        signUpEmailView.setText("qwer1234@naver.com", TextView.BufferType.EDITABLE);
        signUpPawd1View.setText("qwer1234", TextView.BufferType.EDITABLE);
        signUpPawd2View.setText("qwer1234", TextView.BufferType.EDITABLE);

        /*signUpAgeView.setText("1992", TextView.BufferType.EDITABLE);*/
        signUpNameView.setText("심민호", TextView.BufferType.EDITABLE);
        /*signUpGenderView.setText("Man", TextView.BufferType.EDITABLE);*/

        /*loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });*/

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SPINNERLIST);

        android_material_design_spinner = (MaterialSpinner) findViewById(R.id.android_material_design_spinner);
        android_material_design_spinner.setAdapter(adapter);
        android_material_design_spinner.setHint("출생연도");

        android_material_design_spinner.setSelection(20);



        /*adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter);

        Editable good = materialDesignSpinner.getText();
        String name= null;*/

        /*materialDesignSpinner.setSelection(2);*/
    }

    public void startSignup(View arg0) {

        username = signUpUserNameView.getText().toString();
        email = signUpEmailView.getText().toString();
        password1 = signUpPawd1View.getText().toString();
        password2 = signUpPawd2View.getText().toString();
        birth = android_material_design_spinner.getSelectedItem().toString();
        /*birth = signUpAgeView.getText().toString();*/
        name = signUpNameView.getText().toString();

        String first_name = "";
        String last_name = "";
        //이름 성과 이름으로 나눔

        if(name.length() >= 1) {
            first_name = name.substring(0, 1);
            last_name = name.substring(1, name.length());
        }

        new AsyncSignUp().execute(username, email, password1, password2, birth
                , first_name, last_name, gender);

    }

    private class AsyncSignUp extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(SignUpActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {  //Background 작업 시작전에 UI 작업을 진행
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {  //Background 작업을 진행
            try {

                // Enter URL address where your php file resides

                url = new URL("http://113.198.84.37/rest-auth/registration/");


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
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("password1", params[2])
                        .appendQueryParameter("password2", params[3])
                        .appendQueryParameter("birth", params[4])
                        .appendQueryParameter("first_name", params[5])
                        .appendQueryParameter("last_name", params[6])
                        .appendQueryParameter("gender", params[7]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                Log.d("SignUpActivityLog", "SignUpActivityLog 0-1 : " + query);

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

                int response_code = conn.getResponseCode();

                Log.d("SignUpActivityLog", "SignUpActivityLog 1-0 : " + conn.getResponseCode());

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_CREATED) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("SignUpActivityLog", "SignUpActivityLog 1-1 : " + result.toString());

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("SignUpActivityLog", "SignUpActivityLog 1-2 : " + result.toString());
                    Log.d("SignUpActivityLog", "SignUpActivityLog 1-3 : " + reader);
                    // Pass data to onPostExecute method
                    // Read data sent from server

                    return("successful");

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

                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-2 : " + errorResult.toString());

                    if(serverJsonValue.contains("username"))
                        errorUsername = serverJsonObject.getString("username");

                    if(serverJsonValue.contains("email"))
                        errorEmail = serverJsonObject.getString("email");

                    if(serverJsonValue.contains("password1"))
                        errorPassword1 = serverJsonObject.getString("password1");

                    if(serverJsonValue.contains("password2"))
                        errorPassword2 = serverJsonObject.getString("password2");

                    if(serverJsonValue.contains("birth"))
                        errorAge = serverJsonObject.getString("birth");

                    if(serverJsonValue.contains("first_name"))
                        errorName = serverJsonObject.getString("first_name");

                    if(serverJsonValue.contains("last_name"))
                        errorName = serverJsonObject.getString("last_name");

                    if(serverJsonValue.contains("gender"))
                        errorGender = serverJsonObject.getString("gender");

                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-3 : " + errorUsername);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-4 : " + errorEmail);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-5 : " + errorPassword1);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-6 : " + errorPassword2);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-7 : " + errorAge);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-8 : " + errorName);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-9 : " + errorGender);

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
        protected void onPostExecute(String result) {  //Background 작업이 끝난 후 UI 작업을 진행

            Log.d("SignUpActivityLog", "SignUpActivityLog 3-0 : " + result);
            //this method will be running on UI thread

            pdLoading.dismiss();


            //예외 처리 부분----------------------------------------------------------------------------------------
            if (!validate()) {
                return;
            }

            if(result.equals("successful")) {
                Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                SignUpActivity.this.finish();
            }

            else {
                Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
            }

            //예외 처리 부분----------------------------------------------------------------------------------------






        }

    }
/*
    private EditText signUpUserNameView;
    private EditText signUpEmailView;
    private EditText signUpPawd1View;
    private EditText signUpPawd2View;
    private EditText signUpAgeView;
    private EditText signUpNameView;
    private EditText signUpGenderView;
*/
/*
    String username;
    String email;
    String password1;
    String password2;
    String age;
    String name;
    String gender;
    */
    public boolean validate() {
        boolean valid = true;

        if (username.isEmpty()) {
            signUpUserNameView.setError("enter a valid username");
            valid = false;
        } else {
            signUpUserNameView.setError(null);
        }

        if (email.isEmpty()) {
            signUpEmailView.setError("enter a valid email");
            valid = false;
        } else {
            signUpEmailView.setError(null);
        }

        if (password1.isEmpty()) {
            signUpPawd1View.setError("enter a valid password");
            valid = false;
        } else {
            signUpPawd1View.setError(null);
        }

        if (password2.isEmpty()) {
            signUpPawd2View.setError("enter a valid confirm password");
            valid = false;
        } else {
            signUpPawd2View.setError(null);
        }

        if (birth.isEmpty()) {
            signUpAgeView.setError("enter a valid age");
            valid = false;
        } else {
            signUpAgeView.setError(null);
        }

        if (name.isEmpty()) {
            signUpNameView.setError("enter a valid name");
            valid = false;
        } else {
            signUpNameView.setError(null);
        }

        if (gender.isEmpty()) {
            signUpGenderView.setError("enter a valid gender");
            valid = false;
        } else {
            signUpGenderView.setError(null);
        }


        return valid;
    }

   /* public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        loginBtn.setEnabled(true);
    }
*/


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
