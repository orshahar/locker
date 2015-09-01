package com.yorshahar.locker.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.font.FontLoader;
import com.yorshahar.locker.fragment.LockerFragment;
import com.yorshahar.locker.fragment.PasscodeFragment;
import com.yorshahar.locker.fragment.SmartFragmentStatePagerAdapter;
import com.yorshahar.locker.model.notification.Notification;
import com.yorshahar.locker.service.LockService;
import com.yorshahar.locker.service.NotificationService;
import com.yorshahar.locker.service.connection.AbstractServiceConnectionImpl;
import com.yorshahar.locker.util.BlurUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LockerMainActivity extends FragmentActivity implements NotificationService.Delegate, LockService.Delegate {

    private static final String STATUS_BAR_TIME_FORMAT = "h:mm a";
    private float BAR_MAX_OFFSET = 30;

    private AbstractServiceConnectionImpl lockServiceConnection;
    //    private AbstractServiceConnectionImpl notificationServiceConnection;
    private LockService lockService;
    private NotificationService notificationService;
    private boolean isLockServiceBound = false;
    private boolean isNotificationServiceBound = false;
    private boolean isWindowAttached = false;
    private DateFormat dateFormat;
    private Animation chargeAnimation;
    private ImageView wallpaperView;
    private ImageView dimView;
    private ViewPager mViewPager;
    private SectionsPagerAdapter pagerAdapter;
    private TextView batteryLevelTextView;
    private TextView carrierTextView;
    private ImageView batteryFillImageView;
    private ImageView batteryChargeAnimation;
    private ImageView barImageView;
    private TextView clockTextView;
    private Bitmap blurredBackground;
    private ImageView[] signalCircles;
    private View controlCenterView;
    private ImageView controlCenterPullBar;
    private ImageView phoneImageView;
    private ImageView cameraImageView;

    public ImageView getWallpaperView() {
        return wallpaperView;
    }

    public Bitmap getBlurredBackground() {
        return blurredBackground;
    }

    // Set appropriate flags to make the screen appear over the keyguard
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        int height = size.y;
//
//        controlCenterView.setTop(height - 20);

        isWindowAttached = true;
        sendViewToService();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        isWindowAttached = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

//        if (!isBound) {
//            Intent intent = new Intent(this, LockService.class);
//            bindService(intent, lockServiceConnection, Context.BIND_AUTO_CREATE);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        mViewPager.setCurrentItem(1);
//        sectionsPagerAdapter.getItem(0)
//        sectionsPagerAdapter.instantiateItem(mViewPager, 0);
//        reset();

//        if (!isBound) {
//            Intent intent = new Intent(this, LockService.class);
//            bindService(intent, lockServiceConnection, Context.BIND_AUTO_CREATE);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();

//        unbindService(lockServiceConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        moveTaskToBack(true);
        setContentView(R.layout.main_activity);

        dateFormat = new SimpleDateFormat(STATUS_BAR_TIME_FORMAT, Locale.getDefault());

        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);

        // Display the lock screen fragment by default
        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    dimView.setAlpha(hasNotifications() ? 1.0f : 1.0f - positionOffset);
                    barImageView.setTranslationY(-46 * (1.0f - positionOffset));
                    controlCenterView.setTranslationY(754 + 46 * (1.0f - positionOffset));
                    phoneImageView.setTranslationX(-80 * (1.0f - positionOffset));
                    cameraImageView.setTranslationX(754 * (1.0f - positionOffset));
                    clockTextView.setAlpha(1.0f - positionOffset);
                } else {
                    dimView.setAlpha(hasNotifications() ? 1.0f : 0.0f);
                    barImageView.setTranslationY(0);
                    controlCenterView.setTranslationY(754);
                    phoneImageView.setTranslationX(0);
                    cameraImageView.setTranslationX(0);
                    clockTextView.setAlpha(0.0f);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    PasscodeFragment passcodeFragment = (PasscodeFragment) pagerAdapter.getRegisteredFragment(0);
                    passcodeFragment.reset();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        wallpaperView = (ImageView) findViewById(R.id.backgroundImageView);
        Bitmap bm = ((BitmapDrawable) wallpaperView.getDrawable()).getBitmap();
        blurredBackground = BlurUtil.blur(bm);

        dimView = (ImageView) findViewById(R.id.dimView);
        dimView.setImageResource(0);
        dimView.setImageBitmap(blurredBackground);
        dimView.getDrawable().setColorFilter(new LightingColorFilter(0x88888888, 0x00000000));

        signalCircles = new ImageView[5];
        signalCircles[0] = (ImageView) findViewById(R.id.signal1ImageView);
        signalCircles[1] = (ImageView) findViewById(R.id.signal2ImageView);
        signalCircles[2] = (ImageView) findViewById(R.id.signal3ImageView);
        signalCircles[3] = (ImageView) findViewById(R.id.signal4ImageView);
        signalCircles[4] = (ImageView) findViewById(R.id.signal5ImageView);

        carrierTextView = (TextView) findViewById(R.id.carrierTextView);
        carrierTextView.setTypeface(FontLoader.getTypeface(getApplicationContext(), FontLoader.HELVETICA_NEUE_LIGHT));
        carrierTextView.setTextColor(Color.WHITE);

        batteryLevelTextView = (TextView) findViewById(R.id.batteryLevelTextView);
        batteryLevelTextView.setTypeface(FontLoader.getTypeface(getApplicationContext(), FontLoader.HELVETICA_NEUE_LIGHT));
        batteryLevelTextView.setTextColor(Color.WHITE);

        batteryFillImageView = (ImageView) findViewById(R.id.batteryFillImageView);

        batteryChargeAnimation = (ImageView) findViewById(R.id.batteryChargeAnimation);
        chargeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.charging_story_animation);

        barImageView = (ImageView) findViewById(R.id.barImageView);

        clockTextView = (TextView) findViewById(R.id.clockTextView);
        clockTextView.setAlpha(0.0f);
        clockTextView.setText(dateFormat.format(System.currentTimeMillis()));
        clockTextView.setTypeface(FontLoader.getTypeface(getApplicationContext(), FontLoader.HELVETICA_NEUE_BOLD));

        controlCenterView = findViewById(R.id.controlCenterFragment);
        controlCenterView.setBackgroundColor(Color.TRANSPARENT);
        controlCenterView.setTranslationY(controlCenterView.getLayoutParams().height - 46);

        controlCenterPullBar = (ImageView) controlCenterView.findViewById(R.id.pullBarImageView);

        phoneImageView = (ImageView) findViewById(R.id.phoneImageView);
        phoneImageView.setVisibility(View.INVISIBLE);

        cameraImageView = (ImageView) findViewById(R.id.cameraImageView);

        lockServiceConnection = new LockServiceConnection(LockService.class);
