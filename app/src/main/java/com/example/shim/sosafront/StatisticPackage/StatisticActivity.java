package com.example.shim.sosafront.StatisticPackage;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.example.shim.sosafront.DatabasePackage.DataStore;
import com.example.shim.sosafront.R;

import java.util.ArrayList;

public class StatisticActivity extends AppCompatActivity {

    Toolbar mToolbar;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    DataStore dataStore;
    String authKey;

    private ArrayList<String> typeIndex = new ArrayList<String>();
    private ArrayList<String> ageIndex = new ArrayList<String>();
    private ArrayList<String> genderIndex = new ArrayList<String>();

    TypeUserFragment typeUserFragment = new TypeUserFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        /*new AsyncStatistic().execute();*/

        dataStore = new DataStore(this);
        authKey = dataStore.getValue("key", "");


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new StatisticFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(0);



        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

    }

}
