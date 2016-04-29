package com.example.shim.sosafront.StatisticPackage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by shim on 2016-04-20.
 */
public class StatisticFragmentPagerAdapter extends FragmentStatePagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "유형별", "나이별"};
    private ArrayList<TypeUserFragment> mFragments;


    public StatisticFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;

    }

    @Override
    public Fragment getItem(int position) {

        Log.d("제발제발", "제발제발 : " + position);
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return TypeUserFragment.newInstance(0);

            case 1: // Fragment # 0 - This will show FirstFragment different title
                return AgeFragment.newInstance(1);

            default:
                return null;
        }


    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
