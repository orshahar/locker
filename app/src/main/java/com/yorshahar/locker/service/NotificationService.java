package com.yorshahar.locker.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.accessibility.AccessibilityEvent;

import com.yorshahar.locker.db.DatabaseAdapter;
import com.yorshahar.locker.model.notification.Notification;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationService extends AccessibilityService {

    public interface Delegate {

        void onNotification(Notification notification);

        void onNotificationsChanged(List<Notification> notifications);
    }

    private DatabaseAdapter databaseAdapter;
    private static NotificationService instance;
    private Delegate delegate;
    private Set<String> allowedApps = new HashSet<>();
    private PackageManager pm;

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        allowedApps.add("com.google.android.calendar");
        allowedApps.add("com.google.android.keep");
        allowedApps.add("com.ticktick.task");
        allowedApps.add("com.google.android.apps.inbox");
        allowedApps.add("com.google.android.talk");
        allowedApps.add("com.android.mms");
        allowedApps.add("com.android.vending");
        allowedApps.add("com.android.email");
        allowedApps.add("com.google.android.googlequicksearchbox");

        databaseAdapter = new DatabaseAdapter(getApplicationContext());

        instance = this;
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String packageName = event.getPackageName().toString();
        if (isAppAllowed(packageName)) {
            switch (event.getEventType()) {
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: {
                    if (event.getText().size() > 0) {
                        android.app.Notification androidNotification = (android.app.Notification) event.getParcelableData();

                        if (androidNotification != null) {
                            Drawable icon = getApplicationIcon(packageName);
                            String applicationName = getApplicationName(packageName);
                            String text = getNotificationText(event, androidNotification);
                            Date dateArrived = new Date();
//                        GregorianCalendar cal = new GregorianCalendar();
//                        cal.set(Calendar.HOUR, 0);
//                        cal.set(Calendar.MINUTE, 0);
//                        cal.setTimeInMillis(cal.getTimeInMillis() + event.getEventTime());
//                        cal.add(Calendar.HOUR, -4);
//                        Date dateArrived = cal.getTime();

                            Notification notification = new Notification(
                                    applicationName,
                                    dateArrived,
                                    "",
                                    text,
                                    icon
                            );

                            addNotification(notification);
                        }
                    }

                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    private String getNotificationText(AccessibilityEvent event, android.app.Notification androidNotification) {
        StringBuilder textBuilder = new StringBuilder();
        String separator = "";
        for (CharSequence text : event.getText()) {
            textBuilder.append(separator).append(text);
            separator = "\n";
        }

        if (event.getContentDescription() != null && event.getContentDescription().length() > 0) {
            textBuilder.append(separator).append("-------").append(separator).append(event.getContentDescription());
        }

//        android.app.Notification androidNotification = (android.app.Notification) event.getParcelableData();
//        if (androidNotification != null && androidNotification.tickerText != null && androidNotification.tickerText.length() > 0) {
//            textBuilder.append(separator).append("--- tickerText ---").append(separator).append(androidNotification.tickerText);
//        }

//        if (androidNotification != null && androidNotification.deleteIntent != null) {
//            textBuilder.append(separator).append("--- deleteIntent ---").append(separator).append(androidNotification.deleteIntent.toString());
//        }
//
//        if (androidNotification != null && androidNotification.contentView != null) {
//            textBuilder.append(separator).append("--- deleteIntent ---").append(separator).append(androidNotification.deleteIntent.toString());
//        }

        return textBuilder.toString();
    }

    private String getApplicationName(String packageName) {
        String appName = "";
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            appName = pm.getApplicationLabel(appInfo).toString();
        } catch (final PackageManager.NameNotFoundException ignored) {

        }

        return appName;
    }

    private Drawable getApplicationIcon(String packageName) {
        Drawable icon = null;
        try {
            icon = pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return icon;
    }

    private boolean isAppAllowed(String packageName) {
        return allowedApps.contains(packageName);
    }

    public List<Notification> getNotifications() {
        return databaseAdapter.getNotifications();
    }

    public void addNotification(Notification notification) {
        long id = databaseAdapter.saveNotification(notification);

        if (id != -1 && delegate != null) {
            List<Notification> notifications = databaseAdapter.getNotifications();
            delegate.onNotificationsChanged(notifications);
        }
    }

    public void deleteNotification(Notification notification) {
        boolean success = databaseAdapter.deleteNotification(notification);

//        if (success && delegate != null) {
//        List<Notification> notifications = databaseAdapter.getNotifications();
//            delegate.onNotificationsChanged(notifications);
//        }
    }

    @Override
    public void onInterrupt() {
        int a = 2;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        instance = null;

        return super.onUnbind(intent);
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
//        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        pm = getPackageManager();
    }

    public static NotificationService getInstance() {
        return instance;
    }

    @Override
    public boolean stopService(Intent name) {

        return super.stopService(name);
    }

    public void stop() {
        delegate = null;
        stopSelf();
    }


}
