package com.example.shim.sosafront.StatisticPackage;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

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


/**
 * Created by shim on 2016-04-20.
 */


public class TypeUserFragment extends Fragment  {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private String authKey;
    private DataStore dataStore;

    private ArrayList<String> typeIndex = new ArrayList<String>();
    private ArrayList<String> ageIndex = new ArrayList<String>();




    TextView tvX, tvY;
    Typeface tf;
    PieChart mChart;

    String[] mParties = new String[] {                                                                                               //수정 타입별, 나이별, 성별 각각 배열 생성
            "정상", "앞머리형", "뒷머리형", "부분형", "가르마형", "최종형"
    };





    public static TypeUserFragment newInstance(int page) {


        Log.d("test", "실행순서1");
        Log.d("TypeUserFragment", "TypeUserFragment 테스트 0-0-0 : 페이지 = " + page);
        Bundle args = new Bundle();
        args.putInt("page", page);
        TypeUserFragment fragment = new TypeUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("page");



        String strtext = getActivity().getIntent().getStringExtra("GOOD");
        Log.d("test", "실행순서2" + strtext + "authKey");

    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String strtext2 = getActivity().getIntent().getStringExtra("GOOD");
        Log.d("test", "실행순서3" + strtext2);
        new AsyncStatistic().execute();

        View view = inflater.inflate(R.layout.fragment_age_piechart, container, false);

        mChart = (PieChart) view.findViewById(R.id.chart1);
        tvX = (TextView) view.findViewById(R.id.tvXMax);
        tvY = (TextView) view.findViewById(R.id.tvYMax);




        /*mChart = (PieChart) view.findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setCenterText(generateCenterSpannableText());

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);


        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        setData(5, 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);*/

        Log.d("TypeUserFragment", "TypeUserFragment 테스트 0-0 : 페이지 = " + mPage);

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }




    private void setData(int count, float range) {

        float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        for (int i = 0; i < count + 1; i++) {
            /*yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));*/
            yVals1.add(new Entry(20, i));                                                                                   //수정 서버에서 받은값 여기에 저장
        }

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count + 1; i++)
            xVals.add(mParties[i % mParties.length]);

        PieDataSet dataSet = new PieDataSet(yVals1, typeIndex.get(2));
        /*PieDataSet dataSet = new PieDataSet(yVals1, typeIndex.get(2));*/
        /*PieDataSet dataSet = new PieDataSet(yVals1, "Election Results");*/
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList
                <Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        /*data.setValueTypeface(tf);*/
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Gender\ndeveloped by Minho Shim");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }




    private class AsyncStatistic extends AsyncTask<String, String, String> implements OnChartValueSelectedListener {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
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
                url = new URL("http://113.198.84.37/api/v1/userInfo/statistic/");
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception fail!";
            }

            try {
                dataStore = new DataStore(getActivity());
                authKey = dataStore.getValue("key", "");

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
                //여기서 로그인 페이지로 이동
                int response_code = conn.getResponseCode();

                Log.d("TypeUserFragmentLog", "TypeUserFragmentLog 0-0: " + conn.getResponseCode());

                // Check if successful connection made

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("TypeUserFragmentLog", "TypeUserFragmentLog 1: " + result.toString());
                    Log.d("TypeUserFragmentLog", "TypeUserFragmentLog 1-1: " + reader);

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("TypeUserFragmentLog", "TypeUserFragmentLog 2: " + result.toString());  // result.toString()
                    Log.d("TypeUserFragmentLog", "TypeUserFragmentLog 2-1: " + reader);    //java.io.BufferedReader@5015f88

                    String value = result.toString();
                    JSONObject valueJsonObject = new JSONObject(value);

                    Log.d("TypeUserFragmentLog", "TypeUserFragmentLog 2-2: " + valueJsonObject.get("value"));

                    JSONObject subTypeJsonObject = new JSONObject(String.valueOf(valueJsonObject.get("value")));
                    JSONObject allUserTypeJsonObjectKey = new JSONObject(String.valueOf(subTypeJsonObject.get("all_users_type")));
                    JSONObject sameAgeTypeJsonObjectKey = new JSONObject(String.valueOf(subTypeJsonObject.get("same_ages_type")));
                    JSONObject genderUsersTypeJsonObjectKey = new JSONObject(String.valueOf(subTypeJsonObject.get("gender_users_type")));

                    Log.d("TypeUserFragmentLog", "TypeUserFragmentLog 2-3: " + subTypeJsonObject.get("all_users_type"));

                    typeIndex.add(0, String.valueOf(allUserTypeJsonObjectKey.get("0")));
                    typeIndex.add(1, String.valueOf(allUserTypeJsonObjectKey.get("1")));
                    typeIndex.add(2, String.valueOf(allUserTypeJsonObjectKey.get("2")));
                    typeIndex.add(3, String.valueOf(allUserTypeJsonObjectKey.get("3")));
                    typeIndex.add(4, String.valueOf(allUserTypeJsonObjectKey.get("4")));

                    ageIndex.add(0, String.valueOf(sameAgeTypeJsonObjectKey.get("0")));
                    ageIndex.add(1, String.valueOf(sameAgeTypeJsonObjectKey.get("1")));
                    ageIndex.add(2, String.valueOf(sameAgeTypeJsonObjectKey.get("2")));
                    ageIndex.add(3, String.valueOf(sameAgeTypeJsonObjectKey.get("3")));
                    ageIndex.add(4, String.valueOf(sameAgeTypeJsonObjectKey.get("4")));



                    for(int i = 0; i < 5; i++) {
                        Log.d("TypeUserFragmentLog", "TypeUserFragmentLog 2-5: type: " + typeIndex.get(i));
                        Log.d("TypeUserFragmentLog", "TypeUserFragmentLog 2-6: age" + ageIndex.get(i));
                    }

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

            pdLoading.dismiss();

            mChart.setUsePercentValues(true);
            mChart.setDescription("");
            mChart.setExtraOffsets(5, 10, 5, 5);

            mChart.setDragDecelerationFrictionCoef(0.95f);
            mChart.setCenterText(generateCenterSpannableText());

            mChart.setDrawHoleEnabled(true);
            mChart.setHoleColor(Color.WHITE);

            mChart.setTransparentCircleColor(Color.WHITE);
            mChart.setTransparentCircleAlpha(110);

            mChart.setHoleRadius(58f);
            mChart.setTransparentCircleRadius(61f);

            mChart.setDrawCenterText(true);

            mChart.setRotationAngle(0);
            // enable rotation of the chart by touch
            mChart.setRotationEnabled(true);
            mChart.setHighlightPerTapEnabled(true);


            // add a selection listener
            mChart.setOnChartValueSelectedListener(this);

            setData(5, 100);

            mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            // mChart.spin(2000, 0, 360);

            Legend l = mChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

        }


        @Override
        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

            if (e == null)
                return;
            Log.i("VAL SELECTED",
                    "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                            + ", DataSet index: " + dataSetIndex);
        }

        @Override
        public void onNothingSelected() {
            Log.i("PieChart", "nothing selected");
        }




    }






}