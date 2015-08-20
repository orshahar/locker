package com.yorshahar.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeReceiver extends BroadcastReceiver {

    public interface Delegate {

        void onTimeTicked();

        void onDateChanged();

    }

    private Delegate delegate;

    public TimeReceiver() {
    }

    public Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_TIME_TICK: {
                if (delegate != null) {
                    delegate.onTimeTicked();
                }
                break;
            }
            case Intent.ACTION_DATE_CHANGED: {
                if (delegate != null) {
                    delegate.onDateChanged();
                }
                break;
            }
            default: {

            }
        }
    }

}
