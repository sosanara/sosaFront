package com.example.shim.sosafront.HistoryPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shim.sosafront.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HistoryResultActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    ImageView firstImageView;
    ImageView lastImageView;

    String getIndex;
    Bitmap testBitmap;
    /*String authKey;
    DataStore dataStore;*/


    String sendImagePath;
    String resultImagePath;
    String resultType;
    String userName;

    DownloadTask downloadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_result);

        firstImageView = (ImageView) findViewById(R.id.firstImageView);
        lastImageView = (ImageView) findViewById(R.id.lastImageView);


        getIndex = getIntent().getExtras().getString("graphIndex");

        Log.d("GraphResult", "GraphResult 0-0-0: " + getIndex);

        new AsyncGraphResult().execute();

        downloadTask = new DownloadTask();
        /*downloadTask.execute("http://113.198.84.37/" + resultImagePath);*/
    }



    private class AsyncGraphResult extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(HistoryResultActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {  //작업처리중' 프로그레스 다이얼로그 자동 시작
            super.onPreExecute();

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

                Log.d("GraphResult", "GraphResult 0-0-1: " + getIndex);
                url = new URL("http://113.198.84.37/api/v1/userInfo/history/" + getIndex);
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
                /*conn.setRequestProperty("Authorization", " Token " + "a6bf78ac14b1845b9f91f193ea4b86e8c0a65002");
*/
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

                Log.d("HistoryResultLog", "HistoryResultLog 0-0: " + conn.getResponseCode());

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

                    Log.d("HistoryResultLog", "HistoryResultLog 2: " + result.toString());  // result.toString()
                    Log.d("HistoryResultLog", "HistoryResultLog 2-1: " + reader);    //java.io.BufferedReader@5015f88

                    String value = result.toString();
                    JSONObject jsonObject = new JSONObject(value);
                    String test = jsonObject.getString("value").toString();

                    JSONObject subJsonObject = new JSONObject(test);

                    sendImagePath = subJsonObject.getString("image").toString();
                    resultImagePath = subJsonObject.getString("result_image").toString();
                    resultType = subJsonObject.getString("result_type").toString();
                    userName = subJsonObject.getString("user").toString();

                    Log.d("HistoryResultLog", "HistoryResultLog 2-2: " + sendImagePath);
                    Log.d("HistoryResultLog", "HistoryResultLog 2-3: " + resultImagePath);
                    Log.d("HistoryResultLog", "HistoryResultLog 2-4: " + resultType);
                    Log.d("HistoryResultLog", "HistoryResultLog 2-5: " + userName);

                    /*displayImageView(resultImagePath);*/

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

            //여기 처리 생각해야함

            /*downloadTask.execute("http://113.198.84.37/" + resultImagePath);*/

            if(isNetworkAvailable()){
                /** Getting a reference to Edit text containing url */

                /** Creating a new non-ui thread task */
                DownloadTask downloadTask = new DownloadTask();

                /** Starting the task created above */
                Log.d("HistoryResultLog", "HistoryResultLog 3-1" + resultImagePath);
                downloadTask.execute("http://113.198.84.37/" + resultImagePath);
                Log.d("HistoryResultLog", "HistoryResultLog 3-2" + resultImagePath);
            }else{
                Toast.makeText(getBaseContext(), "Network is not Available", Toast.LENGTH_SHORT).show();
            }



            pdLoading.dismiss();
        }

    }

    public void displayImageView(String ImagePath) {
        try {

            //웹사이트에 접속 (사진이 있는 주소로 접근)
            URL Url = new URL("http://113.198.84.37/" + ImagePath);

            Log.d("HistoryResultLog", "HistoryResultLog 4-1: " + "http://113.198.84.37/" + ImagePath);

            // 웹사이트에 접속 설정
            URLConnection urlcon = Url.openConnection();
            // 연결하시오
            urlcon.connect();
            // 이미지 길이 불러옴
            int imagelength = urlcon.getContentLength();
            // 스트림 클래스를 이용하여 이미지를 불러옴
            BufferedInputStream bis = new BufferedInputStream(urlcon.getInputStream(), imagelength);
            // 스트림을 통하여 저장된 이미지를 이미지 객체에 넣어줌
            testBitmap = BitmapFactory.decodeStream(bis);

            /*if(ImagePath.contains("myResult"))
                resultImageView.setImageBitmap(testBitmap);

            else
                sendImageView.setImageBitmap(testBitmap);*/

            firstImageView.setImageBitmap(testBitmap);
            lastImageView.setImageBitmap(testBitmap);

            bis.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable(){
        boolean available = false;
        /** Getting the system's connectivity service */
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        /** Getting active network interface  to get the network's status */
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo !=null && networkInfo.isAvailable())
            available = true;

        /** Returning the status of the network */
        return available;
    }

    private Bitmap downloadUrl(String strUrl) throws IOException{
        Bitmap bitmap=null;
        InputStream iStream = null;
        try{
            URL url = new URL(strUrl);
            /** Creating an http connection to communcate with url */
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            /** Connecting to url */
            urlConnection.connect();

            /** Reading data from url */
            iStream = urlConnection.getInputStream();

            /** Creating a bitmap from the stream returned from the url */
            bitmap = BitmapFactory.decodeStream(iStream);

        }catch(Exception e){
            /*Log.d("Exception while downloading url", e.toString());*/
        }finally{
            iStream.close();
        }
        return bitmap;
    }

    private class DownloadTask extends AsyncTask<String, Integer, Bitmap>{
        Bitmap bitmap = null;
        @Override
        protected Bitmap doInBackground(String... url) {
            try{
                bitmap = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            /** Getting a reference to ImageView to display the
             * downloaded image
             */
            ImageView firstView = (ImageView) findViewById(R.id.firstImageView);
            ImageView lastView = (ImageView) findViewById(R.id.lastImageView);

            /** Displaying the downloaded image */
            firstView.setImageBitmap(result);
            lastView.setImageBitmap(result);

            /** Showing a message, on completion of download process */
            Toast.makeText(getBaseContext(), "Image downloaded successfully", Toast.LENGTH_SHORT).show();
        }
    }


}





