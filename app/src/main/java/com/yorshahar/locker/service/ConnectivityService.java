package com.yorshahar.locker.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;

/**
 * Service to handle the connectivity activities.
 * <p/>
 * Created by yorshahar on 9/7/15.
 */
public class ConnectivityService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public static final int ACTION_NONE = 0x0000000000;
    public static final int ACTION_GET_STATS = 0x0000000001;
    public static final int ACTION_ENABLE_AIRPLAINE = 0x0000000010;
    public static final int ACTION_DISABLE_AIRPLAINE = 0x0000000100;
    public static final int ACTION_ENABLE_WIFI = 0x0000001000;
    public static final int ACTION_DISABLE_WIFI = 0x0000010000;
    public static final int ACTION_ENABLE_BLUETOOTH = 0x0000100000;
    public static final int ACTION_DISABLE_BLUETOOTH = 0x0001000000;

    public static final int STATUS_AIRPLANE_ENABLED = 0x0000000001;
    public static final int STATUS_AIRPLANE_DISABLED = 0x0000000010;
    public static final int STATUS_WIFI_ENABLED = 0x0000000100;
    public static final int STATUS_WIFI_DISABLED = 0x0000001000;
    public static final int STATUS_BLUETOOTH_ENABLED = 0x0000010000;
    public static final int STATUS_BLUETOOTH_DISABLED = 0x0000100000;

    private static final String TAG = "ConnectivityService";

    public ConnectivityService() {
        super(ConnectivityService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");

        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        int action = intent.getIntExtra("action", 0);

        Bundle bundle = new Bundle();

        receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        try {
            switch (action) {
                case ACTION_GET_STATS: {
                    int statuses = getToggleButtonsStatus();
                    bundle.putInt("status", statuses);
                    receiver.send(STATUS_FINISHED, bundle);
                    break;
                }
                case ACTION_ENABLE_AIRPLAINE: {
                    enableAirplaneMode();
                    disableWifi();
                    disableBluetooth();
                    bundle.putInt("status", STATUS_AIRPLANE_ENABLED);
                    receiver.send(STATUS_FINISHED, bundle);
                    break;
                }
                case ACTION_DISABLE_AIRPLAINE: {
                    disableAirplaneMode();
                    enableWifi();
                    bundle.putInt("status", STATUS_AIRPLANE_DISABLED);
                    receiver.send(STATUS_FINISHED, bundle);
                    break;
                }
                case ACTION_ENABLE_WIFI: {
                    enableWifi();
                    break;
                }
                case ACTION_DISABLE_WIFI: {
                    disableWifi();
                    break;
                }
                case ACTION_ENABLE_BLUETOOTH: {
                    enableBluetooth();
                    break;
                }
                case ACTION_DISABLE_BLUETOOTH: {
                    disableBluetooth();
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (Exception e) {
            bundle.putInt("status", -1);
            receiver.send(STATUS_ERROR, bundle);
        }

        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    private int getToggleButtonsStatus() {
        int statuses = 0;

        statuses = statuses | (isAirplaneEnabled() ? STATUS_AIRPLANE_ENABLED : STATUS_AIRPLANE_DISABLED);
        statuses = statuses | (isWifiEnabled() ? STATUS_WIFI_ENABLED : STATUS_WIFI_DISABLED);
        statuses = statuses | (isBluetoothEnabled() ? STATUS_BLUETOOTH_ENABLED : STATUS_BLUETOOTH_DISABLED);

        return statuses;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isAirplaneEnabled() {
        return Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
    }

    private boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    private boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void enableAirplaneMode() {
        Settings.System.putInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 1);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void disableAirplaneMode() {
        Settings.System.putInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
    }

    private void enableWifi() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    private void disableWifi() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();
    }

    private void disableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.disable();
    }

}
