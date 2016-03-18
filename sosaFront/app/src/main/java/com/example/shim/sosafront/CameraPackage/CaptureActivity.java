package com.example.shim.sosafront.CameraPackage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.shim.sosafront.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class CaptureActivity extends Activity {
    Button button;
    File imgFile;
    Bitmap myBitmap;
    ImageView myImage;
    static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        imgFile = new  File("/sdcard/sosaCamera/IMG.jpg");
        myImage = (ImageView) findViewById(R.id.frame);

        /*myImage.setImageDrawable(Drawable.createFromPath(
                "/sdcard/sosaCamera/IMG.jpg"));*/

        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/sosaCamera/IMG.jpg");
        myImage.setImageBitmap(bitmap);










        if(imgFile.exists()){

            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Matrix m = new Matrix();
            m.setRotate(90, (float) myBitmap.getWidth(), (float) myBitmap.getHeight());
            Bitmap rotateBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), m, false);


            myImage.setImageBitmap(rotateBitmap);

        }




        /*if(imgFile.exists()){
            Bitmap myBitmap = decodeSampleImage(imgFile, 720, 960);  // method will be added below
            ((BitmapDrawable)myImage.getDrawable()).getBitmap().recycle(); // most of the time this will work alone
            myImage.setImageBitmap(myBitmap);
        }*/

        button = (Button) findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                button.setVisibility(View.INVISIBLE);
                takeScreenshot();
                scanFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sosaCamera/" + "hair" + i + ".jpg");
            }
        });
        scanFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sosaCamera/" + "hair" + i + ".jpg");
        button.setVisibility(View.VISIBLE);
        scanFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sosaCamera/" + "hair" + i + ".jpg");
        Log.d("tmdlsk", String.valueOf(i));
        /*GalleryScannerClass scanner = GalleryScannerClass.newInstance(CaptureActivity.this);
        scanner.mediaScanning(Environment.getExternalStorageDirectory().getAbsolutePath());*/
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
           /* String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";*/
            String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sosaCamera/" + "hair" + i + ".jpg";
            /*Environment.getExternalStorageDirectory().getAbsolutePath() + "/sosaCamera/"*/
            // create bitmap screen capture
           /* View v1 = getWindow().getDecorView().getRootView();*/
            i++;
            View v1 = myImage.getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }

      /* scanFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sosaCamera/" + "hair" + i + ".jpg");
        */
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }


    private void scanFile(String path) {

        MediaScannerConnection.scanFile(CaptureActivity.this,
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }







}
