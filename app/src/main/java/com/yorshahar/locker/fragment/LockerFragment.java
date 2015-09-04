package com.yorshahar.locker.fragment;


import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.font.FontLoader;
import com.yorshahar.locker.model.notification.Notification;
import com.yorshahar.locker.ui.widget.ShinyTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The lock screen main view fragment
 * <p/>
 * Created by yorshahar on 7/23/15.
 */
public class LockerFragment extends Fragment {

    public interface Delegate {

        void requestDisallowInterceptTouchEvent(boolean disallowIntercept);

        List<Notification> getNotifications();

        void deleteNotification(Notification notification);

        void onNotificationsChanged();

    }

    private int SCREEN_WIDTH = 720;
    private int REL_SWIPE_MIN_DISTANCE;
    private int REL_SWIPE_MAX_OFF_PATH;
    private int REL_SWIPE_THRESHOLD_VELOCITY;
    private int REL_DISPLAY_WIDTH;
    public static final String TIME_FORMAT = "h:mm";
    public static final String DATE_FORMAT = "EEEE, MMMM dd";

    private Delegate delegate;

    ListView notificationsListView;
    NotificationListAdapter listAdapter;
    //    List<Notification> notifications = new ArrayList<>();
    private TextView timeView;
    private TextView dateView;
    private ImageView phoneImageView;
    private ImageView cameraImageView;

    private Map<Long, Integer> mItemIdTopMap = new HashMap<>();

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

        timeView = (TextView) view.findViewById(R.id.timeTextView);
        timeView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
        updateTime();

        dateView = (TextView) view.findViewById(R.id.dateTextView);
        dateView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_LIGHT));
        updateDate();

        phoneImageView = (ImageView) view.findViewById(R.id.phoneImageView);
        phoneImageView.setVisibility(View.INVISIBLE);

        cameraImageView = (ImageView) view.findViewById(R.id.cameraImageView);

        ShinyTextView unlockTextView = (ShinyTextView) view.findViewById(R.id.unlockTextView);
        unlockTextView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_LIGHT));

        notificationsListView = (ListView) view.findViewById(R.id.notificationsListView);
        listAdapter = new NotificationListAdapter(getActivity(), new ArrayList<Notification>());
        notificationsListView.setAdapter(listAdapter);

        notificationsListView.setOnTouchListener(new View.OnTouchListener() {
            private float dX;
            private float dY;
            private float x;
            private float y;
            private boolean slideLeft;
            private boolean slideVerical;
            private boolean slideHorizontal;
            private int tempPosition = ListView.INVALID_POSITION;
            private float distance = 0;


            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN: {
//                        mViewPager.freeze();
                        tempPosition = notificationsListView.pointToPosition((int) event.getX(), (int) event.getY());
                        distance = 0;
                        slideVerical = false;
                        slideHorizontal = false;

                        if (tempPosition != ListView.INVALID_POSITION) {
                            if (delegate != null) {
                                delegate.requestDisallowInterceptTouchEvent(true);
                            }

                            View notificationItem = notificationsListView.getChildAt(tempPosition);
                            dX = notificationItem.getX() - event.getRawX();
                            dY = notificationItem.getY() - event.getRawY();
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (slideVerical) {
                            return false;
                        }

                        if (tempPosition != ListView.INVALID_POSITION) {
                            final View notificationItem = notificationsListView.getChildAt(tempPosition);

                            final boolean delete = slideLeft && x < -REL_SWIPE_MIN_DISTANCE;

                            notificationItem.animate()
                                    .x(delete ? -SCREEN_WIDTH : 0)
                                    .alpha(delete ? 0.0f : 1.0f)
                                    .setDuration(200)
                                    .setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
//                                            animation.removeListener(this);
//                                            notificationItem.clearAnimation();
//                                            mViewPager.unfreeze();
                                            if (delegate != null) {
                                                delegate.requestDisallowInterceptTouchEvent(false);
                                            }

                                            x = delete ? -SCREEN_WIDTH : 0;

                                            if (delete) {
                                                notificationItem.setX(0);
                                                notificationItem.setAlpha(1.0f);
                                                deleteNotificationWithAnimationAtPosition(tempPosition);
                                            }
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    })
                                    .start();

                        }
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (tempPosition != ListView.INVALID_POSITION) {
                            View itemView = notificationsListView.getChildAt(tempPosition);


                            float newX = event.getRawX() + dX;
                            float newY = event.getRawY() + dY;
                            float xDelta = newX - x;
                            float yDelta = newY - y;
                            slideLeft = xDelta < 0;
                            x = newX;
                            y = newY;


//                            if (!slideHorizontal && !slideVerical) {
//                                if (Math.abs(yDelta) > Math.abs(xDelta)) {
//                                    slideVerical = true;
//                                    slideHorizontal = false;
//                                } else {
//                                    slideVerical = false;
//                                    slideHorizontal = true;
//                                }
//                            }

                            if (slideVerical) {
                                return false;
                            }

                            float dim = Math.abs(x / SCREEN_WIDTH);
                            itemView.setX(x);
                            itemView.setAlpha(1.0f - dim);
                        }
                        break;
                    }
                    default: {
                        return false;
                    }
                }
                return true;
            }

        });

        DisplayMetrics dm = getResources().getDisplayMetrics();
        REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);
        REL_DISPLAY_WIDTH = dm.widthPixels;

        if (delegate != null) {
            delegate.requestDisallowInterceptTouchEvent(true);
        }

        return view;
    }

