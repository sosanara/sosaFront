package com.example.shim.sosafront.LoginPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

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

import butterknife.Bind;
import fr.ganfra.materialspinner.MaterialSpinner;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SignUpActivity extends AppCompatActivity {

    private LinearLayout networkCheckLayout;
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private String username;
    private String email;
    private String password1;
    private String password2;
    private String birth;
    private String name;
    private String gender;

    private Spinner ageSpinner;

    private LinearLayout wholeLayout;

    private String errorUsername;
    private String errorEmail;
    private String errorPassword1;
    private String errorPassword2;
    private String errorAge;
    private String errorFirstName;
    private String errorLastName;
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
    @Bind(R.id.signUpNameView) EditText signUpNameView;
    /*@Bind(R.id.signUpGenderView) EditText signUpGenderView;*/
    @Bind(R.id.loginBtn) Button loginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        //글씨체 통일
       CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                       .setDefaultFontPath("")
                       .setFontAttrId(R.attr.fontPath)
                       .build()
       );

        wholeLayout = (LinearLayout) findViewById(R.id.wholeLayout);
        networkCheckLayout = (LinearLayout)findViewById(R.id.networkCheckLayout);
        signUpUserNameView = (EditText) findViewById(R.id.signUpUserNameView);
        signUpEmailView = (EditText) findViewById(R.id.signUpEmailView);
        signUpPawd1View = (EditText) findViewById(R.id.signUpPawd1View);
        signUpPawd2View = (EditText) findViewById(R.id.signUpPawd2View);
        signUpNameView = (EditText) findViewById(R.id.signUpNameView);
        /*signUpGenderView= (EditText) findViewById(R.id.signUpGenderView);*/
        ageSpinner = (Spinner) findViewById(R.id.ageSpinner);

        genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
        maleRadioBtn = (RadioButton) findViewById(R.id.maleRadioBtn);
        femaleRadioBtn = (RadioButton) findViewById(R.id.femaleRadioBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        maleRadioBtn.setChecked(true);
        changeGenderFunc();
        /*signUpUserNameView.setText("", TextView.BufferType.EDITABLE);
        signUpEmailView.setText("qwer1234@naver.com", TextView.BufferType.EDITABLE);
        signUpPawd1View.setText("qwer1234", TextView.BufferType.EDITABLE);
        signUpPawd2View.setText("qwer1234", TextView.BufferType.EDITABLE);*/

        /*signUpAgeView.setText("1992", TextView.BufferType.EDITABLE);*/
        /*signUpNameView.setText("심민호", TextView.BufferType.EDITABLE);*/
        /*signUpGenderView.setText("Man", TextView.BufferType.EDITABLE);*/


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



    public void startSignup(View arg0) {

        username = signUpUserNameView.getText().toString();
        email = signUpEmailView.getText().toString();
        password1 = signUpPawd1View.getText().toString();
        password2 = signUpPawd2View.getText().toString();
        birth = ageSpinner.getSelectedItem().toString();
        /*birth = android_material_design_spinner.getSelectedItem().toString();*/
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
                        errorFirstName = serverJsonObject.getString("first_name");

                    if(serverJsonValue.contains("last_name"))
                        errorLastName = serverJsonObject.getString("last_name");

                    if(serverJsonValue.contains("gender"))
                        errorGender = serverJsonObject.getString("gender");

                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-3 : " + errorUsername);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-4 : " + errorEmail);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-5 : " + errorPassword1);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-6 : " + errorPassword2);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-7 : " + errorAge);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-8 : " + errorFirstName);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-9 : " + errorLastName);
                    Log.d("SignUpActivityLog", "SignUpActivityLog 2-10 : " + errorGender);

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


            if(result.equals("successful")) {
                finish();
            }

            else {

                if (!validate()) {
                    return;
                }
                /*Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();*/
            }

            //예외 처리 부분----------------------------------------------------------------------------------------






        }

    }

 boolean validate() {
        boolean valid = true;

        //여기에 바뀐 style 다시적용
        if(TextUtils.isEmpty(errorUsername)) {
            signUpUserNameView.setBackgroundResource(R.drawable.text_border);
            signUpUserNameView.setError(null);
        } else if (errorUsername.contains("This field may not be blank")) {
            signUpUserNameView.setBackgroundResource(R.drawable.text_border_green);
            signUpUserNameView.setText("");
            signUpUserNameView.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpUserNameView.setHint("아이디를 입력해 주시기 바랍니다.");
            valid = false;
        } else if (errorUsername.contains("only contain letters, digits")){
            signUpUserNameView.setBackgroundResource(R.drawable.text_border_green);
            signUpUserNameView.setText("");
            signUpUserNameView.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpUserNameView.setHint("공백 및 특수문자는 사용하실 수 없습니다.");
            valid = false;
        } else if (errorUsername.contains("Ensure this field") || errorUsername.contains("minimum")){
            signUpUserNameView.setBackgroundResource(R.drawable.text_border_green);
            signUpUserNameView.setText("");
            signUpUserNameView.setText("5~30자리로 입력 하시기 바랍니다.");
            signUpUserNameView.setHintTextColor(Color.parseColor("#2eb74f"));;
            signUpUserNameView.setHint("5~30자리로 입력 하시기 바랍니다.");
            valid = false;
        } else if (errorUsername.contains("This username is already taken")){
            signUpUserNameView.setBackgroundResource(R.drawable.text_border_green);
            signUpUserNameView.setText("");
            signUpUserNameView.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpUserNameView.setHint("이미 사용 중인 아이디입니다.");
            valid = false;
        } else if (errorUsername.contains("Username can not be used. Please use other username")){
            signUpUserNameView.setBackgroundResource(R.drawable.text_border_green);
            signUpUserNameView.setText("");
            signUpUserNameView.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpUserNameView.setHint("블랙리스트로 등록된 아이디입니다.");
            valid = false;
        } else if (errorUsername.contains("Too many failed login attempts.")){
            signUpUserNameView.setBackgroundResource(R.drawable.text_border_green);
            signUpUserNameView.setText("");
            signUpUserNameView.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpUserNameView.setHint("현재 너무 많은 로그인 시도로 인해 나중에 다시 시도하시기 바랍니다.");
            valid = false;
        } else {
            signUpUserNameView.setBackgroundResource(R.drawable.text_border_green);
            signUpUserNameView.setText("");
            signUpUserNameView.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpUserNameView.setHint("잘못된 아이디를 입력하였습니다.");
        }

         if (TextUtils.isEmpty(errorEmail)) {
             signUpEmailView.setBackgroundResource(R.drawable.text_border);
             signUpEmailView.setError(null);
         } else if (errorEmail.contains("This field may not be blank")) {
             signUpEmailView.setBackgroundResource(R.drawable.text_border_green);
             signUpEmailView.setText("");
             signUpEmailView.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpEmailView.setHint("이메일을 입력해 주세요.");
            valid = false;
        } else if (errorEmail.contains("Enter a valid email address")){
             signUpEmailView.setBackgroundResource(R.drawable.text_border_green);
             signUpEmailView.setText("");
             signUpEmailView.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpEmailView.setHint("올바른 이메일 형식이 아닙니다.");
            valid = false;
        } else if (errorEmail.contains("A user is already registered with this e-mail address")){
             signUpEmailView.setBackgroundResource(R.drawable.text_border_green);
             signUpEmailView.setText("");
             signUpEmailView.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpEmailView.setHint("이미 사용 중인 이메일 주소입니다.");
            valid = false;
        } else {
             signUpEmailView.setBackgroundResource(R.drawable.text_border_green);
             signUpEmailView.setText("");
             signUpEmailView.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpEmailView.setHint("다른 이메일을 입력해 주세요.");
        }

         if (TextUtils.isEmpty(errorPassword1)) {
             signUpPawd1View.setBackgroundResource(R.drawable.text_border);
             signUpPawd1View.setError(null);
         } else if (errorPassword1.contains("This field may not be blank")) {
             signUpPawd1View.setBackgroundResource(R.drawable.text_border_green);
             signUpPawd1View.setText("");
             signUpPawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpPawd1View.setHint("비밀번호를 입력해 주세요.");
            valid = false;
        } else if (errorPassword1.contains("The two password fields didn't match")) {
             signUpPawd1View.setBackgroundResource(R.drawable.text_border_green);
             signUpPawd1View.setText("");
             signUpPawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpPawd1View.setHint("비밀번호가 일치하지 않습니다.");
            valid = false;
        } else if (errorPassword1.contains("Password must be a minimum of 8 characters.")) {
             signUpPawd1View.setBackgroundResource(R.drawable.text_border_green);
             signUpPawd1View.setText("");
             signUpPawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
             signUpPawd1View.setHint("비밀번호는 8자리 이상으로 입력해주세요.");
            valid = false;
        } else {
             signUpPawd1View.setBackgroundResource(R.drawable.text_border_green);
             signUpPawd1View.setText("");
             signUpPawd1View.setHintTextColor(Color.parseColor("#2eb74f"));
             signUpPawd2View.setHint("다른 비밀번호를 입력해 주세요");
             valid = false;
        }

        if (TextUtils.isEmpty(errorPassword2)) {
            signUpPawd2View.setBackgroundResource(R.drawable.text_border);
            signUpPawd2View.setError(null);
        } else if (errorPassword2.contains("This field may not be blank")) {
            signUpPawd2View.setBackgroundResource(R.drawable.text_border_green);
            signUpPawd2View.setText("");
            signUpPawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpPawd2View.setHint("비밀번호를 입력해 주세요.");
            valid = false;
        } else if (errorPassword2.contains("The two password fields didn't match")) {
            signUpPawd2View.setBackgroundResource(R.drawable.text_border_green);
            signUpPawd2View.setText("");
            signUpPawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpPawd2View.setHint("비밀번호가 일치하지 않습니다.");
            valid = false;
        } else if (errorPassword2.contains("Password must be a minimum of 8 characters.")) {
            signUpPawd2View.setBackgroundResource(R.drawable.text_border_green);
            signUpPawd2View.setText("");
            signUpPawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpPawd2View.setHint("비밀번호는 8자리 이상으로 입력해주세요.");
            valid = false;
        } else {
            signUpPawd2View.setBackgroundResource(R.drawable.text_border_green);
            signUpPawd2View.setText("");
            signUpPawd2View.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpPawd2View.setHint("다른 비밀번호를 입력해 주세요.");
            valid = false;
        }


        /*if (birth.isEmpty()) {
            signUpAgeView.setError("enter a valid age");
            valid = false;
        } else {
            signUpAgeView.setError(null);
        }
*/

         if(TextUtils.isEmpty(errorFirstName)){
             signUpNameView.setBackgroundResource(R.drawable.text_border);
             signUpNameView.setError(null);
         } else if (errorFirstName.contains("blank")) {
             signUpNameView.setBackgroundResource(R.drawable.text_border_green);
             signUpNameView.setText("");
             signUpNameView.setHintTextColor(Color.parseColor("#2eb74f"));
            signUpNameView.setHint("이름을 입력해 주세요.");
            valid = false;
        } else {
             signUpNameView.setBackgroundResource(R.drawable.text_border_green);
             signUpNameView.setText("");
             signUpNameView.setHintTextColor(Color.parseColor("#2eb74f"));
             signUpNameView.setHint("다른 이름을 입력해 주세요.");
             valid = false;
        }

     if(TextUtils.isEmpty(errorLastName)){
         signUpNameView.setBackgroundResource(R.drawable.text_border);
         signUpNameView.setError(null);
     } else if (errorLastName.contains("blank")) {
         signUpNameView.setBackgroundResource(R.drawable.text_border_green);
         signUpNameView.setText("");
         signUpNameView.setHintTextColor(Color.parseColor("#2eb74f"));
         signUpNameView.setHint("이름을 입력해 주세요.");
         valid = false;
     } else {
         signUpNameView.setBackgroundResource(R.drawable.text_border_green);
         signUpNameView.setText("");
         signUpNameView.setHintTextColor(Color.parseColor("#2eb74f"));
         signUpNameView.setHint("다른 이름을 입력해 주세요.");
         valid = false;
     }



        /*if (errorGender.isEmpty()) {
           *//* signUpGenderView.setError("enter a valid gender");*//*
            valid = false;
        } else {
            *//*signUpGenderView.setError(null);*//*
        }*/


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

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
