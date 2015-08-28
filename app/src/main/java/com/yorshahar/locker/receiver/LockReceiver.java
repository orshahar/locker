package com.yorshahar.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class LockReceiver extends BroadcastReceiver {

    public interface Delegate {

        void lock(boolean reset);

        void unlock();

        void onBatteryChanged(int level, int status);

    }

    private Delegate delegate;

    public LockReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_OFF: {
                if (delegate != null) {
                    delegate.lock(true);
                }
                break;
            }
            case Intent.ACTION_SCREEN_ON: {
                if (delegate != null) {
                    delegate.lock(false);
                }
                break;
            }
            case Intent.ACTION_BATTERY_CHANGED: {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 1);
                if (delegate != null) {
                    delegate.onBatteryChanged(level, status);
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
