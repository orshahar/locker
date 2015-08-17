package com.yorshahar.locker.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yorshahar.locker.R;
import com.yorshahar.locker.fragment.LockerFragment;
import com.yorshahar.locker.fragment.PasscodeFragment;
import com.yorshahar.locker.fragment.SmartFragmentStatePagerAdapter;
import com.yorshahar.locker.model.notification.Notification;
import com.yorshahar.locker.service.LockService;
import com.yorshahar.locker.service.NotificationService;
import com.yorshahar.locker.service.connection.AbstractServiceConnectionImpl;

import java.util.Locale;


public class LockerMainActivity extends FragmentActivity implements NotificationService.Delegate {

    private AbstractServiceConnectionImpl lockServiceConnection;
    private AbstractServiceConnectionImpl notificationServiceConnection;
    private LockService lockService;
    private NotificationService notificationService;
    private boolean isLockServiceBound = false;
    private boolean isNotificationServiceBound = false;
    private boolean isWindowAttached = false;

    private ImageView dimView;
    private ViewPager mViewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;

    // Set appropriate flags to make the screen appear over the keyguard
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

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
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    //    @Override
//    protected void onStart() {
//        super.onStart();
//
//        Intent intent = new Intent(this, LockService.class);
//
////            unbindService(lockServiceConnection);
//        bindService(intent, lockServiceConnection, Context.BIND_AUTO_CREATE);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // unlock screen in case of app get killed by system
        if (getIntent() != null && getIntent().hasExtra("kill")
                && getIntent().getExtras().getInt("kill") == 1) {
            unlockDevice();
        } else {
            // listen the events get fired during the call
            StateListener phoneStateListener = new StateListener();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(sectionsPagerAdapter);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Display the lock screen fragment by default
        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (hasNotifications()) {
                    dimView.setAlpha(1.0f);
                } else {
                    if (position == 0) {
                        dimView.setAlpha((1.0f - positionOffset));
                    } else {
                        dimView.setAlpha(0.0f);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    PasscodeFragment passcodeFragment = (PasscodeFragment) sectionsPagerAdapter.getRegisteredFragment(0);
                    passcodeFragment.reset();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        dimView = (ImageView) findViewById(R.id.dimView);

        lockServiceConnection = new LockServiceConnection(LockService.class);
        notificationServiceConnection = new NotificationServiceConnection(NotificationService.class);

        bindToServices();
    }

    private boolean hasNotifications() {
        LockerFragment lockerFragment = (LockerFragment) sectionsPagerAdapter.getRegisteredFragment(1);
        return lockerFragment.hasNotifications();
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
            bindService(intent, lockServiceConnection, Context.BIND_AUTO_CREATE);
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

    // Handle events of calls and unlock screen if necessary
    private class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    unlockDevice();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    lockService.lock(true);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    }

    // Don't finish Activity on Back press
    @Override
    public void onBackPressed() {
        int a = 2;
    }

    // Handle button clicks
    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (keyCode == KeyEvent.KEYCODE_POWER)
                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {

            return true;
        }

        return false;

    }

    // handle the key press events here itself
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            return false;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME
                || event.getKeyCode() == KeyEvent.KEYCODE_MENU) {

            return true;
        }
        return false;
    }

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
    public class SectionsPagerAdapter extends SmartFragmentStatePagerAdapter implements PasscodeFragment.FragmentDelegate {

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

    }

    //Simply unlock device by finishing the activity
    private void unlockDevice() {
        lockService.unlock();
        reset();
//        finish();
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
    protected void onDestroy() {
        if (isLockServiceBound) {
            unbindService(lockServiceConnection);
        }

        super.onDestroy();
    }

    public void reset() {
        mViewPager.setCurrentItem(1, true);

        bindToServices();
    }


    @Override
    public void onNotification(Notification notification) {
        LockerFragment lockerFragment = (LockerFragment) sectionsPagerAdapter.getRegisteredFragment(1);
        lockerFragment.addNotification(notification);
    }


}
