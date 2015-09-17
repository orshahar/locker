package com.yorshahar.locker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.yorshahar.locker.activity.SettingsActivity;
import com.yorshahar.locker.db.DatabaseAdapter;
import com.yorshahar.locker.model.settings.Settings;

public class SettingsService extends Service implements LockerSettingsService {

    private final IBinder binder = new MyLocalBinder();
    private SettingsActivity activity;

    private DatabaseAdapter databaseAdapter;

    public SettingsService() {
    }

    public SettingsActivity getActivity() {
        return activity;
    }

    public void setActivity(SettingsActivity activity) {
        this.activity = activity;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        activity = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter.createDb();

        return super.onStartCommand(intent, flags, startId);
    }


//////////////////////////////////////////////////////////////
// LockerSettingsService
//////////////////////////////////////////////////////////////

    @Override
    public void enableLocker() {
        try {
            startService(new Intent(this, LockService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disableLocker() {
        try {
            stopService(new Intent(this, LockService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void enableNotifications() {
        startService(new Intent(this, NotificationService.class));
    }

    @Override
    public void disableNotifications() {
//        NotificationService notificationService = NotificationService.getInstance();
//        if (notificationService != null) {
//            notificationService.stop();
//        }
        stopService(new Intent(this, NotificationService.class));
    }

    @Override
    public void hideStatusBar() {

    }

    @Override
    public void showStatusBar() {

    }

    @Override
    public void enableSecurity() {

    }

    @Override
    public void disableSecurity() {

    }

    @Override
    public Settings getSettings() {
        return databaseAdapter.getSettings();
    }

    @Override
    public boolean saveSetings(Settings settings) {
        long id = databaseAdapter.updateSettings(
                settings.isLockerEnabled(),
                settings.isNotificationsEnabled(),
                settings.isSecurityEnabled(),
                settings.isHideStatusBar(),
                settings.getPasscode());

        return id != -1;
    }

    public class MyLocalBinder extends Binder {
        public SettingsService getService() {
            return SettingsService.this;
        }
    }
}
