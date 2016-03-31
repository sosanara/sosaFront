package com.example.shim.sosafront.CameraPackage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shim.sosafront.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SendImageActivity extends Activity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private ImageView sendImageView;
    private Bitmap image;
    private Button sendImageBtn;

    final String uploadFilePath = Environment.getExternalStorageDirectory() + "/sosaCamera/";
    final String uploadFileName = "img.jpg";
    int serverResponseCode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        // Get Reference to variables

        sendImageView = (ImageView) findViewById(R.id.sendImageView);
        image = ((BitmapDrawable)sendImageView.getDrawable()).getBitmap();
        sendImageBtn = (Button) findViewById(R.id.sendImageBtn);

        sendImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {

                            }
                        });

                        uploadFile(uploadFilePath + uploadFileName);

                    }
                }).start();
            }
        });

    }





    public int uploadFile(final String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

           /* Log.e("uploadFile", "Source File not exist :" + filepath);*/

            runOnUiThread(new Runnable() {
                public void run() {
                    /*messageText.setText("Source File not exist :" + filepath);*/
                }
            });

            return 0;

        } else {
            try {

                SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
                String authKey = prefs.getString("key", "");
                Log.d("tmdlsk", "비밀번호 수정 테스트0" + authKey);

                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("http://113.198.84.37/api/v1/picture/");
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", " Token " + authKey);
                conn.setRequestProperty("Connection", "Keep-Alive");
                //  conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("image", fileName);


                dos = new DataOutputStream(conn.getOutputStream());


                dos.writeBytes(twoHyphens + boundary + lineEnd);

/*//Adding Parameter name

                String name="amir";
                dos.writeBytes("Content-Disposition: form-data; name=\"name\"" + lineEnd);
                //dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                //dos.writeBytes("Content-Length: " + name.length() + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(name); // mobile_no is String variable
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);

//Adding Parameter phone
                String phone="9956565656";
                dos.writeBytes("Content-Disposition: form-data; name=\"phone\"" + lineEnd);
                //dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                //dos.writeBytes("Content-Length: " + name.length() + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(phone); // mobile_no is String variable
                dos.writeBytes(lineEnd);*/


                //Json_Encoder encode=new Json_Encoder();
                //call to encode method and assigning response data to variable 'data'
                //String data=encode.encod_to_json();
                //response of encoded data
                //System.out.println(data);


                //Adding Parameter filepath

               /* String filepath="http://192.168.1.110/echo/uploads"+fileName;*/
                String filepath = uploadFilePath + uploadFileName;

                dos.writeBytes("Content-Disposition: form-data; name=\"filepath\"" + lineEnd);
                //dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                //dos.writeBytes("Content-Length: " + name.length() + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(filepath); // mobile_no is String variable
                dos.writeBytes(lineEnd);


//Adding Parameter media file(audio,video and image)

                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\""+ fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();



                Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    Log.i("uploadFile", "File Upload Complete.");

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("sendImage", "sendImage 받는거2: " + result.toString());  // result.toString()
                    Log.d("sendImage", "sendImage거 받는거 2-2: " + result);


                    String value = result.toString();
                    JSONObject jsonObject =  new JSONObject(value);


                    String test = jsonObject.getString("value").toString();





                    /*SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();*/

                    Log.d("sendImage", "sendImage 받는거 2-3: " + test);

                    JSONObject subJsonObject =  new JSONObject(test);

                    String test1 = jsonObject.getString("image").toString();
                    String test2 = jsonObject.getString("result").toString();
                    String test3 = jsonObject.getString("user").toString();

                    /*try {
                        // Locate the array name in JSON
                        jsonarray = jsonobject.getJSONArray("worldpopulation");

                        for (int i = 0; i < jsonarray.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            jsonobject = jsonarray.getJSONObject(i);
                            // Retrive JSON Objects
                            map.put("rank", jsonobject.getString("rank"));
                            map.put("country", jsonobject.getString("country"));
                            map.put("population", jsonobject.getString("population"));
                            map.put("flag", jsonobject.getString("flag"));
                            // Set the JSON Objects into the array
                            arraylist.add(map);
                        }
                    } catch (JSONException e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }*/


                    /*JSONArray jArr = new JSONArray(jsonObject.getString("value"));
                    Log.d("sendImage", "sendImage 받는거 2-4: " + jArr);

                    String btnTitle [] = new String[jArr.length()];


                    String title = jsonObject.getString("title").toString();

                    for(int i = 0; i < jArr.length(); i++){
                        btnTitle [i] = jArr.getJSONObject(i).getString("image").toString();
                        //출력하여 결과 얻기
                        Log.d("sendImage", "btnTitle[" + i + "]=" + btnTitle[i]);
                    }*/

                    /*JSONObject  jsonObject =  new JSONObject(value);
                    JSONArray jsonarray = new JSONArray(jsonObject.getString("value"));

                    String btnTitle [] = new String[jsonarray.length()];

                    for(int i = 0; i < jsonarray.length(); i++){
                        btnTitle [i] = jsonarray.getJSONObject(i).getString("result").toString();
                        Log.d("sendImage", "sendImage 받는거 2-3: " + btnTitle[i]);
                        //출력하여 결과 얻기
                    }*/

                    /*for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String resultFirstImage = jsonobject.getString("image");
                        String resultLastImage = jsonobject.getString("result");
                        String resultUser = jsonobject.getString("user");
                        String resultSuccess = jsonobject.getString("uccess");

                        Log.d("sendImage", "sendImage 받는거 2-3: " + resultFirstImage);
                        Log.d("sendImage", "sendImage 받는거 2-3: " + resultLastImage);
                        Log.d("sendImage", "sendImage 받는거 2-3: " + resultUser);
                        Log.d("sendImage", "sendImage 받는거 2-3: " + resultSuccess);
                    }*/


                    /*SONObject uj = (JSONObject) uJson;
                    JSONArray jsonSites = (JSONArray)  obj;
                    obj =  parser.parse(uj.get("sites").toString());

                    for()*/
/*
                    JSONObject site = (JSONObject)(((JSONArray)jsonSites.get(i)).get(0));
                    JSONObject testJson = new JSONObject(value);
                    String resultValue = (String) testJson.get("value");
                    Log.d("sendImage", "sendImage 받는거 2-3: " + resultValue);*/
                    /*String resultFirstImage = (String) testJson.get("image");
                    String resultLastImage = (String) testJson.get("result");
                    String resultUser = (String) testJson.get("user
");*/

                    /*Log.d("sendImage", "sendImage 받는거 2-3: " + resultFirstImage);
                    Log.d("sendImage", "sendImage 받는거 2-3: " + resultLastImage);
                    Log.d("sendImage", "sendImage 받는거 2-3: " + resultUser);*/

                        /*String authKey = (String) testJson.get("key");*/



                        /*editor.putString("key", authKey);
                        editor.commit();*/

                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {


                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(SendImageActivity.this,
                                "MalformedURLException", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (final Exception e) {


                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(SendImageActivity.this,
                                "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                /*Log.e("Upload file to server Exception", "Exception : " );*/
            }

            return serverResponseCode;
        }
    }
}





