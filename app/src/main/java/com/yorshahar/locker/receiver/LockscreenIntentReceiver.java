package com.yorshahar.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yorshahar.locker.activity.LockerMainActivity;

public class LockscreenIntentReceiver extends BroadcastReceiver {

    private boolean lockscreenOn = false;

    // Handle actions and display Lockscreen
    @Override
    public void onReceive(Context context, Intent intent) {

        switch(intent.getAction()) {
            case Intent.ACTION_SCREEN_OFF:
            case Intent.ACTION_SCREEN_ON:
            case Intent.ACTION_BOOT_COMPLETED: {
                startLockscreen(context);
                break;
            }
            case "com.hmkcode.android.USER_ACTION": {
                lockscreenOn = false;
                break;
            }
            default: {

            }
        }

    }

    // Display lock screen
    public void startLockscreen(Context context) {
        Intent mIntent = new Intent(context, LockerMainActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(mIntent);

        lockscreenOn = true;
    }

    public boolean isLockscreenOn() {
        return lockscreenOn;
    }

    public void setLockscreenOn(boolean lockscreenOn) {
        this.lockscreenOn = lockscreenOn;
    }

}
