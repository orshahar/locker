package com.yorshahar.locker.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.model.notification.Notification;

import java.util.List;

/**
 * Created by yorshahar on 8/12/15.
 */
public class NotificationListAdapter extends ArrayAdapter<Notification> {

    public NotificationListAdapter(Context context, List<Notification> notifications) {
        super(context, R.layout.notification_item, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            item = inflater.inflate(R.layout.notification_item, null, true);
        }

        Notification notification = getItem(position);

        TextView sourceTxtView = (TextView) item.findViewById(R.id.source);
        sourceTxtView.setText(notification.getSource());

        TextView titleTextView = (TextView) item.findViewById(R.id.title);
//        titleTextView.setTypeface(FontLoader.getTypeface(getContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
        titleTextView.setText(notification.getTitle());

        TextView bodyTextView = (TextView) item.findViewById(R.id.body);
//        bodyTextView.setTypeface(FontLoader.getTypeface(getContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
        bodyTextView.setText(notification.getBody());


        TextView dateArrivedTextView = (TextView) item.findViewById(R.id.dateArrived);
        return item;
    }

}