//    public void addNotification(Notification notification) {
//        if (!notifications.contains(notification)) {
//            listAdapter.insert(notification, 0);
//        }
//    }

    public int notificationCount() {
        return listAdapter.getCount();
    }

    public boolean hasNotifications() {
        return !listAdapter.isEmpty();
    }

    public void updateNotifications(List<Notification> notifications) {
        if (notifications == null) {
            return;
        }

        listAdapter.clear();
        listAdapter.addAll(notifications);
        listAdapter.notifyDataSetChanged();
    }

    public void update() {
        updateTime();
        updateDate();
        updateNotifications();
    }

    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.US);
        String timeString = sdf.format(System.currentTimeMillis());
        timeView.setText(timeString);
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String dateString = sdf.format(System.currentTimeMillis());
        dateView.setText(dateString);
    }

    private void updateNotifications() {
        List<Notification> notifications = delegate.getNotifications();
        updateNotifications(notifications);
    }

    private void deleteNotificationWithAnimationAtPosition(final int position) {
//        animateRemoval(position);

        final Notification itemToDelete = listAdapter.getItem(position);
        listAdapter.remove(itemToDelete);
        if (delegate != null) {
            delegate.onNotificationsChanged();
            delegate.deleteNotification(itemToDelete);
        }
    }

//    private void animateRemoval(int position) {
//        final View viewToRemove = notificationsListView.getChildAt(position);
//        int firstVisiblePosition = notificationsListView.getFirstVisiblePosition();
//        for (int i = 0; i < notificationsListView.getChildCount(); ++i) {
//            View child = notificationsListView.getChildAt(i);
////            if (child != viewToRemove) {
//            if (i != position) {
//                int firstPosition = firstVisiblePosition + i;
//                long itemId = listAdapter.getItemId(firstPosition);
//                mItemIdTopMap.put(itemId, child.getTop());
//            }
//        }
//        // Delete the item from the adapter
//        final Notification itemToDelete = listAdapter.getItem(position);
//        listAdapter.remove(itemToDelete);
//        delegate.onNotificationsChanged();
//        delegate.deleteNotification(itemToDelete);
//
//        final ViewTreeObserver observer = notificationsListView.getViewTreeObserver();
//        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//
//            @Override
//            public boolean onPreDraw() {
//                observer.removeOnPreDrawListener(this);
//                boolean firstAnimation = true;
//                int firstVisiblePosition = notificationsListView.getFirstVisiblePosition();
//                for (int i = 0; i < notificationsListView.getChildCount(); ++i) {
//                    final View child = notificationsListView.getChildAt(i);
//                    int position = firstVisiblePosition + i;
//                    long itemId = listAdapter.getItemId(position);
//                    Integer startTop = mItemIdTopMap.get(itemId);
//                    int top = child.getTop();
//                    if (startTop != null) {
//                        if (startTop != top) {
//                            int delta = startTop - top;
//                            child.setTranslationY(delta);
//                            child.setTranslationX(0);
//                            child.setX(0);
//                            child.setAlpha(1.0f);
//                            child.animate().setDuration(200).translationY(0);
//                            if (firstAnimation) {
//                                child.animate().withEndAction(new Runnable() {
//                                    public void run() {
////                                        mBackgroundContainer.hideBackground();
////                                        mSwiping = false;
//                                        notificationsListView.setEnabled(true);
//                                    }
//                                });
//                                firstAnimation = false;
//                            }
//                        }
//                    } else {
//                        // Animate new views along with the others. The catch is that they did not
//                        // exist in the start state, so we must calculate their starting position
//                        // based on neighboring views.
//                        int childHeight = child.getHeight() + notificationsListView.getDividerHeight();
//                        startTop = top + (i > 0 ? childHeight : -childHeight);
//                        int delta = startTop - top;
//                        child.setTranslationY(delta);
//                        child.setTranslationX(0);
//                        child.setX(0);
//                        child.setAlpha(1.0f);
//                        child.animate().setDuration(200).translationY(0);
//                        if (firstAnimation) {
//                            child.animate().withEndAction(new Runnable() {
//                                public void run() {
////                                    mBackgroundContainer.hideBackground();
////                                    mSwiping = false;
//                                    notificationsListView.setEnabled(true);
//                                }
//                            });
//                            firstAnimation = false;
//                        }
//                    }
//                }
//                mItemIdTopMap.clear();
//                return true;
//            }
//
//        });
//    }
}
