package com.example.shim.sosafront.StatisticPackage;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.DialogPackage.TypeDialog;
import com.example.shim.sosafront.MainPackage.MainActivity;
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

    private LinearLayout networkCheckLayout;
    Toolbar toolbar;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    DataStore dataStore;
    String authKey;


    private ArrayList<String> typeIndex = new ArrayList<String>();
    private ArrayList<String> typePercentString = new ArrayList<String>();
    private ArrayList<String> ageIndex = new ArrayList<String>();
    private ArrayList<String> agePercentString = new ArrayList<String>();

    private ViewPager.OnPageChangeListener mPageChangeListener;

    TypeUserFragment typeUserFragment = new TypeUserFragment();

    private int typeSum = 0;
    private int ageSum = 0;

    private LinearLayout typeImgLayout;
    private LinearLayout ageImgLayout;
    private TextView typeOneView;
    private TextView typeTwoView;
    private TextView typeThreeView;
    private TextView typeFourView;
    private TextView typeFiveView;
    private TextView ageOneView;
    private TextView ageTwoView;
    private TextView ageThreeView;
    private TextView ageFourView;
    private TextView ageFiveView;
    private TextView ageSixView;

    private LinearLayout typeLayoutOne;

    private LinearLayout ageLayoutOne;
    private LinearLayout ageLayoutTwo;
    private LinearLayout ageLayoutThree;
    private LinearLayout ageLayoutFour;
    private LinearLayout ageLayoutFive;
    private LinearLayout ageLayoutSix;

    TypeDialog typeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        toolbar = (Toolbar) findViewById(R.id.toolBar);

        networkCheckLayout = (LinearLayout)findViewById(R.id.networkCheckLayout);
        typeImgLayout = (LinearLayout) findViewById(R.id.typeImgLayout);
        ageImgLayout = (LinearLayout) findViewById(R.id.ageImgLayout);

        typeOneView = (TextView) findViewById(R.id.typeOneView);
        typeTwoView = (TextView) findViewById(R.id.typeTwoView);
        typeThreeView = (TextView) findViewById(R.id.typeThreeView);
        typeFourView = (TextView) findViewById(R.id.typeFourView);
        typeFiveView = (TextView) findViewById(R.id.typeFiveView);
        ageOneView = (TextView) findViewById(R.id.ageOneView);
        ageTwoView = (TextView) findViewById(R.id.ageTwoView);
        ageThreeView = (TextView) findViewById(R.id.ageThreeView);
        ageFourView = (TextView) findViewById(R.id.ageFourView);
        ageFiveView = (TextView) findViewById(R.id.ageFiveView);
        ageSixView= (TextView) findViewById(R.id.ageSixView);

        ageLayoutOne = (LinearLayout) findViewById(R.id.ageLayoutOne);
        ageLayoutTwo = (LinearLayout) findViewById(R.id.ageLayoutTwo);
        ageLayoutThree = (LinearLayout) findViewById(R.id.ageLayoutThree);
        ageLayoutFour = (LinearLayout) findViewById(R.id.ageLayoutFour);
        ageLayoutFive = (LinearLayout) findViewById(R.id.ageLayoutFive);
        ageLayoutSix = (LinearLayout) findViewById(R.id.ageLayoutSix);

        typeLayoutOne = (LinearLayout) findViewById(R.id.typeLayoutOne);


        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

        Drawable home = getResources().getDrawable(R.drawable.toolbar_home);
        Drawable resizeHome = resize(home);
        toolbar.setNavigationIcon(resizeHome);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new StatisticFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(0);
        /*viewPager.setOnPageChangeListener(mPageChangeListener);
*/
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg) {
                // TODO Auto-generated method stub
                if (arg == 0) {
                    Log.d("StatisticLog", "StatisticLog 1-0 : " + "현재 0페이지");
                    typeImgLayout.setVisibility(View.VISIBLE);
                    ageImgLayout.setVisibility(View.GONE);
                } else {
                    Log.d("StatisticLog", "StatisticLog 1-1 : " + "현재 1페이지");
                    typeImgLayout.setVisibility(View.GONE);
                    ageImgLayout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });



        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

        /*String typePercentOne = getIntent().getStringExtra("typePercentOne");
        Log.d("StatisticLog", "StatisticLog 2-1 : " + typePercentOne);*/

        new AsyncStatistic().execute();
        typeDialog = new TypeDialog(this);

        ageLayoutOne.setOnTouchListener(typeTouch);
        typeLayoutOne.setOnTouchListener(typeTouch);

        /*ageLayoutOne.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                typeDialog.show();
                return false;
            }
        });*/
    }


    private class AsyncStatistic extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(StatisticActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {  //작업처리중' 프로그레스 다이얼로그 자동 시작
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
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", " Token " + authKey);

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);

                // Append parameters to URL

                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

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

                    String value = result.toString();
                    JSONObject valueJsonObject = new JSONObject(value);

                    Log.d("TypeUserFragmentLog", "TypeUserFragmentLog 2-2: " + valueJsonObject.get("value"));

                    JSONObject subTypeJsonObject = new JSONObject(String.valueOf(valueJsonObject.get("value")));
                    JSONObject allUserTypeJsonObjectKey = new JSONObject(String.valueOf(subTypeJsonObject.get("type")));
                    JSONObject sameAgeTypeJsonObjectKey = new JSONObject(String.valueOf(subTypeJsonObject.get("ages")));

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
                    ageIndex.add(5, String.valueOf(sameAgeTypeJsonObjectKey.get("5")));


                    for(int i = 0; i<typeIndex.size(); i++) {
                        typeSum += Integer.parseInt(typeIndex.get(i));
                    }

                    for(int i = 0; i<ageIndex.size(); i++) {
                        ageSum += Integer.parseInt(ageIndex.get(i));
                    }

                    Log.d("StatisticLog", "StatisticLog 테스트 3-1 : 페이지 = " + typeSum);

                    for(int i = 0; i<typeIndex.size(); i++) {
                        typePercentString.add(i, String.valueOf(Float.parseFloat(typeIndex.get(i)) / typeSum * 100));
                        Log.d("StatisticLog", "StatisticLog 테스트 3-2 : 페이지 = " + typePercentString.get(i));
                    }

                    for(int i = 0; i<ageIndex.size(); i++) {
                        agePercentString.add(i, String.valueOf(Float.parseFloat(ageIndex.get(i)) / ageSum * 100));
                        Log.d("StatisticLog", "StatisticLog 테스트 3-2 : 페이지 = " + agePercentString.get(i));
                    }

                    Log.d("StatisticLog", "StatisticLog 2-1: ");


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
            }finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            typeOneView.setText(typePercentString.get(0)+"%");
            typeTwoView.setText(typePercentString.get(1)+"%");
            typeThreeView.setText(typePercentString.get(2)+"%");
            typeFourView.setText(typePercentString.get(3)+"%");
            typeFiveView.setText(typePercentString.get(4)+"%");

            ageOneView.setText(agePercentString.get(0)+"%");
            ageTwoView.setText(agePercentString.get(1)+"%");
            ageThreeView.setText(agePercentString.get(2)+"%");
            ageFourView.setText(agePercentString.get(3)+"%");
            ageFiveView.setText(agePercentString.get(4)+"%");
            ageSixView.setText(agePercentString.get(5)+"%");

            pdLoading.dismiss();
        }

    }



   private View.OnTouchListener typeTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            typeDialog.show();

            return false;
        }

    };

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 96, 77, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}
