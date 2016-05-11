package com.example.shim.sosafront.HistoryPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.HistoryPackage.HistoryNextPagePakage.HistoryItemClickActivity;
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

public class HistoryActivity extends Activity {

    private static final String IP_ADDRESS = "http://113.198.84.37/";
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

//    private RecyclerView mRecyclerView;
//    private HistoryAdapter mAdapter;

    /*private ArrayList<String> key = new ArrayList<String>();
    private ArrayList<String> val = new ArrayList<String>();*/
    final private ArrayList<String> Index = new ArrayList<String>();
    final private ArrayList<String> historyIndex = new ArrayList<String>();
    final private ArrayList<String> historyImage = new ArrayList<String>();
    final private ArrayList<String> historyCreateDate = new ArrayList<String>();

//    public String val[] = new String[0];

    private ListView list;
    private HistoryAdapter mAdapter;

    Activity act = HistoryActivity.this;

    String authKey;
    DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

        new AsyncHistory().execute();

    }


    public void onItemClick(int mPosition) {
        String tempValues = historyIndex.get(mPosition);
        String tempKeys = historyImage.get(mPosition);

//        String tempValues = val[mPosition];
        Toast.makeText(HistoryActivity.this,
                "Image URL : " + tempKeys,
                Toast.LENGTH_LONG)
                .show();

        Intent goItemClickActivity = new Intent(this, HistoryItemClickActivity.class);
        goItemClickActivity.putExtra("url", tempKeys);
        startActivity(goItemClickActivity);

    }


    //Server Connect class
    private class AsyncHistory extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(HistoryActivity.this);
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
                url = new URL(IP_ADDRESS + "api/v1/userInfo/history/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("HistoryActivity", "HistoryActivity Connection Test : 접근실패");
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
                        String historyAddressValue = IP_ADDRESS + "api/v1/userInfo/history/" + key;

                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-0 : " + key);
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-1 : " + historyAddressValue);

                        JSONObject secondJsonObject = new JSONObject(String.valueOf(firstJsonObject.get(key)));

                        String pictureImagePath = IP_ADDRESS  + secondJsonObject.getString("image").toString();
                        String createData = secondJsonObject.getString("created_data").toString();
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-2 image path : " + pictureImagePath);

                        Index.add(key);
                        historyIndex.add(pictureImagePath);
                        historyImage.add(historyAddressValue);
                        historyCreateDate.add(createData);
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

                                ascendSortTemp = historyIndex.get(i);
                                historyIndex.set(i, (historyIndex.get(j)));
                                historyIndex.set(j, ascendSortTemp);

                                ascendSortTemp = historyImage.get(i);
                                historyImage.set(i, (historyImage.get(j)));
                                historyImage.set(j, ascendSortTemp);

                                ascendSortTemp = historyCreateDate.get(i);
                                historyCreateDate.set(i, (historyCreateDate.get(j)));
                                historyCreateDate.set(j, ascendSortTemp);

                            }
                        }
                    }

                    Collections.reverse(Index);
                    Collections.reverse(historyIndex);
                    Collections.reverse(historyImage);
                    Collections.reverse(historyCreateDate);



                    for(int i = 0; i < historyIndex.size(); i++) {
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-5 : " + Index.get(i));
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-6 : " + historyIndex.get(i));
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-6 : " + historyImage.get(i));
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-7 : " + historyCreateDate.get(i));
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

            list = (ListView) findViewById(R.id.list);
            mAdapter = new HistoryAdapter(act, historyIndex);
            list.setAdapter(mAdapter);

        }
    }
}