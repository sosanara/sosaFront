package com.example.shim.sosafront.CameraPackage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.shim.sosafront.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class CaptureActivity extends Activity {

    Button autoCaptureBtn;
    /*Button reTryBtn;
    Button checkResultBtn;*/

    File takePictureImage;
    Bitmap myBitmap;
    ImageView myImage;
    static int pictureNum = 0;
    String mPath;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        Window window = this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.imageCheckColor));


        autoCaptureBtn = (Button) findViewById(R.id.autoCaptureBtn);
        /*reTryBtn = (Button) findViewById(R.id.reTryBtn);
        checkResultBtn = (Button) findViewById(R.id.resultCheckBtn);

        reTryBtn.setVisibility(View.GONE);
        checkResultBtn.setVisibility(View.GONE);*/

        takePictureImage = new  File("/sdcard/sosaCamera/IMG.jpg");
        myImage = (ImageView) findViewById(R.id.frame);

        /*myImage.setImageDrawable(Drawable.createFromPath(
                "/sdcard/sosaCamera/IMG.jpg"));*/

        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/sosaCamera/IMG.jpg");




        myImage.setImageBitmap(bitmap);

        if(takePictureImage.exists()){

            myBitmap = BitmapFactory.decodeFile(takePictureImage.getAbsolutePath());
            Matrix m = new Matrix();
            /*m.setRotate(90, (float) myBitmap.getWidth(), (float) myBitmap.getHeight());*/
            m.setRotate(90, (float) myBitmap.getWidth(), (float) myBitmap.getHeight());
            Bitmap rotateBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), m, false);


            myImage.setImageBitmap(rotateBitmap);

        }


        autoCaptureBtn.post(new Runnable() {
            @Override
            public void run() {
                autoCaptureBtn.performClick();

            }
        });

        autoCaptureBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                autoCaptureBtn.setVisibility(View.INVISIBLE);
                takeScreenshot();
                /*scanFile(Environment.getExternalStorageDirectory() + "/sosaCamera/hair" + String.valueOf(pictureNum) + ".jpg");*/
                Log.i("tmdlsk", "test3 " + String.valueOf(pictureNum));
                pictureNum++;

                Log.i("minho", mPath);
                Intent sendImageIntent = new Intent(CaptureActivity.this, SendImageActivity.class);
                sendImageIntent.putExtra("captureImage", mPath);
                startActivity(sendImageIntent);
                CaptureActivity.this.finish();
                /*reTryBtn.setVisibility(View.VISIBLE);
                checkResultBtn.setVisibility(View.VISIBLE);*/
            }
        });

        autoCaptureBtn.setVisibility(View.VISIBLE);


        /*reTryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CaptureActivity.this, CameraActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        checkResultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CaptureActivity.this, SendImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
*/

    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            mPath = Environment.getExternalStorageDirectory().toString() + "/sosaCamera/" + now + ".jpg";
            /*String mPath = Environment.getExternalStorageDirectory() + "/sosaCamera/hair" + String.valueOf(pictureNum) + ".jpg";*/
            Log.i("minho", "test1 = " + String.valueOf(String.valueOf(pictureNum)));
            // create bitmap screen captures
           /* View v1 = getWindow().getDecorView().getRootView();*/
            View v1 = myImage.getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap = Bitmap.createScaledBitmap(bitmap, 540, 960, true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            /*openScreenshot(imageFile);*/
            scanFile(mPath);
            //여기
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    /*private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image*//*");
        startActivity(intent);
    }*/


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
