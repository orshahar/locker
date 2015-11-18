package com.yorshahar.locker.service;

import com.yorshahar.locker.model.settings.Settings;

/**
 * Created by yorshahar on 8/19/15.
 */
public interface LockerSettingsService {

    Settings getSettings();

    boolean saveSettings(Settings settings);

    void enableLocker();

    void disableLocker();

    void enableNotifications();

    void disableNotifications();

    void enableSecurity();

    void disableSecurity();

    void hideStatusBar();

    void showStatusBar();

}
