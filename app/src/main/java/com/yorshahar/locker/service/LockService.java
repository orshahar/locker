package com.yorshahar.locker.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.yorshahar.locker.R;
import com.yorshahar.locker.activity.LockerMainActivity;
import com.yorshahar.locker.receiver.LockReceiver;

public class LockService extends Service implements LockReceiver.Delegate {

    private BroadcastReceiver mReceiver;
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

        updateLockerView();
    }

    private void updateLockerView() {
        ViewGroup lockerParent = (ViewGroup) lockerView.getParent();
        if (lockerParent != null) {
            lockerParent.removeView(lockerView);
        }

//        windowManager.addView(lockerView, params);
//        lockerView.setVisibility(View.VISIBLE);
//
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        lockerView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        windowManager.removeView(lockerView);
        activity = null;
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

// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new LockReceiver();
        ((LockReceiver) mReceiver).setDelegate(this);
        registerReceiver(mReceiver, filter);
        startForeground();

        Intent containerIntent = new Intent(this, LockerMainActivity.class);
        containerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(containerIntent);

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


//    @Override
//    public void onNotificationPosted(StatusBarNotification sbn) {
//        super.onNotificationPosted(sbn);
//    }
//
//    @Override
//    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
//        super.onNotificationPosted(sbn, rankingMap);
//    }
//
//    @Override
//    public void onNotificationRemoved(StatusBarNotification sbn) {
//        super.onNotificationRemoved(sbn);
//    }
//
//    @Override
//    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
//        super.onNotificationRemoved(sbn, rankingMap);
//    }
//
//    @Override
//    public void onListenerConnected() {
//        super.onListenerConnected();
//    }
//
//    @Override
//    public void onNotificationRankingUpdate(RankingMap rankingMap) {
//        super.onNotificationRankingUpdate(rankingMap);
//    }
//
//    @Override
//    public void onListenerHintsChanged(int hints) {
//        super.onListenerHintsChanged(hints);
//    }
//
//    @Override
//    public void onInterruptionFilterChanged(int interruptionFilter) {
//        super.onInterruptionFilterChanged(interruptionFilter);
//    }
//
//    @Override
//    public StatusBarNotification[] getActiveNotifications() {
//        return super.getActiveNotifications();
//    }
//
//    @Override
//    public StatusBarNotification[] getActiveNotifications(String[] keys) {
//        return super.getActiveNotifications(keys);
//    }
//
//    @Override
//    public RankingMap getCurrentRanking() {
//        return super.getCurrentRanking();
//    }

    // Unregister receiver
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

        if (lockerView != null) {
            windowManager.removeView(lockerView);
        }
    }


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
//        lockerView.setVisibility(View.INVISIBLE);
        if (lockerView != null) {
            try {
                windowManager.removeView(lockerView);
            } catch (Exception e) {
                int a = 2;
            }
        }
    }

    public class MyLocalBinder extends Binder {
        public LockService getService() {
            return LockService.this;
        }
    }

}
