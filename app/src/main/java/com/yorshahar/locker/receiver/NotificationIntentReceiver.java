package com.yorshahar.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by yorshahar on 7/30/15.
 */
public class NotificationIntentReceiver extends BroadcastReceiver {

    public interface Delegate {

        void gotIt();

    }

    private Delegate delegate;

    // Handle actions and display Lockscreen
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                || intent.getAction().equals(Intent.ACTION_SCREEN_ON)
                || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            int a = 2;
        }

        delegate.gotIt();
    }

    public Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

}
