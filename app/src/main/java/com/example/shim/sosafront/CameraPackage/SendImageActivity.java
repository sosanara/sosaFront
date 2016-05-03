package com.example.shim.sosafront.CameraPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class SendImageActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private ImageView sendImageView;
    /*private ImageView resultImageView;*/

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    String authKey;

    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;

    final String uploadFilePath = Environment.getExternalStorageDirectory() + "/sosaCamera/";
    final String uploadFileName = "img.jpg";
    String captureImage;
    String fileName;
    Bitmap testBitmap;

    DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        captureImage  = getIntent().getExtras().getString("captureImage");
        Log.d("SendImageActivityLog", "SendImageActivityLog0-0 : " + captureImage);
        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

        sendImageView = (ImageView) findViewById(R.id.sendImageView);

        /*resultImageView = (ImageView) findViewById(R.id.resultImageView);
        resultImageView.setVisibility(View.GONE);*/

        File imgFile = new  File(captureImage);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            sendImageView.setImageBitmap(myBitmap);

        }

    }

    public void retryImage(View arg0) {

        new File("/sdcard/my/file/is/here.txt").delete();
        Intent cameraIntent = new Intent(SendImageActivity.this, CameraActivity.class);
        startActivity(cameraIntent);
        SendImageActivity.this.finish();

    }

    public void sendImage(View arg0) {

        // Initialize  AsyncLogin() class with email and password
        new AsyncSendImage().execute();

    }

    private class AsyncSendImage extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(SendImageActivity.this);
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
                url = new URL("http://113.198.84.37/api/v1/picture/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }

            try {
                FileInputStream fileInputStream = new FileInputStream(uploadFilePath + uploadFileName);
                DataOutputStream dos = null;
                fileName = uploadFilePath + uploadFileName;

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                Log.d("sendImage", "sendImage 주는거 1-1: " + authKey);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", " Token " + authKey);
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("image", fileName);


                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                String filepath = uploadFilePath + uploadFileName;

                dos.writeBytes("Content-Disposition: form-data; name=\"filepath\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(filepath); // mobile_no is String variable
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);


                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                conn.connect();



            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                //여기서 로그인 페이지로 이동
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
                    JSONObject jsonObject = new JSONObject(value);
                    String test = jsonObject.getString("value").toString();

                    JSONObject subJsonObject = new JSONObject(test);

                    String sendImagePath = subJsonObject.getString("image").toString();
                    String resultImagePath = subJsonObject.getString("result_image").toString();
                    String resultType = subJsonObject.getString("result_type").toString();
                    String userName = subJsonObject.getString("user").toString();

                    Log.d("sendImage", "sendImage 받는거 2-2: " + sendImagePath);
                    Log.d("sendImage", "sendImage 받는거 2-3: " + resultImagePath);
                    Log.d("sendImage", "sendImage 받는거 2-4: " + resultType);
                    Log.d("sendImage", "sendImage 받는거 2-5: " + userName);

                    displayImageView(resultImagePath);

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
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

            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                /*Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                SignUpActivity.this.finish();*/

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
               /* Toast.makeText(SignUpActivity.this, "Invalid email or password", Toast.LENGTH_LONG);*/

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                /*Toast.makeText(SignUpActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG);*/

            }
        }

    }

    public void displayImageView(String ImagePath) {
        try {

            //웹사이트에 접속 (사진이 있는 주소로 접근)
            URL Url = new URL("http://113.198.84.37/" + ImagePath);
            Log.d("SendImageActivity","SendImageActivity 0-0 : " + "http://113.198.84.37/" + ImagePath);
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

            sendImageView.setImageBitmap(testBitmap);

            bis.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
