package com.yorshahar.locker.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.yorshahar.locker.service.ConnectivityService;
import com.yorshahar.locker.util.NumberUtil;

import java.util.Set;

/**
 * Connectivity receiver.
 * <p/>
 * Created by yorshahar on 9/7/15.
 */
public class ConnectivityReceiver extends ResultReceiver {

    public interface Receiver {

        void onAirplaneTurnedOn();

        void onAirplaneTurnedOff();

        void onWifiTurnedOn();

        void onWifiTurnedOff();

        void onBluetoothTurnedOn();

        void onBluetoothTurnedOff();

    }

    private Receiver mReceiver;

    public ConnectivityReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            switch (resultCode) {
                case ConnectivityService.STATUS_FINISHED: {
                    final int status = resultData.getInt("status", -1);
                    Set<Integer> statuses = NumberUtil.getSetBits(status);

                    if (statuses.contains(ConnectivityService.STATUS_AIRPLANE_ENABLED)) {
                        mReceiver.onAirplaneTurnedOn();
                    }

                    if (statuses.contains(ConnectivityService.STATUS_AIRPLANE_DISABLED)) {
                        mReceiver.onAirplaneTurnedOff();
                    }

                    if (statuses.contains(ConnectivityService.STATUS_WIFI_ENABLED)) {
                        mReceiver.onWifiTurnedOn();
                    }

                    if (statuses.contains(ConnectivityService.STATUS_WIFI_DISABLED)) {
                        mReceiver.onWifiTurnedOff();
                    }

                    if (statuses.contains(ConnectivityService.STATUS_BLUETOOTH_ENABLED)) {
                        mReceiver.onBluetoothTurnedOn();
                    }

                    if (statuses.contains(ConnectivityService.STATUS_BLUETOOTH_DISABLED)) {
                        mReceiver.onBluetoothTurnedOff();
                    }

                    break;
                }
                default: {
                    break;
                }
            }

        }
    }

}
