package com.yorshahar.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yorshahar.locker.activity.LockerMainActivity;

public class NotificationReceiver extends BroadcastReceiver {

    public interface Delegate {

        void lock();

        void unlock();

    }

    private Delegate delegate;

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_OFF:
            case Intent.ACTION_SCREEN_ON:
            case Intent.ACTION_BOOT_COMPLETED: {
                if (delegate != null) {
                    delegate.lock();
                }
                break;
            }
            case "com.hmkcode.android.USER_ACTION": {
                if (delegate != null) {
                    delegate.unlock();
                }
                break;
            }
            default: {

            }
        }

    }

    public Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

}
