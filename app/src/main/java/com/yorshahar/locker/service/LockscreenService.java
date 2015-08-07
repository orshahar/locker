package com.yorshahar.locker.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.yorshahar.locker.R;
import com.yorshahar.locker.activity.LockerMainActivity;
import com.yorshahar.locker.receiver.LockscreenIntentReceiver;
import com.yorshahar.locker.util.HomeWatcher;
import com.yorshahar.locker.util.OnHomePressedListener;

public class LockscreenService extends Service {

    private BroadcastReceiver mReceiver;
    private Context mContext;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // Register for Lockscreen event intents
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction("com.hmkcode.android.USER_ACTION");

        mContext = this;
        mReceiver = new LockscreenIntentReceiver();
        registerReceiver(mReceiver, filter);
        startForeground();

        final HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {

            @Override
            public void onHomePressed() {
                if (((LockscreenIntentReceiver) mReceiver).isLockscreenOn()) {

//                    ((LockscreenIntentReceiver) mReceiver).startLockscreen(getApplicationContext());

//                    Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    Intent intent = new Intent(mContext, LockerMainActivity.class);
//                    intent.putExtra(DEFAULT_EXTRA,"");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);


//                    PackageManager pm = getPackageManager();
//                    Intent intent = pm.getLaunchIntentForPackage("net.suckga.ilauncher2");
//                    startActivity(intent);
//
//                    startLauncherActivity(getApplicationContext());
                }


//                if (((LockscreenIntentReceiver) mReceiver).isLockscreenOn()) {
//                    ((LockscreenIntentReceiver) mReceiver).startLockscreen(getApplicationContext());
//                }


//                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//                mainIntent.addCategory(Intent.CATEGORY_HOME);
//
//                final PackageManager packageManager = getPackageManager();
//                List<ResolveInfo> appList = packageManager.queryIntentActivities(mainIntent, 0);
//                Collections.sort(appList, new ResolveInfo.DisplayNameComparator(packageManager));
//
//                ResolveInfo defaultLauncher = null;
//                for (ResolveInfo resolveInfo : appList) {
//                    if (resolveInfo.activityInfo.packageName.equals("net.suckga.ilauncher2")) {
//                        break;
//                    }
//                }


//
//                Intent it = new Intent("intent.my.action");
//                it.setComponent(new ComponentName(getApplicationContext().getPackageName(), MyMainActivity.class.getName()));
//                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getApplicationContext().getApplicationContext().startActivity(it);
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();

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

    // Unregister receiver
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

//    /**
//     * Backwards compatible method that will clear all activities in the stack.
//     */
//    public void startLauncherActivity(Context context) {
//        PackageManager packageManager = context.getPackageManager();
//        Intent intent = packageManager.getLaunchIntentForPackage("net.suckga.ilauncher2");
////        ComponentName componentName = intent.getComponent();
////        Intent launcherIntent = IntentCompat.makeRestartActivityTask(componentName);
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        context.startActivity(intent);
//    }

}
