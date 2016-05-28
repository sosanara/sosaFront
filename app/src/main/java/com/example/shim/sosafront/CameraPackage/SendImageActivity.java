package com.example.shim.sosafront.CameraPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.GalleryPackage.GalleryActivity;
import com.example.shim.sosafront.HistoryPackage.HistoryActivity;
import com.example.shim.sosafront.R;
import com.example.shim.sosafront.StatisticPackage.StatisticActivity;

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

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SendImageActivity extends Activity {

    private LinearLayout networkCheckLayout;
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    ImageButton galleryBtn;
    ImageButton historyBtn;
    ImageButton statisticsBtn;

    public static final int CONNECTION_TIMEOUT=1000000;
    public static final int READ_TIMEOUT=1500000;

    private ImageView sendImageView;


    TextView userNameView;
    TextView baldTypeView;
    TextView baldProgressView;
    ImageView originImageView;
    ImageView resultImageView;

    String originImagePath;
    String resultImagePath;
    String resultType;
    String userName;
    String percentage;

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    String authKey;

    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;

    final String uploadFilePath = Environment.getExternalStorageDirectory() + "/sosaCamera/";
    String uploadFileName;
    String captureImage;
    String fileName;
    Bitmap testBitmap;

    DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        //글씨체 통일
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        captureImage  = getIntent().getExtras().getString("captureImage");
        Log.d("SendImageActivityLog", "SendImageActivityLog0-0 : " + captureImage);
        uploadFileName = captureImage;

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

        networkCheckLayout = (LinearLayout)findViewById(R.id.networkCheckLayout);
        sendImageView = (ImageView) findViewById(R.id.sendImageView);

        /*resultImageView = (ImageView) findViewById(R.id.resultImageView);
        resultImageView.setVisibility(View.GONE);*/

        File imgFile = new  File(captureImage);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            sendImageView.setImageBitmap(myBitmap);

        }


    }

    //글씨체 통일
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

    public void retryImage(View arg0) {

        new File("/sdcard/my/file/is/here.txt").delete();
        Intent cameraIntent = new Intent(SendImageActivity.this, CameraActivity.class);
        startActivity(cameraIntent);
        SendImageActivity.this.finish();

    }

    public void sendImage(View arg0) {
        // Initialize  AsyncLogin() class with email and password
        setContentView(R.layout.image_result);
        originImageView = (ImageView) findViewById(R.id.originImageView);
        resultImageView = (ImageView) findViewById(R.id.resultImageView);

        galleryBtn = (ImageButton) findViewById(R.id.galleryBtn);
        historyBtn = (ImageButton) findViewById(R.id.historyBtn);
        statisticsBtn = (ImageButton) findViewById(R.id.statisticsBtn);

        userNameView = (TextView) findViewById(R.id.sendImageUserNameView);
        baldTypeView = (TextView) findViewById(R.id.baldTypeView);
        baldProgressView = (TextView) findViewById(R.id.baldProgress);

        new AsyncSendImage().execute();

    }

    private class AsyncSendImage extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(SendImageActivity.this);
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
                FileInputStream fileInputStream = new FileInputStream(uploadFileName);
                DataOutputStream dos = null;
                fileName = uploadFileName;

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

                String filepath = uploadFileName;

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

                    originImagePath = subJsonObject.getString("origin_image").toString();
                    resultImagePath = subJsonObject.getString("change_image").toString();
                    resultType = subJsonObject.getString("type").toString();
                    userName = subJsonObject.getString("user").toString();
                    percentage = subJsonObject.getString("percentage").toString();

                    Log.d("sendImageActivityLog", "sendImageActivityLog 2-2 : " + originImagePath);
                    Log.d("sendImageActivityLog", "sendImageActivityLog 2-3 : " + resultImagePath);
                    Log.d("sendImageActivityLog", "sendImageActivityLog 2-4 : " + resultType);
                    Log.d("sendImageActivityLog", "sendImageActivityLog 2-5 : " + userName);
                    Log.d("sendImageActivityLog", "sendImageActivityLog 2-6 : " + percentage);


                    displayImageView(originImagePath, 1);
                    displayImageView(resultImagePath, 2);

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

            /*galleryBtn = (ImageButton) findViewById(R.id.galleryBtn);
            historyBtn = (ImageButton) findViewById(R.id.historyBtn);
            statisticsBtn = (ImageButton) findViewById(R.id.statisticsBtn);

            userNameView = (TextView) findViewById(R.id.sendImageUserNameView);
            baldTypeView = (TextView) findViewById(R.id.baldTypeView);
            baldProgressView = (TextView) findViewById(R.id.baldProgress);*/

            //this method will be running on UI thread
            Log.d("SendImageActivityLog", "SendImageActivityLog 3-0 : " + result);



            galleryBtn.setOnClickListener(menuClick);
            historyBtn.setOnClickListener(menuClick);
            statisticsBtn.setOnClickListener(menuClick);

            int typeNumber = Integer.parseInt(resultType);
            switch(typeNumber) {
                case 0 :
                    baldTypeView.setText("Normal");
                    break;

                case 1 :
                    baldTypeView.setText("Forward");
                    break;

                case 2 :
                    baldTypeView.setText("Backward");
                    break;

                case 3 :
                    baldTypeView.setText("Karma");
                    break;

                case 4:
                    baldTypeView.setText("Bald");
                    break;

                default:
                    baldTypeView.setText("에러");
            }


            Log.d("SendImageActivityLog", "SendImageActivityLog 3-1 : " + percentage);

            if(percentage.contains(".")) {
                int cutDot = percentage.indexOf(".");
                String cutPercentage = percentage.substring(0, cutDot);
                baldProgressView.setText(cutPercentage + "%");
                Log.d("SendImageActivityLog", "SendImageActivityLog 3-1 : " + cutPercentage + "%");
            }

            else
                baldProgressView.setText(percentage + "%");


            userNameView.setText(userName + "님의 두발상태");

            /*displayImageView(originImagePath, 1);
            displayImageView(resultImagePath, 2);*/

            pdLoading.dismiss();

            if(result.equals("success"))
            {
                /*int typeNumber = Integer.parseInt(resultType);
                switch(typeNumber) {
                    case 0 :
                        baldTypeView.setText("정상");

                    case 1 :
                        baldTypeView.setText("앞부분");

                    case 2 :
                        baldTypeView.setText("뒷부분");

                    case 3 :
                        baldTypeView.setText("가르마");

                    case 4:
                        baldTypeView.setText("완전탈모");
                }
                userNameView.setText(userName + "님의 모발상태");
                baldProgressView.setText(percentage + "%");*/

            }else {

            }
        }

    }

    public void displayImageView(String ImagePath, int i) {
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

            if(i==1)
                originImageView.setImageBitmap(testBitmap);
            else
                resultImageView.setImageBitmap(testBitmap);

            bis.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private View.OnClickListener menuClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent;
            switch(v.getId()) {  //switch가 if문보다 빠름 몇개 없는데 if문이 나려나?
                case R.id.takePictureBtn:
                    intent = new Intent(SendImageActivity.this, CameraActivity.class);
                    startActivity(intent);
                    break;

                case R.id.galleryBtn:
                    intent = new Intent(SendImageActivity.this, GalleryActivity.class);
                    startActivity(intent);
                    break;

                case R.id.historyBtn:
                    intent = new Intent(SendImageActivity.this, HistoryActivity.class);
                    startActivity(intent);
                    break;

                case R.id.statisticsBtn:
                    intent = new Intent(SendImageActivity.this, StatisticActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