//        notificationServiceConnection = new NotificationServiceConnection(NotificationService.class);

        bindToServices();
    }

    @Override
    protected void onDestroy() {
        if (isLockServiceBound) {
            unbindService(lockServiceConnection);
        }

        super.onDestroy();
    }

    private boolean hasNotifications() {
        LockerFragment lockerFragment = (LockerFragment) pagerAdapter.getRegisteredFragment(1);
        return lockerFragment.hasNotifications();
    }

    public void updateTimes() {
        clockTextView.setText(dateFormat.format(System.currentTimeMillis()));

        LockerFragment lockerFragment = (LockerFragment) pagerAdapter.getRegisteredFragment(1);
        if (lockerFragment != null) {
            lockerFragment.update();
        }
    }

    private class LockServiceConnection extends AbstractServiceConnectionImpl {

        public LockServiceConnection(Class clazz) {
            super(clazz);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LockService.MyLocalBinder binder = (LockService.MyLocalBinder) service;
            lockService = binder.getService();
            isLockServiceBound = true;

            lockService.setActivity(LockerMainActivity.this);
            lockService.setDelegate(LockerMainActivity.this);
            sendViewToService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isLockServiceBound = false;
        }
    }

    private class NotificationServiceConnection extends AbstractServiceConnectionImpl {

        public NotificationServiceConnection(Class clazz) {
            super(clazz);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            notificationService = NotificationService.getInstance();
            if (notificationService != null) {
                isNotificationServiceBound = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isNotificationServiceBound = false;
        }
    }

    private void sendViewToService() {
        if (isLockServiceBound && isWindowAttached) {
            lockService.setLockerView((RelativeLayout) findViewById(R.id.layout));
        }
    }

    private void bindToServices() {
        if (!isLockServiceBound) {
            Intent intent = new Intent(this, lockServiceConnection.getClazz());
            bindService(intent, lockServiceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
        }

//        if (!isNotificationServiceBound) {
//            Intent intent = new Intent(this, notificationServiceConnection.getClazz());
//            bindService(intent, notificationServiceConnection, Context.BIND_AUTO_CREATE);
//        }

        if (!isNotificationServiceBound) {
            notificationService = NotificationService.getInstance();
            if (notificationService != null) {
                notificationService.setDelegate(this);
                isNotificationServiceBound = true;
            }
        }
    }

//    // Don't finish Activity on Back press
//    @Override
//    public void onBackPressed() {
//        int a = 2;
//    }
//
////    // Handle button clicks
////    @Override
////    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
////        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
////                || (keyCode == KeyEvent.KEYCODE_POWER)
////                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
////                || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
////            return true;
////        }
////        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
////
////            return true;
////        }
////
////        return false;
////
////    }
//
//    // handle the key press events here itself
//    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
//                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
//                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
//            return false;
//        }
//        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME
//                || event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
//
//            return true;
//        }
//        return false;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends SmartFragmentStatePagerAdapter implements LockerFragment.Delegate, PasscodeFragment.Delegate {

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
                    ((LockerFragment) fragment).setDelegate(this);
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
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }

        ////////////////////////////////////////////////////
        // PasscodeFragment.FragmentDelegate
        ////////////////////////////////////////////////////

        @Override
        public void onPasscodePassed() {
            unlockDevice();
        }

        @Override
        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            mViewPager.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        ////////////////////////////////////////////////////
        // LockerFragment.FragmentDelegate
        ////////////////////////////////////////////////////

        @Override
        public List<Notification> getNotifications() {
            return notificationService != null ? notificationService.getNotifications() : new ArrayList<Notification>();
        }

        @Override
        public void deleteNotification(Notification notification) {
            if (notificationService != null) {
                notificationService.deleteNotification(notification);
            }
        }

        @Override
        public void onNotificationsChanged() {
            updateBackgroundBlur();
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        mViewPager.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    //Simply unlock device by finishing the activity
    private void unlockDevice() {
        lockService.unlock();
        reset();
//        finish(); // TODO: should I call finish() here or in the service?
    }

    public void reset() {
        mViewPager.setCurrentItem(1, true);

        bindToServices();
    }


    @Override
    public void onNotification(Notification notification) {
        LockerFragment lockerFragment = (LockerFragment) pagerAdapter.getRegisteredFragment(1);
        lockerFragment.update();

        updateBackgroundBlur();
    }

    @Override
    public void onNotificationsChanged(List<Notification> notifications) {
        LockerFragment lockerFragment = (LockerFragment) pagerAdapter.getRegisteredFragment(1);
        lockerFragment.updateNotifications(notifications);

        updateBackgroundBlur();
    }

    private void updateBackgroundBlur() {
        LockerFragment lockerFragment = (LockerFragment) pagerAdapter.getRegisteredFragment(1);

        if (mViewPager.getCurrentItem() == 1) {
            if (lockerFragment.hasNotifications()) {
                dimView.animate()
                        .alpha(1.0f)
                        .setDuration(1000)
                        .start();
            } else {
                dimView.animate()
                        .alpha(0.0f)
                        .setDuration(1000)
                        .start();
            }
        }

    }
//////////////////////////////////////////////////////////////////
// LockService.Delegate
//////////////////////////////////////////////////////////////////

    @Override
    public void onBatteryLevelChanged(int level, int status) {
        boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING;

        batteryLevelTextView.setText(String.valueOf(level) + "%");
        batteryFillImageView.setScaleX(level / 100.0f);
        batteryFillImageView.setTranslationX(-batteryFillImageView.getWidth() * (100.0f - level) / 100.0f / 2.0f);

        if (charging) {
            batteryFillImageView.setImageResource(R.drawable.battery_fill_charging);
            batteryChargeAnimation.setVisibility(View.VISIBLE);
            batteryChargeAnimation.getLayoutParams().width = 10;
            batteryChargeAnimation.startAnimation(chargeAnimation);
            setMargin(batteryChargeAnimation, 5, 0, 0, 0);
        } else {
            batteryFillImageView.setImageResource(R.drawable.battery_fill);
            batteryChargeAnimation.setVisibility(View.INVISIBLE);
            batteryChargeAnimation.getLayoutParams().width = 0;
            batteryChargeAnimation.clearAnimation();
            setMargin(batteryChargeAnimation, 0, 0, 0, 0);
        }


    }

    @Override
    public void onSignalStrengthsChanged(int bars) {
        for (int i = 0; i < 5; i++) {
            if (i < bars) {
                signalCircles[i].setImageResource(R.drawable.signal_fill);
            } else {
                signalCircles[i].setImageResource(R.drawable.signal_empty);
            }
        }
    }

    private void setMargin(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            marginLayoutParams.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }


}
