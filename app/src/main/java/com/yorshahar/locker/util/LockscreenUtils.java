package com.yorshahar.locker.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.yorshahar.locker.R;

public class LockscreenUtils {

    // Member variables
    private OverlayDialog mOverlayDialog;
    private OnLockStatusChangedListener mLockStatusChangedListener;

    // Interface to communicate with owner activity
    public interface OnLockStatusChangedListener {
        void onLockStatusChanged(boolean isLocked);
    }

    // Reset the variables
    public LockscreenUtils() {
        reset();
    }

    // Display overlay dialog with a view to prevent home button click
    public void lock(Activity activity) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//
//        AlertDialog alert = builder.create();
//        alert.show();
//
//        //use this line to disable home key
//        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);


        if (mOverlayDialog == null) {
            mOverlayDialog = new OverlayDialog(activity);
            mOverlayDialog.show();
            mLockStatusChangedListener = (OnLockStatusChangedListener) activity;
        }
    }

    // Reset variables
    public void reset() {
        if (mOverlayDialog != null) {
            mOverlayDialog.dismiss();
            mOverlayDialog = null;
        }
    }

    // Unlock the home button and give callback to unlock the screen
    public void unlock() {
        if (mOverlayDialog != null) {
            mOverlayDialog.dismiss();
            mOverlayDialog = null;
            if (mLockStatusChangedListener != null) {
                mLockStatusChangedListener.onLockStatusChanged(false);
            }
        }
    }

    // Create overlay dialog for lockedscreen to disable hardware buttons
    private static class OverlayDialog extends AlertDialog {

        public OverlayDialog(Activity activity) {
            super(activity, R.style.AlertDialog_AppCompat_Light);
            LayoutParams params = getWindow().getAttributes();
            params.type = LayoutParams.TYPE_SYSTEM_ERROR;
            params.dimAmount = 0.0F;
            params.width = 0;
            params.height = 0;
            params.alpha = 0.2f;
            params.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(params);
            getWindow().setFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    0xffffff);
            setOwnerActivity(activity);
            setCancelable(false);
        }

        // consume touch events
        public final boolean dispatchTouchEvent(MotionEvent motionevent) {
            return true;
        }

    }
}
