package com.yorshahar.locker.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.yorshahar.locker.model.settings.Settings;
import com.yorshahar.locker.service.SettingsService;
import com.yorshahar.locker.util.NumberUtil;

import java.util.Set;

/**
 * Settings receiver.
 * <p/>
 * Created by yorshahar on 9/7/15.
 */
public class SettingsReceiver extends ResultReceiver {

    public interface Receiver {

        void onResultWallpaper(int resourceId);

        void onResultSettings(Settings settings);

        void onSettingsSaved();

        void onLockerEnabled();

        void onLockerDisabled();

    }

    private Receiver mReceiver;

    public SettingsReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            switch (resultCode) {
                case SettingsService.STATUS_FINISHED: {
                    final int status = resultData.getInt("status", -1);
                    Set<Integer> statuses = NumberUtil.getSetBits(status);

                    if (statuses.contains(SettingsService.RESULT_WALLPAPER)) {
                        int resourceId = resultData.getInt("resourceId");
                        mReceiver.onResultWallpaper(resourceId);
                    }

                    if (statuses.contains(SettingsService.RESULT_SETTINGS)) {
                        Settings settings = resultData.getParcelable("settings");
                        mReceiver.onResultSettings(settings);
                    }

                    if (statuses.contains(SettingsService.STATUS_SETTINGS_SAVED)) {
                        mReceiver.onSettingsSaved();
                    }

                    if (statuses.contains(SettingsService.STATUS_LOCKER_ENABLED)) {
                        mReceiver.onLockerEnabled();
                    }

                    if (statuses.contains(SettingsService.STATUS_LOCKER_DISABLED)) {
                        mReceiver.onLockerDisabled();
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
