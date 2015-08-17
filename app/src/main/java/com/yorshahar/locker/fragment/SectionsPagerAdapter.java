package com.yorshahar.locker.fragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.

 * Created by yorshahar on 8/7/15.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yorshahar.locker.R;

import java.util.Locale;

public class SectionsPagerAdapter extends FragmentPagerAdapter implements PasscodeFragment.FragmentDelegate {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0: {
                fragment = new PasscodeFragment();
                ((PasscodeFragment) fragment).setDelegate(this);
                break;
            }
            case 1: {
                fragment = new LockerFragment();
                break;
            }
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "Section 1";
            case 1:
                return "Sections2";
        }
        return null;
    }

    ////////////////////////////////////////////////////
    // PasscodeFragment.FragmentDelegate
    ////////////////////////////////////////////////////

    @Override
    public void onPasscodePassed() {
//        mLockscreenUtils.unlock();
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
