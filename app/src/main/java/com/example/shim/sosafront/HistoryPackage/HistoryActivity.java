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

public class HistoryActivity extends Activity {

    private static final String IP_ADDRESS = "http://113.198.84.37/";
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

//    private RecyclerView mRecyclerView;
//    private HistoryAdapter mAdapter;

    public JSONObject json_object;
    public ArrayList<String> key = new ArrayList<String>();
    public ArrayList<String> val = new ArrayList<String>();
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
        String tempValues = val.get(mPosition);
        String tempKeys = key.get(mPosition);

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

                    String value = result.toString();
                    JSONObject jo = new JSONObject(value);
                    String test = jo.getString("value").toString();

                    json_object = new JSONObject(test);

                    json_object.names().length();

                    ArrayList<String> tempKey = new ArrayList<String>();
                    ArrayList<String> tempVal = new ArrayList<String>();


//                    String [] a = new String[0];
                    Log.d("sendImage", "sendImage 총 길이 : " + json_object.names().length());

                    for (int i = 0; i < json_object.names().length(); i++) {
                        String kee = json_object.names().get(i).toString();
                        String k = IP_ADDRESS + "api/v1/userInfo/history/" + kee;

                        String v = IP_ADDRESS  + json_object.getString(kee);
//                        val[i] = v;
                        tempVal.add(v);
                        tempKey.add(k);
                        Log.d("key", "keykeykeykeykeykeykeykeykeykeykey : " + k);
                        Log.d("val", "valvalvalvalvalvalvalvalvalvalval : " + v);
                    }
                    val = tempVal;
                    key = tempKey;

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
            mAdapter = new HistoryAdapter(act, val);
            list.setAdapter(mAdapter);

        }
    }
}