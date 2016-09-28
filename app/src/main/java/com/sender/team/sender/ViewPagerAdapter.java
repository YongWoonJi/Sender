package com.sender.team.sender;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Tacademy on 2016-09-08.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return ViewPagerFragment.newInstance(position % MainActivity.VIEWPAGER_COUNT);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }
}
