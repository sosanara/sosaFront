package com.example.shim.sosafront.StatisticPackage;

/**
 * Created by shim on 2016-04-23.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by shim on 2016-04-20.
 */
public class testAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "유형별", "나이별", "성 별" };

    public testAdapter(FragmentManager fm) {
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
            case 2: // Fragment # 1 - This will show SecondFragment
                return GenderFragment.newInstance(2);
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

