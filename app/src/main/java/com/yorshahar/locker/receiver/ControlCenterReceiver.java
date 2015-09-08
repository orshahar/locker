package com.yorshahar.locker.receiver;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.yorshahar.locker.service.ControlCenterService;

/**
 * Created by yorshahar on 9/7/15.
 */
public class ControlCenterReceiver extends ResultReceiver {

    public interface Receiver {

//        void onFlashlightTurnedOn(Camera camera);

//        void onFlashlightTurnedOff();

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
//            mReceiver.onReceiveResult(resultCode, resultData);

            switch (resultCode) {
                case ControlCenterService.STATUS_FINISHED: {
                    final boolean turnedOn = resultData.getBoolean("turnOn");
                    if (turnedOn) {
//                        mReceiver.onFlashlightTurnedOn(camera);
                    } else {
//                        mReceiver.onFlashlightTurnedOff();
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
