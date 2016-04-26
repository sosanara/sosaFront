package com.example.shim.sosafront.StatisticPackage;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;
import com.example.shim.sosafront.DatabasePackage.DataStore;
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
import java.util.ArrayList;

public class StatisticActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    DataStore dataStore;
    String authKey;

    private ArrayList<String> typeIndex = new ArrayList<String>();
    private ArrayList<String> ageIndex = new ArrayList<String>();
    private ArrayList<String> genderIndex = new ArrayList<String>();

    TypeUserFragment typeUserFragment = new TypeUserFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);

        new AsyncStatistic().execute();

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");


        /*// Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new StatisticFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);



        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip


        tabsStrip.setViewPager(viewPager);*/




    }

    private class AsyncStatistic extends AsyncTask<String, String, String>
    {
        /* ProgressDialog pdLoading = new ProgressDialog(GenderFragment.this);*/
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {  //작업처리중' 프로그레스 다이얼로그 자동 시작
            super.onPreExecute();

            //this method will be running on UI thread
           /* pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();*/

        }
        @Override
        protected String doInBackground(String... params) {  //프로그레스 다이얼로그 자동 종료 및 에러메시지 토스트보여줌
            //doInBackground()에서 에러발생시 하위 클래스의 onPostExecute()는 실행되지 않음
            try {
                // Enter URL address where your php file resides
                url = new URL("http://113.198.84.37/api/v1/userInfo/statistic/");
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception fail!";
            }

            try {

                authKey = "710f1c4a9d21686bb5d2b73a9cd43546a1184cf5";

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", " Token " + authKey);

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                /*conn.setDoOutput(true);*/

                // Append parameters to URL

                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                //여기서 로그인 페이지로 이동
                int response_code = conn.getResponseCode();

                Log.d("StatisticActivity", "StatisticActivity 0-0: " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("StatisticActivity", "StatisticActivity 1: " + result.toString());
                    Log.d("StatisticActivity", "StatisticActivity 1-1: " + reader);

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("StatisticActivity", "StatisticActivity 2: " + result.toString());  // result.toString()
                    Log.d("StatisticActivity", "StatisticActivity 2-1: " + reader);    //java.io.BufferedReader@5015f88

                    String value = result.toString();
                    JSONObject valueJsonObject = new JSONObject(value);

                    Log.d("StatisticActivity", "StatisticActivity 2-2: " + valueJsonObject.get("value"));

                    JSONObject subTypeJsonObject = new JSONObject(String.valueOf(valueJsonObject.get("value")));
                    JSONObject allUserTypeJsonObjectKey = new JSONObject(String.valueOf(subTypeJsonObject.get("all_users_type")));
                    JSONObject sameAgeTypeJsonObjectKey = new JSONObject(String.valueOf(subTypeJsonObject.get("same_ages_type")));
                    JSONObject genderUsersTypeJsonObjectKey = new JSONObject(String.valueOf(subTypeJsonObject.get("gender_users_type")));

                    Log.d("StatisticActivity", "StatisticActivity 2-3: " + subTypeJsonObject.get("all_users_type"));

                    typeIndex.add(0, String.valueOf(allUserTypeJsonObjectKey.get("0")));
                    typeIndex.add(1, String.valueOf(allUserTypeJsonObjectKey.get("1")));
                    typeIndex.add(2, String.valueOf(allUserTypeJsonObjectKey.get("2")));
                    typeIndex.add(3, String.valueOf(allUserTypeJsonObjectKey.get("3")));
                    typeIndex.add(4, String.valueOf(allUserTypeJsonObjectKey.get("4")));

                    ageIndex.add(0, String.valueOf(sameAgeTypeJsonObjectKey.get("0")));
                    ageIndex.add(1, String.valueOf(sameAgeTypeJsonObjectKey.get("1")));
                    ageIndex.add(2, String.valueOf(sameAgeTypeJsonObjectKey.get("2")));
                    ageIndex.add(3, String.valueOf(sameAgeTypeJsonObjectKey.get("3")));
                    ageIndex.add(4, String.valueOf(sameAgeTypeJsonObjectKey.get("4")));

                    genderIndex.add(0, String.valueOf(genderUsersTypeJsonObjectKey.get("0")));
                    genderIndex.add(1, String.valueOf(genderUsersTypeJsonObjectKey.get("1")));
                    genderIndex.add(2, String.valueOf(genderUsersTypeJsonObjectKey.get("2")));
                    genderIndex.add(3, String.valueOf(genderUsersTypeJsonObjectKey.get("3")));
                    genderIndex.add(4, String.valueOf(genderUsersTypeJsonObjectKey.get("4")));

                    for(int i = 0; i < 5; i++) {
                        Log.d("StatisticActivity", "StatisticActivity 2-5: type: " + typeIndex.get(i));
                        Log.d("StatisticActivity", "StatisticActivity 2-5: age" + ageIndex.get(i));
                        Log.d("StatisticActivity", "StatisticActivity 2-5: gender" + genderIndex.get(i));
                    }

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



            Intent i = new Intent(StatisticActivity.this, TypeUserFragment.class);
            i.putExtra("GOOD", "GOOD");


            // Get the ViewPager and set it's PagerAdapter so that it can display items
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new StatisticFragmentPagerAdapter(getSupportFragmentManager()));
            viewPager.setCurrentItem(0);
            viewPager.setOffscreenPageLimit(2);



            // Give the PagerSlidingTabStrip the ViewPager
            PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            // Attach the view pager to the tab strip


            tabsStrip.setViewPager(viewPager);

        }

    }
}
