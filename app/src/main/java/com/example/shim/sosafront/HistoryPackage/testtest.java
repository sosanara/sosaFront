package com.example.shim.sosafront.HistoryPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

public class testtest extends Activity {

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



        new AsyncItemClick().execute();
    }



    //다시 갑니다~
    //Server Connect class
    private class AsyncItemClick extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(testtest.this);
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
                url = new URL("http://113.198.84.37/api/v1/userInfo/history/63");

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
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Authorization", " Token " + authKey);

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


}
