package com.acemen.android.wasters.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.acemen.android.wasters.R;

/**
 * Created by Audrik ! on 01/09/2016.
 */
public class NavigationPager extends FragmentPagerAdapter {
    private final Context mContext;

    public NavigationPager(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ContributeFragment();
            case 1:
                return new OverviewFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.contribute);
            case 1:
                return mContext.getString(R.string.overview);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
