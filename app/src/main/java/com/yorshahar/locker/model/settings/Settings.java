package com.yorshahar.locker.model.settings;

import android.os.Parcel;
import android.os.Parcelable;

import com.yorshahar.locker.model.DomainObject;

/**
 * Created by yorshahar on 9/16/15.
 */
public class Settings extends DomainObject implements Parcelable {
    private boolean lockerEnabled;
    private boolean notificationsEnabled;
    private boolean securityEnabled;
    private boolean hideStatusBar;
    private String passcode;
    private int wallpaper;

    public Settings() {

    }

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

    public int getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(int wallpaper) {
        this.wallpaper = wallpaper;
    }


///////////////////////////////////////////////////////////////
//
// Parcelable
//
///////////////////////////////////////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(lockerEnabled ? 1 : 0);
        dest.writeInt(notificationsEnabled ? 1 : 0);
        dest.writeInt(notificationsEnabled ? 1 : 0);
        dest.writeInt(securityEnabled ? 1 : 0);
        dest.writeInt(hideStatusBar ? 1 : 0);
//        dest.writeString("passcode");
        dest.writeInt(wallpaper);
    }

    public static final Parcelable.Creator<Settings> CREATOR = new Parcelable.Creator<Settings>() {
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

    private Settings(Parcel in) {
        lockerEnabled = in.readInt() == 1;
        notificationsEnabled = in.readInt() == 1;
        securityEnabled = in.readInt() == 1;
        hideStatusBar = in.readInt() == 1;
//        passcode = in.readString();
        wallpaper = in.readInt();
    }

}
