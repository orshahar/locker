package com.yorshahar.locker.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.yorshahar.locker.R;
import com.yorshahar.locker.activity.LockerMainActivity;
import com.yorshahar.locker.receiver.BootReceiver;
import com.yorshahar.locker.receiver.LockReceiver;
import com.yorshahar.locker.receiver.TimeReceiver;

public class LockService extends Service implements LockReceiver.Delegate, TimeReceiver.Delegate {

    private BroadcastReceiver lockReceiver;
    private BroadcastReceiver bootReceiver;
    private BroadcastReceiver timeReceiver;
    private WindowManager windowManager;
    private LockerMainActivity activity;
    private final IBinder myBinder = new MyLocalBinder();

    private RelativeLayout lockerView;
    private WindowManager.LayoutParams params;

    public LockService() {
    }

    public LockerMainActivity getActivity() {
        return activity;
    }

    public void setActivity(LockerMainActivity activity) {
        this.activity = activity;
    }

    public void setLockerView(RelativeLayout lockerView) {
        this.lockerView = lockerView;

        ViewGroup lockerParent = (ViewGroup) lockerView.getParent();
        if (lockerParent != null) {
            lockerParent.removeView(lockerView);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        try {
            windowManager.removeView(lockerView);
        } catch (IllegalArgumentException ignored) {

        }

//        activity = null;
        lockerView = null;

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: Register all these receivers as part of the settings activity

        // Register the lock receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        lockReceiver = new LockReceiver();
        ((LockReceiver) lockReceiver).setDelegate(this);
        registerReceiver(lockReceiver, filter);

        // Register the boot receiver
        filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_USER_INITIALIZE); // Required API 17 :(
        bootReceiver = new BootReceiver();
        registerReceiver(bootReceiver, filter);

        // Register the time receiver
        filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        timeReceiver = new TimeReceiver();
        ((TimeReceiver) timeReceiver).setDelegate(this);
        registerReceiver(timeReceiver, filter);

        startForeground();

        // TODO: Should I start the activity here?
        startLockerActivity();

        return START_STICKY;
    }

    // Run service in foreground so it is less likely to be killed by system
    private void startForeground() {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText("Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(null)
                .setOngoing(true)
                .setAutoCancel(true)
                .build();
        startForeground(9999, notification);
    }

    private void startLockerActivity() {
        Intent lockerIntent = new Intent(this, LockerMainActivity.class);
        lockerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(lockerIntent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    // Unregister receiver
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (activity != null) {
            activity.finish();
            activity = null;
        }

        if (lockerView != null) {
            windowManager.removeView(lockerView);
        }

        unregisterReceiver(lockReceiver);
        unregisterReceiver(bootReceiver);
        unregisterReceiver(timeReceiver);
    }

//////////////////////////////////////////////////////////////////////
// LockReceiver.Delegate
//////////////////////////////////////////////////////////////////////

    @Override
    public void lock(boolean reset) {
        if (reset && activity != null) {
            activity.reset();
        }

        if (reset && lockerView != null) {
            try {
                windowManager.addView(lockerView, params);

                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                lockerView.setSystemUiVisibility(uiOptions);
                lockerView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                int a = 2;
            }
        }

    }

    @Override
    public void unlock() {
        if (lockerView != null) {
            try {
                windowManager.removeView(lockerView);
            } catch (Exception e) {
                int a = 2;
            }
        }
    }

//////////////////////////////////////////////////////////////////////
// TimeReceiver.Delegate
//////////////////////////////////////////////////////////////////////

    @Override
    public void onTimeTicked() {
        if (activity != null) {
            activity.updateTimes();
        }
    }

    @Override
    public void onDateChanged() {
        int a = 2;
    }

    public class MyLocalBinder extends Binder {
        public LockService getService() {
            return LockService.this;
        }
    }

}
