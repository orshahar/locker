package com.yorshahar.locker.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.model.notification.Notification;
import com.yorshahar.locker.util.TimeUtil;

import java.util.List;

/**
 * List Adapter class for the notification list.
 * <p/>
 * Created by yorshahar on 8/12/15.
 */
public class NotificationListAdapter extends ArrayAdapter<Notification> {

    public NotificationListAdapter(Context context, List<Notification> notifications) {
        super(context, R.layout.notification_item, notifications);
    }

    @Override
    public void insert(Notification object, int index) {
        super.insert(object, index);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Notification object) {
        super.remove(object);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            item = inflater.inflate(R.layout.notification_item, parent, false);
        }

        Notification notification = getItem(position);

        TextView sourceTxtView = (TextView) item.findViewById(R.id.source);
        sourceTxtView.setText(notification.getSource());

//        TextView titleTextView = (TextView) item.findViewById(R.id.title);
////        titleTextView.setTypeface(FontLoader.getTypeface(getContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
//        titleTextView.setText(notification.getTitle());
////        if (notification.getTitle() == null || notification.getTitle().length() == 0) {
////            titleTextView.setVisibility(View.INVISIBLE);
////            titleTextView.setHeight(0);
////        }

        TextView bodyTextView = (TextView) item.findViewById(R.id.body);
//        bodyTextView.setTypeface(FontLoader.getTypeface(getContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
        bodyTextView.setText(notification.getBody());


        TextView dateArrivedTextView = (TextView) item.findViewById(R.id.dateArrived);
        dateArrivedTextView.setText(TimeUtil.formatDuration(notification.getDateArrived()));

        ImageView iconImageView = (ImageView) item.findViewById(R.id.icon);
        iconImageView.setImageDrawable(notification.getIcon());

        View topBorder = item.findViewById(R.id.topBorder);
        topBorder.setVisibility(View.GONE);

        View bottomBorder = item.findViewById(R.id.bottomBorder);
        bottomBorder.setVisibility(View.GONE);

        if (position == 0) {
            topBorder.setVisibility(View.VISIBLE);
        }

        if (position == getCount() - 1) {
            bottomBorder.setVisibility(View.VISIBLE);
        }

        item.setTag(position);

        return item;

    }

    public void deleteItemAtPosition(int position) {
        Notification notification = getItem(position);
        if (notification != null) {
            remove(notification);
        }
    }

}
