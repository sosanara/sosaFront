package com.example.shim.sosafront.GalleryPackage;

import android.app.Activity;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.GalleryPackage.GalleryNextPagePackage.GalleryItemClickActivity;
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
import java.util.Collections;
import java.util.Iterator;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GalleryActivity extends AppCompatActivity {

    private LinearLayout networkCheckLayout;

    private static final String IP_ADDRESS = "http://113.198.84.37/";
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    final private ArrayList<String> Index = new ArrayList<String>();
    final private ArrayList<String> galleryIndex = new ArrayList<String>();
    final private ArrayList<String> galleryImage = new ArrayList<String>();
    final private ArrayList<String> galleryCreateDate = new ArrayList<String>();

    private RecyclerView list;
    private GalleryAdapter mAdapter;

    Activity act = GalleryActivity.this;

    String authKey;
    DataStore dataStore;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

        networkCheckLayout = (LinearLayout)findViewById(R.id.networkCheckLayout);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        //글씨체 통일
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        Drawable home = getResources().getDrawable(R.drawable.toolbar_home);
        Drawable resizeHome = resize(home);
        toolbar.setNavigationIcon(resizeHome);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        new AsyncGallery().execute();
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


    public void onItemClick(int mPosition) {
        String tempValues = galleryIndex.get(mPosition);
        String tempKeys = galleryImage.get(mPosition);

        Intent goItemClickActivity = new Intent(this, GalleryItemClickActivity.class);
        goItemClickActivity.putExtra("url", tempKeys);
        finish();
        startActivity(goItemClickActivity);

    }


    //Server Connect class
    private class AsyncGallery extends AsyncTask<String, String, String> {
        //MaterialDialog dialog; 소사 다이어로그
        ProgressDialog pdLoading = new ProgressDialog(GalleryActivity.this);
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

            //소사 다이어로그
            /*LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.dialog_loading, null);
            GifView gifView = (GifView) view.findViewById(R.id.loading);
            gifView.setVisibility(View.VISIBLE);
            dialog = new MaterialDialog.Builder(act).customView(view, true).build();
            dialog.setCancelable(false);
            dialog.show();*/
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Enter URL address where your php file resides
                url = new URL(IP_ADDRESS + "api/v1/userInfo/gallery/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("GalleryLog", "GalleryLog Connection Test : 접근실패");
                return "exception";
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", " Token " + authKey);
                /*conn.setRequestProperty("Authorization", " Token 02d5b931870caedc2ce683fcad66e42040447e5e");*/


                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
//                conn.setDoOutput(true);   //이거땜에 GET이 POST로 바뀜

                conn.connect();


            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                //Data를 받음
                int response_code = conn.getResponseCode();

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);    //서버에서 온 데이터를 계속 붙임
                    }

                    //JSONObject jsonObject = new JSONObject(value);

                    String value = result.toString();
                    JSONObject serverJsonObject = new JSONObject(value);
                    String serverJsonValue = serverJsonObject.getString("value").toString();

                    JSONObject firstJsonObject = new JSONObject(String.valueOf(serverJsonObject.get("value")));

                    Iterator<String> keys = firstJsonObject.keys();



                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                        String galleryLogAddressValue = IP_ADDRESS + "api/v1/userInfo/gallery/" + key;

                        Log.d("GalleryLog", "GalleryLog 2-0 : " + key);
                        Log.d("GalleryLog", "GalleryLog 2-1 : " + galleryLogAddressValue);

                        JSONObject secondJsonObject = new JSONObject(String.valueOf(firstJsonObject.get(key)));

                        String pictureImagePath = IP_ADDRESS  + secondJsonObject.getString("origin_image").toString();
                        String createData = secondJsonObject.getString("created_data").toString();
                        Log.d("GalleryLog", "GalleryLog 2-2 image path : " + pictureImagePath);

                        Index.add(key);
                        galleryIndex.add(pictureImagePath);
                        galleryImage.add(galleryLogAddressValue);
                        galleryCreateDate.add(createData);
                    }

                    //Index -> historyIndex, historyIndex -> histroydetailPage 변수명 수정

                    //히스토리 시간 오름차순 정렬
                    String ascendSortTemp;

                    for (int i = 0; i < Index.size(); i++) {
                        for (int j = i + 1; j < Index.size(); j++) {
                            if (Integer.valueOf(Index.get(i)) > Integer.valueOf(Index.get(j))) {
                                ascendSortTemp = Index.get(i);
                                Index.set(i, (Index.get(j)));
                                Index.set(j, ascendSortTemp);

                                ascendSortTemp = galleryIndex.get(i);
                                galleryIndex.set(i, (galleryIndex.get(j)));
                                galleryIndex.set(j, ascendSortTemp);

                                ascendSortTemp = galleryImage.get(i);
                                galleryImage.set(i, (galleryImage.get(j)));
                                galleryImage.set(j, ascendSortTemp);

                                ascendSortTemp = galleryCreateDate.get(i);
                                galleryCreateDate.set(i, (galleryCreateDate.get(j)));
                                galleryCreateDate.set(j, ascendSortTemp);

                            }
                        }
                    }
                    Collections.reverse(Index);
                    Collections.reverse(galleryIndex);
                    Collections.reverse(galleryImage);
                    Collections.reverse(galleryCreateDate);

                    for(int i = 0; i < galleryIndex.size(); i++) {
                        Log.d("GalleryLog", "GalleryLog 2-5 : " + Index.get(i));
                        Log.d("GalleryLog", "GalleryLog 2-6 : " + galleryIndex.get(i));
                       /* Log.d("HistoryActivityLog", "HistoryActivityLog 2-6 : " + historyImage.get(i));
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-7 : " + historyCreateDate.get(i));*/
                    }



                    return (result.toString());

                } else {
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } catch (JSONException e) {
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();

            list = (RecyclerView) findViewById(R.id.list);
            ArrayList<String> changeDate = new ArrayList<String>();
            for(int i=0 ; i < galleryCreateDate.size() ; i++) {
                String buf[] = galleryCreateDate.get(i).split("-");
                String temp = buf[0]+"."+buf[1]+"."+buf[2];
                changeDate.add(temp.substring(0,10));
            }
            mAdapter = new GalleryAdapter(act, galleryIndex, changeDate);
            GridLayoutManager gManager = new GridLayoutManager(act, 3);


            list.setLayoutManager(gManager);
            list.setAdapter(mAdapter);

        }
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 96, 77, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}