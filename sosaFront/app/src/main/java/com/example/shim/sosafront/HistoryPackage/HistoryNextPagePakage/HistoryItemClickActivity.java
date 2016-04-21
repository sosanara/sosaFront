package com.example.shim.sosafront.HistoryPackage.HistoryNextPagePakage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.HistoryPackage.HistoryActivity;
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

public class HistoryItemClickActivity extends Activity {

    private static String IP_ADDRESS ;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;


    private static String type;
    private static String name;
    private static String created_date;
    private static String success;

    private TextView type_textview;
    private TextView name_textview;
    private TextView created_date_textview;

    Activity act = HistoryItemClickActivity.this;

//    private RecyclerView mRecyclerView;
//    private HistoryAdapter mAdapter;

    public JSONObject json_object;

    String authKey;
    DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_item_click);

        type_textview = (TextView) findViewById(R.id.item_click_type);
        name_textview = (TextView) findViewById(R.id.item_click_name);
        created_date_textview = (TextView) findViewById(R.id.item_click_created_data);


        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

        Intent intent = getIntent();
        IP_ADDRESS = intent.getStringExtra("url");

        new AsyncItemClick().execute();
    }

    public void delete_image(View v) {

        new AsyncDeleteClick().execute();

    }


    //다시 갑니다~
    //Server Connect class
    private class AsyncItemClick extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(HistoryItemClickActivity.this);
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
                Log.d("HistoryClickLog", "HistoryClickLog 0-0: " + IP_ADDRESS + "/");
                url = new URL(IP_ADDRESS);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("HistoryClickActivity", "HistoryClickActivity Connection Test : 접근실패");
                return "exception";
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                /*conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );*/
               /* conn.setRequestProperty("Authorization", " Token " + authKey);
*/
                /*conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");*/

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", " Token " + authKey);

                conn.setDoInput(true);
                /*conn.setDoOutput(true);*/
                /*conn.setDoOutput(true);
                conn.setRequestProperty(
                        "Content-Type", "application/x-www-form-urlencoded");*/
                /*conn.setRequestProperty("Authorization", " Token " + authKey);*/
                /*conn.setRequestMethod("DELETE");*/

                conn.connect();


            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                //Data를 받음
                int response_code = conn.getResponseCode();

                Log.d("HistoryClickLog", "HistoryClickLog 2-0: " + conn.getResponseCode());
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

                    type = json_object.getString("type");
                    name = json_object.getString("name");
                    created_date = json_object.getString("created_date");

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

            type_textview.setText(type);
            name_textview.setText(name);
            created_date_textview.setText(created_date);


        }
    }


    //Server Connect class
    private class AsyncDeleteClick extends AsyncTask<String, String, String> {
        /*ProgressDialog pdLoading = new ProgressDialog(HistoryItemClickActivity.this);*/
        HttpURLConnection conn2;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            //*pdLoading.setMessage("\tLoading...");
            /*pdLoading.setCancelable(false);
            pdLoading.show();*/

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Enter URL address where your php file resides
                url = new URL(IP_ADDRESS + "/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("HistoryItemClickLog", "HistoryItemClickLog Connection Test : 접근실패");
                return "exception";
            }


            try {
                //authKey : User가 로그인한 고유의 Key 값.

                /*conn2 = (HttpURLConnection) url.openConnection();

                conn2.setDoOutput(true);


                conn2.setRequestProperty("Authorization", " Token " + authKey);
                conn2.setRequestMethod("DELETE");*/



                conn2 = (HttpURLConnection) url.openConnection();
                conn2.setRequestProperty("Content-Type",
                        "application/json");
                conn2.setRequestProperty("Authorization", " Token " + authKey);
                conn2.setRequestMethod("DELETE");



                conn2.setDoInput(true);


                conn2.connect();


            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                //Data를 받음
                int response_code = conn2.getResponseCode();

                Log.d("HistoryItemClickLog", "HistoryItemClickLog 4-0: " + conn2.getResponseCode());
                // Check if successful connection made


                if (response_code == HttpURLConnection.HTTP_NO_CONTENT) { //204리퀘스트 받음

                    /*Toast.makeText(act, "성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
*/
//                    // Read data sent from server
//                    InputStream input = conn.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                    StringBuilder result = new StringBuilder();
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        result.append(line);    //서버에서 온 데이터를 계속 붙임
//                    }
//
//                    String value = result.toString();
//                    JSONObject jo = new JSONObject(value);
//                    success = jo.getString("success").toString();
//                    Log.d("HistoryItemClickLog", "HistoryItemClickLog Success : " + success);
//
//                    return (result.toString());

                    return ("successful");
                } else {
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }  finally {
                conn2.disconnect();
            }
        }


        @Override
        protected void onPostExecute(String result) {
            /*pdLoading.dismiss();*/

            Intent userInfoIntent = new Intent(HistoryItemClickActivity.this, HistoryActivity.class);
            startActivity(userInfoIntent);
            finish();

//            if(success.equals("Successful removed History Parts.")) {
//                Toast.makeText(act, "성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
//                finish();
//            } else {
//                Toast.makeText(act, "삭제 실패", Toast.LENGTH_SHORT).show();
//            }
            finish();
        }
    }
}
