package com.yorshahar.locker.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import com.yorshahar.locker.activity.SettingsActivity;
import com.yorshahar.locker.db.DatabaseAdapter;
import com.yorshahar.locker.model.settings.Settings;

public class SettingsService extends IntentService implements LockerSettingsService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public static final int ACTION_NONE =               0x0000000000;
    public static final int ACTION_GET_WALLPAPER =      0x0000000001;
    public static final int ACTION_GET_SETTINGS =       0x0000000010;
    public static final int ACTION_SAVE_SETTINGS =      0x0000000100;
    public static final int ACTION_ENABLE_LOCKER =      0x0000001000;
    public static final int ACTION_DISABLE_LOCKER =     0x0000010000;

    public static final int RESULT_WALLPAPER =          0x0000000001;
    public static final int RESULT_SETTINGS =           0x0000000010;
    public static final int STATUS_SETTINGS_SAVED =     0x0000000100;
    public static final int STATUS_LOCKER_ENABLED =     0x0000001000;
    public static final int STATUS_LOCKER_DISABLED =    0x0000010000;

    private static final String TAG = "SettingsService";

//    private final IBinder binder = new MyLocalBinder();
//    private SettingsActivity activity;

    private DatabaseAdapter databaseAdapter;

    public SettingsService() {
        super(SettingsService.class.getName());
    }

//    public SettingsActivity getActivity() {
//        return activity;
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");

        ResultReceiver receiver = intent.getExtras().getParcelable("receiver");
        int action = intent.getIntExtra("action", 0);

        Bundle bundle = new Bundle();
        bundle.putInt("action", action);

        assert receiver != null;
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        try {
            switch (action) {
                case ACTION_GET_WALLPAPER: {
                    Settings settings = getSettings();
                    int resourceId = settings.getWallpaper();
                    bundle.putInt("status", RESULT_WALLPAPER);
                    bundle.putInt("resourceId", resourceId);
                    receiver.send(STATUS_FINISHED, bundle);
                    break;
                }
                case ACTION_GET_SETTINGS: {
                    Settings settings = getSettings();
                    bundle.putInt("status", RESULT_SETTINGS);
                    bundle.putParcelable("settings", settings);
                    receiver.send(STATUS_FINISHED, bundle);
                    break;
                }
                case ACTION_SAVE_SETTINGS: {
                    Settings settings = intent.getParcelableExtra("settings");
                    boolean success = saveSettings(settings);
                    if (success) {
                        bundle.putInt("status", STATUS_SETTINGS_SAVED);
                        receiver.send(STATUS_FINISHED, bundle);
                    } else {
                        receiver.send(STATUS_ERROR, bundle);
                    }
                    break;
                }
                case ACTION_ENABLE_LOCKER: {
                    enableLocker();
                    bundle.putInt("status", STATUS_LOCKER_ENABLED);
                    receiver.send(STATUS_FINISHED, bundle);
                    break;
                }
                case ACTION_DISABLE_LOCKER: {
                    disableLocker();
                    bundle.putInt("status", STATUS_LOCKER_DISABLED);
                    receiver.send(STATUS_FINISHED, bundle);
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (Exception e) {
            bundle.putInt("status", -1);
            receiver.send(STATUS_ERROR, bundle);
        }

        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

//    public void setActivity(SettingsActivity activity) {
//        this.activity = activity;
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return binder;
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        activity = null;
//        return super.onUnbind(intent);
//    }

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
    public boolean saveSettings(Settings settings) {
        long id = -1;
        try {
            id = databaseAdapter.updateSettings(
                    settings.isLockerEnabled(),
                    settings.isNotificationsEnabled(),
                    settings.isSecurityEnabled(),
                    settings.isHideStatusBar(),
                    settings.getPasscode(),
                    settings.getWallpaper());
        } catch (Exception e) {
            int a = 2;
        }

        return id != -1;
    }

    public void changeWallpaper(int resourceId) {

    }

//    public class MyLocalBinder extends Binder {
//        public SettingsService getService() {
//            return SettingsService.this;
//        }
//    }
}
