package com.yorshahar.locker.model.settings;

import com.yorshahar.locker.model.DomainObject;

/**
 * Created by yorshahar on 9/16/15.
 */
public class Settings extends DomainObject {
    private boolean lockerEnabled;
    private boolean notificationsEnabled;
    private boolean securityEnabled;
    private boolean hideStatusBar;
    private String passcode;

    public boolean isLockerEnabled() {
        return lockerEnabled;
    }

    public void setLockerEnabled(boolean lockerEnabled) {
        this.lockerEnabled = lockerEnabled;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    public void setSecurityEnabled(boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    public boolean isHideStatusBar() {
        return hideStatusBar;
    }

    public void setHideStatusBar(boolean hideStatusBar) {
        this.hideStatusBar = hideStatusBar;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

}
