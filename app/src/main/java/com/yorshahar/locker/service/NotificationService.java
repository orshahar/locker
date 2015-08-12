package com.yorshahar.locker.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;

public class NotificationService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int a=2;
    }

    @Override
    public void onInterrupt() {
        int a=2;
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }
}
