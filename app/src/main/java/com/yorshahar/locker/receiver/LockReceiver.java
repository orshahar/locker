package com.yorshahar.locker.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;

public class LockReceiver extends BroadcastReceiver {

    public interface Delegate {

        void lock(boolean reset);

        void unlock();

        void onBatteryChanged(int level, int status);

        void onAirplaneModeEnabled();

        void onAirplaneModeDisabled();

        void onWifiEnabled();

        void onWifiDisabled();

        void onBluetoothEnabled();

        void onBluetoothDisabled();
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
            case Intent.ACTION_AIRPLANE_MODE_CHANGED: {
                if (intent.getBooleanExtra("state", false)) {
                    if (delegate != null) {
                        delegate.onAirplaneModeEnabled();
                    }
                } else {
                    if (delegate != null) {
                        delegate.onAirplaneModeDisabled();
                    }
                }
                break;
            }
            case WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION: {
                if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                    if (delegate != null) {
                        delegate.onWifiEnabled();
                    }
                } else {
                    if (delegate != null) {
                        delegate.onWifiDisabled();
                    }
                }
                break;
            }
            case BluetoothAdapter.ACTION_STATE_CHANGED: {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF: {
                        if (delegate != null) {
                            delegate.onBluetoothDisabled();
                        }
                        break;
                    }
                    case BluetoothAdapter.STATE_TURNING_OFF: {
                        break;
                    }
                    case BluetoothAdapter.STATE_ON: {
                        if (delegate != null) {
                            delegate.onBluetoothEnabled();
                        }
                        break;
                    }
                    case BluetoothAdapter.STATE_TURNING_ON: {
                        break;
                    }
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
