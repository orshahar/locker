package com.yorshahar.locker.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yorshahar on 8/12/15.
 */
public class NotificationItem extends ViewGroup {

    private ImageView icon;
    private TextView titleTextView;
    private TextView bodyTextView;
    private TextView dateArrived;

    public NotificationItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    public TextView getBodyTextView() {
        return bodyTextView;
    }

    public void setBodyTextView(TextView bodyTextView) {
        this.bodyTextView = bodyTextView;
    }

    public TextView getDateArrived() {
        return dateArrived;
    }

    public void setDateArrived(TextView dateArrived) {
        this.dateArrived = dateArrived;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

}
