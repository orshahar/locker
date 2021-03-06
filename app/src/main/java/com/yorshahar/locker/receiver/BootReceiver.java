package com.yorshahar.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yorshahar.locker.service.LockService;
import com.yorshahar.locker.service.NotificationService;

public class BootReceiver extends BroadcastReceiver {

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_USER_PRESENT:
            case Intent.ACTION_USER_INITIALIZE: {
                context.startService(new Intent(context, NotificationService.class));
                context.startService(new Intent(context, LockService.class));
                break;
            }
            case Intent.ACTION_BOOT_COMPLETED: {
                context.startService(new Intent(context, NotificationService.class));
                context.startService(new Intent(context, LockService.class));
                break;
            }
            default: {

            }
        }

    }

}
