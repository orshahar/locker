package com.yorshahar.locker.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

public class NotificationService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub.
//Code when the event is caught
        int a=2;
    }

    @Override
    public void onInterrupt() {
        // TODO Auto-generated method stub.
        int a=3;

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.feedbackType = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }
}