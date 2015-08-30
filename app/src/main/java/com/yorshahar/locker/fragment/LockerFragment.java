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
import android.view.ViewTreeObserver;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yorshahar.locker.R;
import com.yorshahar.locker.font.FontLoader;
import com.yorshahar.locker.model.notification.Notification;
import com.yorshahar.locker.ui.widget.ShinyTextView;

import java.text.SimpleDateFormat;
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

    private Delegate delegate;

    public static final String TIME_FORMAT = "h:mm";
    public static final String DATE_FORMAT = "EEEE, MMMM dd";
    private int REL_SWIPE_MIN_DISTANCE;
    private int REL_SWIPE_MAX_OFF_PATH;
    private int REL_SWIPE_THRESHOLD_VELOCITY;
    private int REL_DISPLAY_WIDTH;

    ListView notificationsListView;
    NotificationListAdapter listAdapter;
    //    List<Notification> notifications = new ArrayList<>();
    private TextView timeView;
    private TextView dateView;

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

        ShinyTextView unlockTextView = (ShinyTextView) view.findViewById(R.id.unlockTextView);
        unlockTextView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_LIGHT));

        notificationsListView = (ListView) view.findViewById(R.id.notificationsListView);
        listAdapter = new NotificationListAdapter(getActivity(), delegate.getNotifications());
        notificationsListView.setAdapter(listAdapter);


        DisplayMetrics dm = getResources().getDisplayMetrics();
        REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);
        REL_DISPLAY_WIDTH = dm.widthPixels;

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

        private int tempPosition = ListView.INVALID_POSITION;
        float distance = 0;

        // Detect a single-click and call my own handler.
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int pos = notificationsListView.pointToPosition((int) e.getX(), (int) e.getY());
            myOnItemClick(pos);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            tempPosition = notificationsListView.pointToPosition((int) e.getX(), (int) e.getY());
            if (tempPosition != ListView.INVALID_POSITION) {
                delegate.requestDisallowInterceptTouchEvent(true);
            }
            distance = 0;
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            return super.onScroll(e1, e2, distanceX, distanceY);
            distance -= distanceX;

            if (tempPosition != ListView.INVALID_POSITION) {
                View view = notificationsListView.getChildAt(tempPosition);

                view.setTranslationX(distance);
                view.setAlpha(1.0f - Math.abs(distance) / REL_DISPLAY_WIDTH);
//                return true;
            }

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH) {
                delegate.requestDisallowInterceptTouchEvent(false);
                return false;
            }

//            int pos = ListView.INVALID_POSITION;

            if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
                if (tempPosition != ListView.INVALID_POSITION) {
                    deleteNotificationWithAnimationAtPosition(tempPosition);
                    return true;
                }

//                pos = notificationsListView.pointToPosition((int) e2.getX(), (int) e2.getY());
//                pos = 0;
////                if (pos >= 0 && tempPosition == pos) {
////                    getSwipeItem(false, pos);
////                }
            } else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
//                if (tempPosition != ListView.INVALID_POSITION) {
//                    deleteNotificationWithAnimationAtPosition(tempPosition);
//                    return true;
//                }

//                pos = notificationsListView.pointToPosition((int) e2.getX(), (int) e2.getY());
//
////                if (pos >= 0 && tempPosition == pos) {
////                    getSwipeItem(true, pos);
////                }
            }

//            if (pos != ListView.INVALID_POSITION) {
//                if (tempPosition == pos) {
//                    deleteNotificationWithAnimationAtPosition(pos);
//                }
//            } else {
            if (tempPosition != ListView.INVALID_POSITION) {
                View view = notificationsListView.getChildAt(tempPosition);
                if (view != null) {
                    view.animate()
                            .translationX(0)
                            .alpha(1.0f)
                            .setDuration(200)
                            .start();
                }
            }
//            }

            delegate.requestDisallowInterceptTouchEvent(false);
            return false;
        }

    }

    private void deleteNotificationWithAnimationAtPosition(final int position) {
        final View view = notificationsListView.getChildAt(position);

        try {
            Notification notification = listAdapter.getItem(position);

            animateRemoval(position, view);
            view.setTranslationX(0);
            view.setAlpha(1.0f);

            delegate.onNotificationsChanged();
            delegate.deleteNotification(notification);
        } catch (Exception e) {
            int a = 2;
        }

//        view.animate()
//                .scaleY(0f)
//                .setDuration(200)
//                .setListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        view.setTranslationX(0);
//                        view.setAlpha(1.0f);
//
//                        Notification notification = listAdapter.getItem(position);
//                        delegate.deleteNotification(notification);
//
//                        view.setScaleY(1.0f);
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                })
//                .start();
    }

    private void animateRemoval(int position, View viewToRemove) {
        int firstVisiblePosition = notificationsListView.getFirstVisiblePosition();
        for (int i = 0; i < notificationsListView.getChildCount(); ++i) {
            View child = notificationsListView.getChildAt(i);
            if (child != viewToRemove) {
                int firstPosition = firstVisiblePosition + i;
                long itemId = listAdapter.getItemId(firstPosition);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
//        int position = mListView.getPositionForView(viewToRemove);
        listAdapter.remove(listAdapter.getItem(position));

        final ViewTreeObserver observer = notificationsListView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = notificationsListView.getFirstVisiblePosition();
                for (int i = 0; i < notificationsListView.getChildCount(); ++i) {
                    final View child = notificationsListView.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = listAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(200).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
//                                        mBackgroundContainer.hideBackground();
//                                        mSwiping = false;
                                        notificationsListView.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + notificationsListView.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(200).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
//                                    mBackgroundContainer.hideBackground();
//                                    mSwiping = false;
                                    notificationsListView.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }
}
