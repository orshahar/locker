package com.yorshahar.locker.activity;

import android.animation.Animator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.font.FontLoader;
import com.yorshahar.locker.fragment.ControlCenterFragment;
import com.yorshahar.locker.fragment.LockerFragment;
import com.yorshahar.locker.fragment.PasscodeFragment;
import com.yorshahar.locker.fragment.SmartFragmentStatePagerAdapter;
import com.yorshahar.locker.model.notification.Notification;
import com.yorshahar.locker.receiver.ControlCenterReceiver;
import com.yorshahar.locker.service.LockService;
import com.yorshahar.locker.service.NotificationService;
import com.yorshahar.locker.service.connection.AbstractServiceConnectionImpl;
import com.yorshahar.locker.ui.widget.AppLauncherView;
import com.yorshahar.locker.ui.widget.FreezableViewPager;
import com.yorshahar.locker.ui.widget.ToggleButtonView;
import com.yorshahar.locker.util.BlurUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LockerMainActivity extends FragmentActivity implements NotificationService.Delegate, LockService.Delegate, ControlCenterFragment.Delegate, ControlCenterReceiver.Receiver {

    private static final String STATUS_BAR_TIME_FORMAT = "h:mm a";
    private static final int CONTROL_CENTER_COLOR_ON = 0xffcccccc;
    private static final int CONTROL_CENTER_COLOR_OFF = 0x00ffffff;
    private static final int CONTROL_CENTER_MAX_INTENSITY = 50;


    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    private int CONTROL_CENTER_HEIGHT;
    private int BAR_HEIGHT;

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
    private FreezableViewPager mViewPager;
    private SectionsPagerAdapter pagerAdapter;
    private TextView batteryLevelTextView;
    private TextView carrierTextView;
    private ImageView batteryFillImageView;
    private ImageView batteryChargeAnimation;
    private ImageView barImageView;
    private Bitmap blurredBackground;
    private ImageView[] signalCircles;
    private View controlCenterView;
    private ImageView controlCenterBackground;
    private ImageView controlCenterGlass;
    private ImageView controlCenterPullBar;
    private ImageView controlCenterPullBarDown;
    private RelativeLayout controlCenterTopBar;
    private View totalDimView;
    private ControlCenterFragment controlCenterFragment;

    public ControlCenterFragment getControlCenterFragment() {
        return controlCenterFragment;
    }

    public void setControlCenterFragment(ControlCenterFragment controlCenterFragment) {
        this.controlCenterFragment = controlCenterFragment;
        controlCenterFragment.setDelegate(this);
    }

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
//        controlCenterView.getLayoutParams().height;
//        controlCenterView.getCalculatedHeight();

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
        mViewPager = (FreezableViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);

        // Display the lock screen fragment by default
        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                totalDimView.setBackgroundColor(0xff000000);
                if (position == 0) {
                    dimView.setAlpha(hasNotifications() ? 1.0f : 1.0f - positionOffset);
                    barImageView.setTranslationY(-BAR_HEIGHT * (1.0f - positionOffset));
                    controlCenterView.setY(SCREEN_HEIGHT - BAR_HEIGHT * (positionOffset));
                } else {
                    dimView.setAlpha(hasNotifications() ? 1.0f : 0.0f);
                    barImageView.setTranslationY(0);
                    controlCenterView.setY(SCREEN_HEIGHT - BAR_HEIGHT);
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

        controlCenterView = findViewById(R.id.controlCenterFragment);
        controlCenterView.setBackgroundColor(Color.TRANSPARENT);

        controlCenterBackground = (ImageView) controlCenterView.findViewById(R.id.background);
        controlCenterBackground.setBackground(new BitmapDrawable(getResources(), blurredBackground));
        controlCenterBackground.setVisibility(View.INVISIBLE);

        controlCenterGlass = (ImageView) controlCenterView.findViewById(R.id.glassImageView);
        controlCenterGlass.setVisibility(View.INVISIBLE);

        CONTROL_CENTER_HEIGHT = 840; //controlCenterView.getLayoutParams().height;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;

        controlCenterTopBar = (RelativeLayout) controlCenterView.findViewById(R.id.topBar);

        BAR_HEIGHT = controlCenterTopBar.getLayoutParams().height;

        controlCenterView.setY(SCREEN_HEIGHT - BAR_HEIGHT);

        controlCenterTopBar.setOnTouchListener(new View.OnTouchListener() {
            float dY;
            float y;
            boolean slideUp;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN: {
                        mViewPager.freeze();
                        controlCenterBackground.setVisibility(View.VISIBLE);
                        controlCenterGlass.setVisibility(View.VISIBLE);
                        dY = controlCenterView.getY() - event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        controlCenterView.animate()
                                .y(slideUp ? SCREEN_HEIGHT - CONTROL_CENTER_HEIGHT : SCREEN_HEIGHT - BAR_HEIGHT)
                                .setDuration(100)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        y = slideUp ? SCREEN_HEIGHT - CONTROL_CENTER_HEIGHT : SCREEN_HEIGHT - BAR_HEIGHT;

                                        if (y == SCREEN_HEIGHT - BAR_HEIGHT) {
                                            mViewPager.unfreeze();
                                            controlCenterBackground.setVisibility(View.INVISIBLE);
                                            controlCenterGlass.setVisibility(View.INVISIBLE);

//                                            controlCenterPullBar.setVisibility(View.VISIBLE);
//                                            controlCenterPullBarDown.setVisibility(View.INVISIBLE);
                                        } else {
                                            controlCenterPullBar.setVisibility(View.INVISIBLE);
                                            controlCenterPullBarDown.setVisibility(View.VISIBLE);

                                        }

                                        float dim = y / SCREEN_HEIGHT;
                                        totalDimView.setAlpha(1.0f - dim);
                                        controlCenterBackground.setTranslationY(-y);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .start();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        controlCenterPullBar.setVisibility(View.VISIBLE);
                        controlCenterPullBarDown.setVisibility(View.INVISIBLE);

                        float newY = event.getRawY() + dY;
                        slideUp = newY < y;
                        y = newY;
                        if (y > SCREEN_HEIGHT - BAR_HEIGHT) {
                            y = SCREEN_HEIGHT - BAR_HEIGHT;
                        } else if (y < SCREEN_HEIGHT - CONTROL_CENTER_HEIGHT) {
                            y = SCREEN_HEIGHT - CONTROL_CENTER_HEIGHT;
                        }
                        controlCenterView.setY(y);
                        float dim = y / SCREEN_HEIGHT;
                        totalDimView.setAlpha(1.0f - dim);
                        controlCenterBackground.setTranslationY(-y);
                        break;
                    }
                    default: {
                        return false;
                    }
                }
                return true;
            }

        });

        controlCenterPullBar = (ImageView) controlCenterView.findViewById(R.id.pullBarImageView);
        controlCenterPullBar.setVisibility(View.VISIBLE);

        controlCenterPullBarDown = (ImageView) controlCenterView.findViewById(R.id.pullBarDownImageView);
        controlCenterPullBarDown.setVisibility(View.INVISIBLE);


        totalDimView = findViewById(R.id.totalDim);
        totalDimView.setBackgroundColor(0x00000000);
        totalDimView.setAlpha(0);

        lockServiceConnection = new

                LockServiceConnection(LockService.class);
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
//
// LockService.Delegate
//
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

    @Override
    public void onAirplaneModeEnabled() {
        controlCenterFragment.toggleAirplainModeOn();
    }

    @Override
    public void onAirplaneModeDisabled() {
        controlCenterFragment.toggleAirplainModeOff();
    }

    @Override
    public void onWifiEnabled() {
        controlCenterFragment.toggleWifiOn();
    }

    @Override
    public void onWifiDisabled() {
        controlCenterFragment.toggleWifiOff();
    }

    @Override
    public void onBluetoothEnabled() {
        controlCenterFragment.toggleBluetoothOn();
    }

    @Override
    public void onBluetoothDisabled() {
        controlCenterFragment.toggleBluetoothOff();
    }

    @Override
    public void onFlashlightTurnedOn() {
        controlCenterFragment.updateAppLauncher(ControlCenterFragment.AppLauncher.FLASHLIGHT, AppLauncherView.State.ON);
    }

    @Override
    public void onFlashlightTurnedOff() {
        controlCenterFragment.updateAppLauncher(ControlCenterFragment.AppLauncher.FLASHLIGHT, AppLauncherView.State.OFF);
    }

