package com.yorshahar.locker.fragment;

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

    public interface Delegate {

        void onToggleButtonStateChanged(ToggleButtonType toggleButtonType, ToggleButtonView.State state);

        void onAppLauncherStateChanged(AppLauncher appLauncherType, AppLauncherView.State state);

        void onScreenBrightnessChanged(int brightness);

        void getToggleButtonsStatus();

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
        brightnessSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    changeBrightness(progress + 20);
                }
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
        updateBrightnessView();
    }

    public void updateToggleButton(ToggleButtonType type, ToggleButtonView.State state) {
        switch (type) {
            case AIRPLANE: {
                airplaneToggleButton.updateState(state);
                break;
            }
            case WIFI: {
                wifiToggleButton.updateState(state);
                break;
            }
            case BLUETHOOTH: {
                bluethoothToggleButton.updateState(state);
                break;
            }
        }
    }

    public void updateAppLauncher(AppLauncher type, AppLauncherView.State state) {
        switch (type) {
            case FLASHLIGHT: {
                flashlightAppLauncher.updateState(state);
            }
        }
    }

    private void updateToggleButtons() {
        if (delegate != null) {
            delegate.getToggleButtonsStatus();
        }
    }

    @Override
    public void onToggleButtonStateChanged(ToggleButtonView button, ToggleButtonView.State state) {
        switch (button.getId()) {
            case R.id.airplaneToggleButton: {
                if (delegate != null) {
                    delegate.onToggleButtonStateChanged(ToggleButtonType.AIRPLANE, state);
                }
                break;
            }
            case R.id.wifiToggleButton: {
                if (delegate != null) {
                    delegate.onToggleButtonStateChanged(ToggleButtonType.WIFI, state);
                }
                break;
            }
            case R.id.bluetoothToggleButton: {
                if (delegate != null) {
                    delegate.onToggleButtonStateChanged(ToggleButtonType.BLUETHOOTH, state);
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

    public void changeBrightness(int brightness) {
//        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);

//        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
//        lp.screenBrightness = brightness / 256.0f;
//        getActivity().getWindow().setAttributes(lp);

        if (delegate != null) {
            delegate.onScreenBrightnessChanged(brightness);
        }
    }

    public void updateBrightnessView() {
        try {
            int brightness = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            brightnessSlider.setProgress(brightness);
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
