package com.yorshahar.locker.fragment;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.yorshahar.locker.R;
import com.yorshahar.locker.activity.LockerMainActivity;
import com.yorshahar.locker.ui.widget.AppLauncherView;
import com.yorshahar.locker.ui.widget.ToggleButtonView;

/**
 * Fragment for the control center.
 * <p/>
 * Created by yorshahar on 8/31/15.
 */
public class ControlCenterFragment extends Fragment implements ToggleButtonView.Delegate, AppLauncherView.Delegate {

    public void updateAppLauncher(AppLauncher type, AppLauncherView.State state) {
        switch (type) {
            case FLASHLIGHT: {
                flashlightAppLauncher.updateState(state);
            }
        }
    }

    public interface Delegate {

        void onToggleButtonStateChanged(ToggleButtonType toggleButtonType, ToggleButtonView.State state);

        void onAppLauncherStateChanged(AppLauncher appLauncherType, AppLauncherView.State state);

        void onScreenBrightnessChanged(int brightness);

    }

    public enum ToggleButtonType {
        AIRPLANE,
        WIFI,
        BLUETHOOTH,
        NIGHT,
        ROTATION_LOCK
    }

    public enum AppLauncher {
        FLASHLIGHT,
        TIME,
        CALCULATOR,
        CAMERA
    }

    private Delegate delegate;
    private WifiManager wifiManager;
    private BluetoothAdapter bluetoothAdapter;

    private ToggleButtonView airplaneToggleButton;
    private ToggleButtonView wifiToggleButton;
    private ToggleButtonView bluethoothToggleButton;
    private ToggleButtonView nightToggleButton;
    private ToggleButtonView rotationLockToggleButton;

    private AppLauncherView flashlightAppLauncher;

    private SeekBar brightnessSlider;

    public Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((LockerMainActivity) getActivity()).setControlCenterFragment(this);

        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // TODO: release the wifiManger and bluetoothAdapter ?

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_control_center, container, false);

        airplaneToggleButton = (ToggleButtonView) view.findViewById(R.id.airplaneToggleButton);
        airplaneToggleButton.setDelegate(this);

        wifiToggleButton = (ToggleButtonView) view.findViewById(R.id.wifiToggleButton);
        wifiToggleButton.setDelegate(this);

        bluethoothToggleButton = (ToggleButtonView) view.findViewById(R.id.bluetoothToggleButton);
        bluethoothToggleButton.setDelegate(this);

        nightToggleButton = (ToggleButtonView) view.findViewById(R.id.nightToggleButton);
        nightToggleButton.setDelegate(this);

        rotationLockToggleButton = (ToggleButtonView) view.findViewById(R.id.rotationLockToggleButton);
        rotationLockToggleButton.setDelegate(this);

        flashlightAppLauncher = (AppLauncherView) view.findViewById(R.id.flashlightAppLauncherView);
        flashlightAppLauncher.setDelegate(this);

        AppLauncherView timeAppLauncher = (AppLauncherView) view.findViewById(R.id.timeAppLauncherView);
        timeAppLauncher.setDelegate(this);

        AppLauncherView calculatorAppLauncher = (AppLauncherView) view.findViewById(R.id.calculatorAppLauncherView);
        calculatorAppLauncher.setDelegate(this);

        AppLauncherView cameraAppLauncher = (AppLauncherView) view.findViewById(R.id.cameraAppLauncherView);
        cameraAppLauncher.setDelegate(this);

        brightnessSlider = (SeekBar) view.findViewById(R.id.brightnessSlider);
        try {
            int brightness = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            brightnessSlider.setProgress(brightness - 20);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        brightnessSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeBrightness(progress + 20);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateToggleButtons();
    }

    private void updateToggleButtons() {
        if (Settings.System.getInt(getActivity().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1) {
            airplaneToggleButton.turnOn();
        } else {
            airplaneToggleButton.turnOff();
        }

        if (wifiManager.isWifiEnabled()) {
            wifiToggleButton.turnOn();
        } else {
            wifiToggleButton.turnOff();
        }

        if (bluetoothAdapter.isEnabled()) {
            bluethoothToggleButton.turnOn();
        } else {
            bluethoothToggleButton.turnOff();
        }

    }

    @Override
    public void onToggleButtonStateChanges(ToggleButtonView button, ToggleButtonView.State state) {
        switch (button.getId()) {
            case R.id.airplaneToggleButton: {
                if (delegate != null) {
                    delegate.onToggleButtonStateChanged(ToggleButtonType.AIRPLANE, state);
                }
                if (state == ToggleButtonView.State.ON) {
                    enableAirplaneMode();
                } else {
                    disableAirplaneMode();
                }
                break;
            }
            case R.id.wifiToggleButton: {
                if (delegate != null) {
                    delegate.onToggleButtonStateChanged(ToggleButtonType.WIFI, state);
                }
                if (state == ToggleButtonView.State.ON) {
                    enableWifi();
                } else {
                    disableWifi();
                }
                break;
            }
            case R.id.bluetoothToggleButton: {
                if (delegate != null) {
                    delegate.onToggleButtonStateChanged(ToggleButtonType.BLUETHOOTH, state);
                }
                if (state == ToggleButtonView.State.ON) {
                    enableBluetooth();
                } else {
                    disableBluetooth();
                }
                break;
            }
            case R.id.nightToggleButton: {
                if (delegate != null) {
                    delegate.onToggleButtonStateChanged(ToggleButtonType.AIRPLANE, state);
                }
                break;
            }
            case R.id.rotationLockToggleButton: {
                if (delegate != null) {
                    delegate.onToggleButtonStateChanged(ToggleButtonType.ROTATION_LOCK, state);
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void enableAirplaneMode() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 1);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void disableAirplaneMode() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
    }

    private void enableWifi() {
        wifiManager.setWifiEnabled(true);
    }

    private void disableWifi() {
        wifiManager.setWifiEnabled(false);
    }

    private void enableBluetooth() {
        bluetoothAdapter.enable();
    }

    private void disableBluetooth() {
        bluetoothAdapter.disable();
    }

    public void toggleAirplainModeOn() {
        airplaneToggleButton.turnOn();
    }

    public void toggleAirplainModeOff() {
        airplaneToggleButton.turnOff();
    }

    public void toggleWifiOn() {
        wifiToggleButton.turnOn();
    }

    public void toggleWifiOff() {
        wifiToggleButton.turnOff();
    }

    public void toggleBluetoothOn() {
        bluethoothToggleButton.turnOn();
    }

    public void toggleBluetoothOff() {
        bluethoothToggleButton.turnOff();
    }

    public void changeBrightness(int brightness) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);

//        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
//        lp.screenBrightness = brightness / 256.0f;
//        getActivity().getWindow().setAttributes(lp);

        if (delegate != null) {
            delegate.onScreenBrightnessChanged(brightness);
        }

//        ((LockerMainActivity) getActivity()).onScreenBrightnessChanged(brightness);
    }

    public void updateBrightnessView() {
        try {
            int brightness = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            brightnessSlider.setProgress(brightness - 20);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAppLauncherStateChanges(AppLauncherView launcher, AppLauncherView.State state) {
        switch (launcher.getId()) {
            case R.id.flashlightAppLauncherView: {
                if (delegate != null) {
                    delegate.onAppLauncherStateChanged(AppLauncher.FLASHLIGHT, state);
                }
                break;
            }
            default: {
                break;
            }
        }
    }

}
