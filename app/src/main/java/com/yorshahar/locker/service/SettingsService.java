package com.yorshahar.locker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.yorshahar.locker.activity.SettingsActivity;

public class SettingsService extends Service implements LockerSettingsService {

    private final IBinder binder = new MyLocalBinder();
    private SettingsActivity activity;

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
        return super.onStartCommand(intent, flags, startId);
    }


//////////////////////////////////////////////////////////////
// LockerSettingsService
//////////////////////////////////////////////////////////////

    @Override
    public void enableLocker() {
        startService(new Intent(this, LockService.class));
    }

    @Override
    public void disableLocker() {
        stopService(new Intent(this, LockService.class));
    }

    @Override
    public void enableNotifications() {
        startService(new Intent(this, NotificationService.class));
    }

    @Override
    public void disableNotifications() {
        stopService(new Intent(this, NotificationService.class));
    }


    public class MyLocalBinder extends Binder {
        public SettingsService getService() {
            return SettingsService.this;
        }
    }
}
