package com.yorshahar.locker.receiver;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.yorshahar.locker.service.ControlCenterService;
import com.yorshahar.locker.util.NumberUtil;

import java.util.Set;

/**
 * Created by yorshahar on 9/7/15.
 */
public class ControlCenterReceiver extends ResultReceiver {

    public interface Receiver {

//        void onFlashlightTurnedOn(Camera camera);

//        void onFlashlightTurnedOff();

        void onAirplaneTurnedOn();

        void onAirplaneTurnedOff();

        void onWifiTurnedOn();

        void onWifiTurnedOff();

        void onBluetoothTurnedOn();

        void onBluetoothTurnedOff();

    }

    private Receiver mReceiver;
    private Camera camera;

    public ControlCenterReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            switch (resultCode) {
                case ControlCenterService.STATUS_FINISHED: {
                    final int status = resultData.getInt("status", -1);
                    Set<Integer> statuses = NumberUtil.getSetBits(status);

                    if (statuses.contains(ControlCenterService.STATUS_AIRPLANE_ENABLED)) {
                        mReceiver.onAirplaneTurnedOn();
                    }

                    if (statuses.contains(ControlCenterService.STATUS_AIRPLANE_DISABLED)) {
                        mReceiver.onAirplaneTurnedOff();
                    }

                    if (statuses.contains(ControlCenterService.STATUS_WIFI_ENABLED)) {
                        mReceiver.onWifiTurnedOn();
                    }

                    if (statuses.contains(ControlCenterService.STATUS_WIFI_DISABLED)) {
                        mReceiver.onWifiTurnedOff();
                    }

                    if (statuses.contains(ControlCenterService.STATUS_BLUETOOTH_ENABLED)) {
                        mReceiver.onBluetoothTurnedOn();
                    }

                    if (statuses.contains(ControlCenterService.STATUS_BLUETOOTH_DISABLED)) {
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
