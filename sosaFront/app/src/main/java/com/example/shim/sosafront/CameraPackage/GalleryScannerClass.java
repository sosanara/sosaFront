package com.example.shim.sosafront.CameraPackage;

//사진 찍은 즉시 갤러

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

class GalleryScannerClass {
    private Context mContext;

    private String mPath;

    private MediaScannerConnection mMediaScanner;
    private MediaScannerConnection.MediaScannerConnectionClient mMediaScannerClient;

    public static GalleryScannerClass newInstance(Context context) {
        return new GalleryScannerClass(context);
    }

    private GalleryScannerClass(Context context) {
        mContext = context;
    }

    public void mediaScanning(final String path) {

        if (mMediaScanner == null) {
            mMediaScannerClient = new MediaScannerConnection.MediaScannerConnectionClient() {

                @Override
                public void onMediaScannerConnected() {
                    mMediaScanner.scanFile(mPath, null); // 디렉토리
                    // 가져옴
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            };
            mMediaScanner = new MediaScannerConnection(mContext, mMediaScannerClient);
        }

        mPath = path;
        mMediaScanner.connect();
    }
}