//////////////////////////////////////////////////////////////////
//
// ControlCenterFragment.Delegate
//
//////////////////////////////////////////////////////////////////

    @Override
    public void onToggleButtonStateChanged(ControlCenterFragment.ToggleButtonType toggleButtonType, ToggleButtonView.State state) {

    }

    @Override
    public void onAppLauncherStateChanged(ControlCenterFragment.AppLauncher appLauncherType, AppLauncherView.State state) {
        switch (appLauncherType) {
            case FLASHLIGHT: {
                switch (state) {
                    case ON: {
//                        final ControlCenterReceiver controlCenterReceiver = new ControlCenterReceiver(new Handler());
//                        controlCenterReceiver.setReceiver(this);
//
//                        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, ControlCenterService.class);
//                        intent.putExtra("receiver", controlCenterReceiver);
//                        intent.putExtra("turnOn", true);
//
//                        startService(intent);

                        lockService.turnFlashlightOn();
//                        controlCenterFragment.updateAppLauncher(ControlCenterFragment.AppLauncher.FLASHLIGHT, AppLauncherView.State.ON);

                        break;
                    }
                    case OFF: {
//                        final ControlCenterReceiver controlCenterReceiver = new ControlCenterReceiver(new Handler());
//                        controlCenterReceiver.setReceiver(this);
//                        controlCenterReceiver.setCamera(camera);
//
//                        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, ControlCenterService.class);
//                        intent.putExtra("receiver", controlCenterReceiver);
//                        intent.putExtra("turnOn", false);
//
//                        startService(intent);

                        lockService.turnFlashlightOff();
//                        controlCenterFragment.updateAppLauncher(ControlCenterFragment.AppLauncher.FLASHLIGHT, AppLauncherView.State.OFF);

                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onScreenBrightnessChanged(int brightness) {
        lockService.onScreenBrightnessChanged(brightness);
    }

//////////////////////////////////////////////////////////////////
//
// ControlCenterReceiver.Receiver
//
//////////////////////////////////////////////////////////////////

//    @Override
//    public void onFlashlightTurnedOn() {
//        controlCenterFragment.updateAppLauncher(ControlCenterFragment.AppLauncher.FLASHLIGHT, AppLauncherView.State.ON);
//    }
//
//    @Override
//    public void onFlashlightTurnedOff() {
//        controlCenterFragment.updateAppLauncher(ControlCenterFragment.AppLauncher.FLASHLIGHT, AppLauncherView.State.OFF);
//    }

}