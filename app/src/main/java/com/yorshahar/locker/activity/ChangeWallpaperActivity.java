package com.yorshahar.locker.activity;

import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yorshahar.locker.R;
import com.yorshahar.locker.fragment.MyWallpapersFragment;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ChangeWallpaperActivity extends FragmentActivity implements View.OnClickListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    ImageView applyImageView;
    boolean buttonsVisible;
    List<Integer> resourceIds = Arrays.asList(R.drawable.wallpaper_iphone_stars, R.drawable.wallpaper_pebbles, R.drawable.wallpaper_bambook, R.drawable.wallpaper_bridge_purple, R.drawable.wallpaper_coffee, R.drawable.wallpaper_nexus5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_wallpaper);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            float currX;
            float xDiff;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        currX = event.getX();
                        xDiff = 0;
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (Math.abs(xDiff) < 10) {
                            if (buttonsVisible) {
                                hideButtons();
                            } else {
                                showButtons();
                            }
                        }

                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        float x = event.getX();
                        xDiff += currX - x;
                        currX = x;

                        break;
                    }
                    default: {
                        break;
                    }
                }

                return false;
            }
        });
        applyImageView = (ImageView) findViewById(R.id.applyImageView);
        applyImageView.setOnClickListener(this);

        buttonsVisible = true;
    }

    private void hideButtons() {
        applyImageView.animate()
                .translationY(250f)
                .setDuration(250)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        buttonsVisible = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    private void showButtons() {
        applyImageView.animate()
                .translationY(0)
                .setDuration(250)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        buttonsVisible = true;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_change_wallpaper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int itemIndex = mViewPager.getCurrentItem();
        int resourceId = resourceIds.get(itemIndex);
        setResult(resourceId);

        finish();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MyWallpapersFragment.newInstance(resourceIds.get(position));
        }

        @Override
        public int getCount() {
            return resourceIds.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }


    private class MyOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset > 0 && buttonsVisible) {
                hideButtons();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }
    }

}
