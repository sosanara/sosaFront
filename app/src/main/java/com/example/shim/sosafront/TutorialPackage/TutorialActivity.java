package com.example.shim.sosafront.TutorialPackage;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.example.shim.sosafront.MainPackage.MainActivity;
import com.example.shim.sosafront.R;

public class TutorialActivity extends Activity implements GestureDetector.OnGestureListener {
    private LinearLayout networkCheckLayout;
    protected GestureDetector gestureScanner;
    protected ViewFlipper vf;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private ImageView clearOneView;
    private ImageView clearTwoView;
    private ImageView clearThreeView;

    boolean tf;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.tutorialColor));

        gestureScanner = new GestureDetector(this);
        setContentView(R.layout.activity_tutorial);
        vf = (ViewFlipper) findViewById(R.id.pages);

        Intent intent = getIntent();
        tf = intent.getExtras().getBoolean("TrueFalse");

        clearOneView = (ImageView) findViewById(R.id.clearOneView);
        clearTwoView = (ImageView) findViewById(R.id.clearTwoView);
        clearThreeView = (ImageView) findViewById(R.id.clearThreeView);

        if (!tf) {
            Intent intentToMain = new Intent(this, MainActivity.class);
            finish();
            startActivity(intentToMain);
        }



        clearOneView.setOnTouchListener(exitTutorial);
        clearTwoView.setOnTouchListener(exitTutorial);
        clearThreeView.setOnTouchListener(exitTutorial);
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }

    public boolean onDown(MotionEvent e) {
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (e1.getX() > e2.getX() && Math.abs(e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                if (vf.getDisplayedChild() != 2)
                    vf.showNext();
            } else if (e1.getX() < e2.getX() && e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                if (vf.getDisplayedChild() != 0)
                    vf.showPrevious();
            }
        } catch (Exception e) {
            // nothing
        }
        return true;

    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    /*public void endTutorial(View v) {
        switch (v.getId()) {
            case R.id.endBtn:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }*/

    private View.OnTouchListener exitTutorial = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(v.getId()) {
                case R.id.clearOneView:
                    finish();
                    break;

                case R.id.clearTwoView:
                    finish();
                    break;

                case R.id.clearThreeView:
                    finish();
                    break;
            }

            return false;

        }
    };
}

