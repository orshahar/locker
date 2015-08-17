package com.yorshahar.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LockReceiver extends BroadcastReceiver {

    public interface Delegate {

        void lock(boolean reset);

        void unlock();

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
