package com.yorshahar.locker.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.font.FontLoader;
import com.yorshahar.locker.model.notification.Notification;
import com.yorshahar.locker.ui.widget.CustomDigitalClock;
import com.yorshahar.locker.ui.widget.ShinyTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The lock screen main view fragment
 * <p/>
 * Created by yorshahar on 7/23/15.
 */
public class LockerFragment extends Fragment {

    ListView notificationsListView;
    NotificationListAdapter listAdapter;
    List<Notification> notifications = new ArrayList<>();
    private TextView dateView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_locker, container, false);
        view.setBackgroundColor(Color.TRANSPARENT);

        CustomDigitalClock timeView = (CustomDigitalClock) view.findViewById(R.id.textClock);
        timeView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        dateView = (TextView) view.findViewById(R.id.dateTextView);
        dateView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
        dateView.getPaint().setFakeBoldText(true);


        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd", Locale.US);
        String dateString = sdf.format(System.currentTimeMillis());
        dateView.setText(dateString);

        ShinyTextView unlockTextView = (ShinyTextView) view.findViewById(R.id.unlockTextView);
        unlockTextView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.APPLE_THIN));
        unlockTextView.getPaint().setFakeBoldText(true);

        notificationsListView = (ListView) view.findViewById(R.id.notificationsListView);
        listAdapter = new NotificationListAdapter(getActivity(), notifications);
        notificationsListView.setAdapter(listAdapter);

        return view;
    }

    public void addNotification(Notification notification) {
        if (!notifications.contains(notification)) {
            listAdapter.insert(notification, 0);
        }
    }

    public int notificationCount() {
        return notifications.size();
    }

    public boolean hasNotifications() {
        return !notifications.isEmpty();
    }

    public void update() {
        updateDate();
        updateNotifications();
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd", Locale.US);
        String dateString = sdf.format(System.currentTimeMillis());
        dateView.setText(dateString);
    }

    private void updateNotifications() {
        listAdapter.notifyDataSetChanged();
    }

}
