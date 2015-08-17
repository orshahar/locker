package com.yorshahar.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yorshahar.locker.service.LockService;

public class BootReceiver extends BroadcastReceiver {

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_USER_PRESENT: {
                context.startService(new Intent(context, LockService.class));
                break;
            }
            case Intent.ACTION_BOOT_COMPLETED: {
                context.startService(new Intent(context, LockService.class));
                break;
            }
            default: {

            }
        }

    }

}
