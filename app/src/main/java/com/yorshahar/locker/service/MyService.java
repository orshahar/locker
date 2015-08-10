package com.yorshahar.locker.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.yorshahar.locker.R;
import com.yorshahar.locker.activity.LockerMainActivity;
import com.yorshahar.locker.receiver.NotificationReceiver;

public class MyService extends Service implements NotificationReceiver.Delegate {

    private BroadcastReceiver mReceiver;
    private WindowManager windowManager;
    private LockerMainActivity activity;
    private final IBinder myBinder = new MyLocalBinder();

    private RelativeLayout containerView;
    private RelativeLayout lockerView;

    public MyService() {
    }

    public LockerMainActivity getActivity() {
        return activity;
    }

    public void setActivity(LockerMainActivity activity) {
        this.activity = activity;
    }

    public RelativeLayout getContainerView() {
        return containerView;
    }

    public void setContainerView(RelativeLayout lockerView) {
        containerView.removeAllViews();

        this.lockerView = lockerView;
        ViewGroup lockerParent = (ViewGroup) lockerView.getParent();
        if (lockerParent != null) {
            lockerParent.removeView(lockerView);
        }
        containerView.addView(lockerView);
        containerView.setVisibility(View.VISIBLE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        containerView = new RelativeLayout(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;

        windowManager.addView(containerView, params);
        containerView.setVisibility(View.INVISIBLE);

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        containerView.setSystemUiVisibility(uiOptions);

// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);

        mReceiver = new NotificationReceiver();
        ((NotificationReceiver) mReceiver).setDelegate(this);
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
        if (containerView != null) windowManager.removeView(containerView);
    }


    @Override
    public void lock() {
        if (activity != null) {
            activity.reset();
        }

        containerView.setVisibility(View.VISIBLE);
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        containerView.setSystemUiVisibility(uiOptions);
        lockerView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void unlock() {
        containerView.setVisibility(View.INVISIBLE);
    }

    public class MyLocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

}
