package com.yorshahar.locker.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    public interface Delegate {

        void requestDisallowInterceptTouchEvent(boolean disallowIntercept);

    }

    private Delegate delegate;

    private int REL_SWIPE_MIN_DISTANCE;
    private int REL_SWIPE_MAX_OFF_PATH;
    private int REL_SWIPE_THRESHOLD_VELOCITY;

    ListView notificationsListView;
    NotificationListAdapter listAdapter;
    List<Notification> notifications = new ArrayList<>();
    private TextView dateView;

    public Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

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


        DisplayMetrics dm = getResources().getDisplayMetrics();
        REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);

        final GestureDetector gestureDetector = new GestureDetector(getActivity().getApplicationContext(), new MyGestureDetector());

        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        notificationsListView.setOnTouchListener(gestureListener);

        if (delegate != null) {
            delegate.requestDisallowInterceptTouchEvent(true);
        }

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


    public void getSwipeItem(boolean isRight, int position) {
        Toast.makeText(
                getActivity(),
                "Swipe to " + (isRight ? "right" : "left") + " direction",
                Toast.LENGTH_SHORT)
                .show();
    }

    public void onItemClickListener(ListAdapter adapter, int position) {
        Toast.makeText(getActivity(), "Single tap on item position " + position, Toast.LENGTH_SHORT).show();
    }

    private void myOnItemClick(int position) {
        if (position < 0) {
            return;
        }

        onItemClickListener(notificationsListView.getAdapter(), position);
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private int temp_position = -1;

        // Detect a single-click and call my own handler.
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int pos = notificationsListView.pointToPosition((int) e.getX(), (int) e.getY());
            myOnItemClick(pos);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            temp_position = notificationsListView.pointToPosition((int) e.getX(), (int) e.getY());
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH) {
                return false;
            }

            if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
                int pos = notificationsListView.pointToPosition((int) e1.getX(), (int) e2.getY());

                if (pos >= 0 && temp_position == pos) {
                    getSwipeItem(false, pos);
                }
            } else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
                int pos = notificationsListView.pointToPosition((int) e1.getX(), (int) e2.getY());
                if (pos >= 0 && temp_position == pos) {
                    getSwipeItem(true, pos);
                }
            }
            return false;
        }

    }


}
