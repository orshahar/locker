package com.yorshahar.locker.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.yorshahar.locker.receiver.ControlCenterReceiver;

/**
 * Created by yorshahar on 9/7/15.
 */
public class ControlCenterService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private Camera cam;
    private Camera.Parameters cameraParameters;
    private ResultReceiver receiver;

    private static final String TAG = "ControlCenterService";

    public ControlCenterService() {
        super(ControlCenterService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");

        receiver = intent.getParcelableExtra("receiver");
        boolean turnOn = intent.getBooleanExtra("turnOn", false);

        Bundle bundle = new Bundle();

        receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        try {
            if (turnOn) {
                turnFlashlightOn();
            } else {
                turnFlashlightOff();
            }
            bundle.putBoolean("turnOn", turnOn);
            receiver.send(STATUS_FINISHED, bundle);
        } catch (RuntimeException e) {
            bundle.putBoolean("turnOn", turnOn);
            receiver.send(STATUS_ERROR, bundle);
        }

        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }


    public void turnFlashlightOn() throws RuntimeException {
        try {
            if (cam == null) {
                cam = Camera.open();
                ((ControlCenterReceiver) receiver).setCamera(cam);
            }
            cameraParameters = cam.getParameters();

            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(cameraParameters);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void turnFlashlightOff() throws RuntimeException {
        try {
            cam = ((ControlCenterReceiver) receiver).getCamera();
            cam.release();
//            cameraParameters = cam.getParameters();
//
//            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
//                cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                cam.setParameters(cameraParameters);
//            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
