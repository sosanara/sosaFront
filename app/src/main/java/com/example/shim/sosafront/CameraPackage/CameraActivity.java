package com.example.shim.sosafront.CameraPackage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.shim.sosafront.R;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import io.values.camera.widget.CameraView;
import io.values.camera.widget.FocusView;

public class CameraActivity extends Activity implements CameraView.OnCameraSelectListener,
        View.OnClickListener {

    private static final int Permission_Request = 211;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 211;
    int permissionCheck;
    private CameraView cameraView;

    DisplayImageOptions options = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.ARGB_8888)
            .imageScaleType(ImageScaleType.EXACTLY).considerExifParams(true)
            .cacheInMemory(false).cacheOnDisk(false).displayer(new FadeInBitmapDisplayer(0)).build();

    private RelativeLayout rlTop;

    private ImageButton ib_camera_change;
    private ImageButton ib_camera_grid;

    private ImageButton ibTakePicture;

    private ImageView imgGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImaLoader();
        setContentView(R.layout.activity_camera);

        permissionCheck = ContextCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.WRITE_CALENDAR);
        //마시멜로 버전 부터 이거 써야함
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // only for gingerbread and newer versions
            String permission = Manifest.permission.CAMERA;
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                // Add your function here which open camera
            }
        } else {
            // Add your function here which open camera
        }


        try {
            cameraView = new CameraView(this);
            cameraView.setOnCameraSelectListener(this);
            cameraView.setFocusView((FocusView) findViewById(R.id.sf_focus));
            //  cameraView.setCameraView((SurfaceView) findViewById(R.id.sf_camera));  //default
            cameraView.setCameraView((SurfaceView) findViewById(R.id.sf_camera), CameraView.MODE4T3);
            cameraView.setPicQuality(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initViews();
        imgGrid.setVisibility(View.VISIBLE);

        scanFile(Environment.getExternalStorageDirectory() + "/sosaCamera/IMG.jpg");

    }

    private void initViews() {
        rlTop = $(R.id.rl_top);
        ib_camera_change = $(R.id.ib_camera_change);
        ib_camera_grid = $(R.id.ib_camera_grid);
        ibTakePicture = $(R.id.ib_camera_take_picture);
        imgGrid = $(R.id.img_grid);
    }

    private <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    private void initImaLoader() {
        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration
                        .Builder(getApplicationContext())
                        .diskCacheFileCount(50)
                        .threadPoolSize(Thread.NORM_PRIORITY - 2)
                        .denyCacheImageMultipleSizesInMemory()
                        .memoryCacheSize(30)
                        .memoryCache(new LRULimitedMemoryCache(30))
                        .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ibTakePicture.setOnClickListener(this);
        ib_camera_change.setOnClickListener(this);
        ib_camera_grid.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            cameraView.onResume();
            cameraView.setTopDistance(rlTop.getHeight());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null)
            cameraView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_camera_change:
                cameraView.changeCamera();
                break;

            /*case R.id.ib_camera_grid:
                if (imgGrid.getVisibility() == View.VISIBLE) {
                    imgGrid.setVisibility(View.GONE);
                    ib_camera_grid.setBackgroundResource(R.drawable.camera_grid_normal);
                    break;
                }
                ib_camera_grid.setBackgroundResource(R.drawable.camera_grid_press);
                imgGrid.setVisibility(View.VISIBLE);
                break;*/

            case R.id.ib_camera_take_picture:
                cameraView.takePicture(false);
                scanFile(Environment.getExternalStorageDirectory() + "/sosaCamera/IMG.jpg");
                /*Intent captureIntent = new Intent(this, CaptureActivity.class);
                captureIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK); // | Intent.FLAG_ACTIVITY_NEW_TASK);
*/
                break;
        }
    }

    @Override
    public void onShake(int orientation) {
        // you can rotate views here
    }

    @Override
    public void onTakePicture(final boolean success, String filePath) {
        //sd/ResoCamera/(file)
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    /*Toast.makeText(CameraActivity.this, "success!", Toast.LENGTH_SHORT).show();*/
                }
            }
        });

        Intent captureIntent = new Intent(CameraActivity.this, CaptureActivity.class);
        startActivity(captureIntent);
        CameraActivity.this.finish();
    }

    @Override
    public void onChangeFlashMode(int flashMode) {
        /*switch (flashMode) {
            case CameraView.FLASH_AUTO:
                ib_camera_flash.setBackgroundResource(R.drawable.camera_flash_auto);
                break;
            case CameraView.FLASH_OFF:
                ib_camera_flash.setBackgroundResource(R.drawable.camera_flash_off);
                break;
            case CameraView.FLASH_ON:
                ib_camera_flash.setBackgroundResource(R.drawable.camera_flash_on);
                break;
        }*/
    }

    @Override
    public void onChangeCameraPosition(int camera_position) {

    }

    private void scanFile(String path) {

        MediaScannerConnection.scanFile(CameraActivity.this,
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }
}
