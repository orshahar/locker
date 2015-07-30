package com.yorshahar.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    public interface Delegate {

        void gotIt();

    }

    private Delegate delegate;

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int a = 2;
        if (delegate != null) {
            delegate.gotIt();
        }
    }

    public Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

}
