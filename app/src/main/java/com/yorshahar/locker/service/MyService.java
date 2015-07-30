package com.yorshahar.locker.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.yorshahar.locker.receiver.LockscreenIntentReceiver;
import com.yorshahar.locker.receiver.NotificationReceiver;

public class MyService extends Service {

    private BroadcastReceiver mReceiver;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);

        mReceiver = new NotificationReceiver();
        registerReceiver(mReceiver, filter);
        return START_STICKY;
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
    }
}
