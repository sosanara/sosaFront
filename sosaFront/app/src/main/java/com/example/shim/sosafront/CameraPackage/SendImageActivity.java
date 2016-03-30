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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

//Adding Parameter name

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
                dos.writeBytes(lineEnd);


                //Json_Encoder encode=new Json_Encoder();
                //call to encode method and assigning response data to variable 'data'
                //String data=encode.encod_to_json();
                //response of encoded data
                //System.out.println(data);


                //Adding Parameter filepath

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                String filepath="http://192.168.1.110/echo/uploads"+fileName;

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
                Log.e("Upload file to server Exception",
                        "Exception : " + e.getMessage(), e);
            }

            return serverResponseCode;
        }
    }
}





