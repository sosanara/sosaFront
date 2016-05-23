package com.example.shim.sosafront.HistoryPackage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.MainPackage.MainActivity;
import com.example.shim.sosafront.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class HistoryActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

    private LineChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private String authKey;
    private DataStore dataStore;

    final private ArrayList<String> historyIndex = new ArrayList<String>();
    final private ArrayList<String> historyType = new ArrayList<String>();
    final private ArrayList<String> historyPercentage = new ArrayList<String>();
    final private ArrayList<String> historyCreateDate = new ArrayList<String>();
    final private ArrayList<String> historyBirth = new ArrayList<String>();

    int cutDot;
    float [] floatValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        Drawable home = getResources().getDrawable(R.drawable.toolbar_home);
        Drawable resizeHome = resize(home);
        toolbar.setNavigationIcon(resizeHome);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");

        new AsyncHistory().execute();

        /*tvX = (TextView) findViewById(R.id.tvXMax);*/
        /*tvY = (TextView) findViewById(R.id.tvYMax);*/

        /*mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        mSeekBarX.setProgress(graphIndex.size());  //여기 이미지 개수만큼
        mSeekBarY.setProgress(10);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);*/

       /* Paint p = mChart.getPaint(Chart.setPaint());
        p.setTextSize(...);

        mChart.setPaint(p, Chart.PAINT_VALUES);*/

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        /*mChart.getXAxis().setTextSize(30);*/

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setHighlightPerDragEnabled(false);
        mChart.setHighlightPerTapEnabled(true);



        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // set the marker to the chart
        mChart.setMarkerView(mv);

        // x-axis limit line
        /*LimitLine llXAxis = new LimitLine(10f, "Index 10");*/
        /*llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLabelPosition.LEFT_BOTTOM);
        llXAxis.setTextSize(10f);*/

        XAxis xAxis = mChart.getXAxis();
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line



        /*LimitLine ll1 = new LimitLine(100f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(0f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);*/

        YAxis leftAxis = mChart.getAxisLeft();
        /*leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);*/
        /*leftAxis.setAxisMaxValue(220f);
        leftAxis.setAxisMinValue(-50f);*/
        //leftAxis.setYOffset(20f);
        leftAxis.setAxisMaxValue(100f);
        leftAxis.setAxisMinValue(0f);

        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        /*leftAxis.setDrawLimitLinesBehindData(true);*/

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        /*setData(45, 100);*/

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(LegendForm.LINE);

        // // dont forget to refresh the drawing
        // mChart.invalidate();
    }

    private class AsyncHistory extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(HistoryActivity.this);
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
                url = new URL("http://113.198.84.37/api/v1/userInfo/history/");
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
                conn.setRequestProperty("Authorization", " Token " + authKey);

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                /*conn.setDoOutput(true);*/

                // Append parameters to URL

                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                int response_code = conn.getResponseCode();

                Log.d("HistoryActivityLog", "HistoryActivityLog 0-0: " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("HistoryActivityLog", "HistoryActivityLog 1: " + result.toString());
                    Log.d("HistoryActivityLog", "HistoryActivityLog 1-1: " + reader);

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("HistoryActivityLog", "HistoryActivityLog 2: " + result.toString());  // result.toString()
                    Log.d("HistoryActivityLog", "HistoryActivityLog 2-1: " + reader);    //java.io.BufferedReader@5015f88

                    String value = result.toString();
                    JSONObject serverJsonObject = new JSONObject(value);

                    Log.d("HistoryActivityLog", "HistoryActivityLog 2-3: " + serverJsonObject.get("value"));



                    JSONObject firstJsonObject = new JSONObject(String.valueOf(serverJsonObject.get("value")));

                    Iterator<String> keys = firstJsonObject.keys();



                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                        historyIndex.add(key);
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-4: " + key);

                        JSONObject secondJsonObject = new JSONObject(String.valueOf(firstJsonObject.get(key)));
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-5 : " + secondJsonObject);
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-6 type : " + secondJsonObject.getString("type").toString());
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-7 create_date : " + secondJsonObject.getString("create_date").toString());
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-8 birth : " + secondJsonObject.getString("birth").toString());
                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-9 percentage : " + secondJsonObject.getString("percentage").toString());

                        historyType.add(secondJsonObject.getString("type").toString());
                        historyCreateDate.add(secondJsonObject.getString("create_date").toString());
                        historyBirth.add(secondJsonObject.getString("birth").toString());
                        historyPercentage.add(secondJsonObject.getString("percentage").toString());


                    }

                    //그래프 시간 오름차순 정렬
                    String ascendSortTemp;

                    for (int i = 0; i < historyIndex.size(); i++) {
                        for (int j = i + 1; j < historyIndex.size(); j++) {
                            if (Integer.valueOf(historyIndex.get(i)) > Integer.valueOf(historyIndex.get(j))) {
                                ascendSortTemp = historyIndex.get(i);
                                historyIndex.set(i, (historyIndex.get(j)));
                                historyIndex.set(j, ascendSortTemp);

                                ascendSortTemp = historyType.get(i);
                                historyType.set(i, (historyType.get(j)));
                                historyType.set(j, ascendSortTemp);

                                ascendSortTemp = historyCreateDate.get(i);
                                historyCreateDate.set(i, (historyCreateDate.get(j)));
                                historyCreateDate.set(j, ascendSortTemp);

                                ascendSortTemp = historyBirth.get(i);
                                historyBirth.set(i, (historyBirth.get(j)));
                                historyBirth.set(j, ascendSortTemp);

                                ascendSortTemp = historyPercentage.get(i);
                                historyPercentage.set(i, (historyPercentage.get(j)));
                                historyPercentage.set(j, ascendSortTemp);
                            }
                        }
                    }

                    /*for(int i = 0; i < historyIndex.size(); i++) {
                        if(historyPercentage.get(i).contains(".")) {
                            cutDot = historyPercentage.get(i).indexOf(".");
                            historyPercentage.set(i, historyPercentage.get(i).substring(0, cutDot) + ".0");
                        }

                        historyPercentage.set(i, historyPercentage.get(i) + ".0");

                        Log.d("HistoryActivityLog", "HistoryActivityLog 2-14 historyPercentage: " + historyPercentage.get(i));
                    }*/




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

            setData(historyIndex.size(), 100);

            pdLoading.dismiss();
        }

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.line, menu);*/
        return true;
    }

    /*@Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());

        // redraw
        mChart.invalidate();
    }*/

   /* @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }*/

    private void setData(int count, float range) {

        Log.d("HistoryActivityLog", "HistoryActivityLog 4-0: " + historyIndex.size());


        range = 100;
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            String historyCreateDateCut =  historyCreateDate.get(i).substring(5, 10) ;// ex) 2016-04-01
            String historyCreateDateTransfer = historyCreateDateCut.replace("-", "월");
            historyCreateDateTransfer = historyCreateDateTransfer + "일";
            xVals.add(historyCreateDateTransfer);  //여기에 찍은 날짜
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        //여기요
        for (int i = 0; i < count; i++) {

            String cutDotHistoryPercentage = String.format("%.3f", Float.parseFloat(historyPercentage.get(i)));
            float yValue = Float.parseFloat(cutDotHistoryPercentage);
            /*Log.d("HistoryActivityLog", "HistoryActivityLog 2-14 historyPercentage: " + f );*/
            /*float mult = (range + 1);*/
            /*float yValue = 10f;*/

            /*float mult = Float.parseFloat(historyPercentage.get(i));*/
            /*String deleteDotPercentage =String.format("%.f" , historyPercentage.get(i));
            float percentage = Float.parseFloat(String.valueOf(deleteDotPercentage)); //수정 예정 서버에서 받은 탈모비율 적용 예정
            */
            yVals.add(new Entry(yValue, i));
        }

        // create a dataset and give it a type
        /*LineDataSet set1 = new LineDataSet(yVals, "x축 : 찍은날짜,  y축 : 탈모 비율");*/
        LineDataSet set1 = new LineDataSet(yVals, "");
        // set1.setFillAlpha(110);
        /*set1.setFillColor(Color.RED);*/
       /* set1.setFillColor(Color.BLACK);*/

        set1.setFillColor(Color.parseColor("#2eb74f"));

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.parseColor("#9b9b9b"));
        set1.setCircleColor(Color.parseColor("#2eb74f"));
        /*set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLUE);*/
        set1.setLineWidth(1f);
        set1.setCircleRadius(5);
        set1.setDrawCircleHole(true);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

       /* if(Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.graph_backround_red);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.GREEN);
        }*/

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        int k = 0;

        String xDotString = e.toString().substring(0, 20);
        Log.i("HistoryActivityLog", "HistoryActivityLog 5-1 xDot :" + xDotString);

        String xDotNum = xDotString.replaceAll("[^0-9]", "");
        Log.i("HistoryActivityLog", "HistoryActivityLog 5-2 xDot :" + xDotNum);




        Intent moveMainIntent = new Intent(HistoryActivity.this, HistoryResultActivity.class);
        //graphIndex 보내면됨
        moveMainIntent.putExtra("historyIndex", historyIndex.get(Integer.parseInt(xDotNum)));  //x축 index값을 보냄
        startActivity(moveMainIntent);

        Log.i("HistoryActivityLog", " HistoryActivityLog 5-3 Entry selected :" + e.toString());
        Log.i("HistoryActivityLog",  "HistoryActivityLog 5-4 " + String.valueOf(mChart.getHighestVisibleXIndex()));

        Log.i("HistoryActivityLog", "HistoryActivityLog 5-5 low: " + mChart.getLowestVisibleXIndex() + ", high: " + mChart.getHighestVisibleXIndex());
        Log.i("HistoryActivityLog", "HistoryActivityLog 5-6 xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());

    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 96, 77, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}
