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
import android.os.IInterface;
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

public class MyService extends Service implements NotificationReceiver.Delegate, IInterface {

    private BroadcastReceiver mReceiver;
    private WindowManager windowManager;

    private RelativeLayout containerView;
    private RelativeLayout lockerView;

    public MyService() {
    }

    public RelativeLayout getContainerView() {
        return containerView;
    }

    public void setContainerView(RelativeLayout lockerView) {
        this.lockerView = lockerView;

        containerView.removeAllViews();

        ViewGroup lockerParent = (ViewGroup) lockerView.getParent();
        if (lockerParent != null) {
            lockerParent.removeView(lockerView);
        }
        containerView.addView(lockerView);
        containerView.setVisibility(View.VISIBLE);
    }

    @Override
    public IBinder onBind(Intent intent) {
//        return null;

        return asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        containerView = (RelativeLayout) inflater.inflate(R.layout.main_activity, null);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;

        windowManager.addView(containerView, params);
        containerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction("com.hmkcode.android.USER_ACTION");

        mReceiver = new NotificationReceiver();
        ((NotificationReceiver) mReceiver).setDelegate(this);
        registerReceiver(mReceiver, filter);
        startForeground();

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
//        containerView.setVisibility(View.VISIBLE);

        Intent containerIntent = new Intent(this, LockerMainActivity.class);
        containerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(containerIntent);
    }

    @Override
    public void unlock() {
        containerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public IBinder asBinder() {
        Binder binder = new Binder();
        binder.attachInterface(this, "MyService");

        return binder;
    }
}
