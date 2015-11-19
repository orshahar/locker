package com.yorshahar.locker.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.activity.LockerMainActivity;
import com.yorshahar.locker.font.FontLoader;
import com.yorshahar.locker.ui.widget.FillableCircleView;
import com.yorshahar.locker.ui.widget.Key;

/**
 * The passcode entry fragment
 * <p/>
 * Created by yorshahar on 7/23/15.
 */
public class PasscodeFragment extends Fragment implements View.OnClickListener, Key.KeyDelegate {

    public interface Delegate {

        void onPasscodePassed();

        void requestDisallowInterceptTouchEvent(boolean disallowIntercept);

    }

    public static final float SHAKE_CIRCLES_SIZE = 15.0f;
    public static final int SHAKE_CIRCLES_DURATION_MILLIS = 20;
    public static final int SHAKE_CIRCLES_REPETITIONS = 5;
    public static final int SHAKE_CIRCLES_VIBRATION_DURATION = 200;

    private float DENSITY;

    private Delegate delegate;
    private int passcodeSize;
    private String passcode;
    private StringBuffer enteredKeysBuffer;
    private String currentKey = "";

    FillableCircleView[] circleViews;
    View circlesContainerView;
    //    private static final int SMALL_EMPTY_CIRCLE_IMAGE = R.drawable.white_circle_empty;
//    private static final int SMALL_FILLED_CIRCLE_IMAGE = R.drawable.white_circle_filled;
//    private static final int LARGE_EMPTY_CIRCLE_IMAGE = R.drawable.gray_circle_empty;
//    private static final int LARGE_FILLED_CIRCLE_IMAGE = R.drawable.gray_circle_filled;
    private boolean processing = false;
    private int keysAnimating = 0;
    private boolean passcodeMatch = false;
    private boolean securityFailed = false;

    public Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        passcode = getString(R.string.default_passcode);
        passcodeSize = passcode.length();
        enteredKeysBuffer = new StringBuffer(passcodeSize);
        circleViews = new FillableCircleView[passcodeSize];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, container, false);
        view.setBackgroundColor(Color.TRANSPARENT);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        DENSITY = dm.density;

        WindowManager.LayoutParams windowManager = getActivity().getWindow().getAttributes();
        windowManager.dimAmount = 0.0f;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        TextView enterPasscodeLabelView = (TextView) view.findViewById(R.id.enterPasscodeView);
        enterPasscodeLabelView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_LIGHT));

        TextView cancelTextView = (TextView) view.findViewById(R.id.cancelTextView);
        cancelTextView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_LIGHT));
        cancelTextView.setOnClickListener(this);

//        int keyWidth = SCREEN_WIDTH / 3 - 30;
//        int keyHeight = keyWidth;

        Key key0 = (Key) view.findViewById(R.id.key0);
        key0.setDelegate(this);
        key0.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
//        key0.getLayoutParams().height = keyHeight;
//        key0.getLayoutParams().width = keyWidth;

        Key key1 = (Key) view.findViewById(R.id.key1);
        key1.setDelegate(this);
        key1.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
//        key1.getLayoutParams().height = keyHeight;
//        key1.getLayoutParams().width = keyWidth;

        Key key2 = (Key) view.findViewById(R.id.key2);
        key2.setDelegate(this);
        key2.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
//        key2.getLayoutParams().height = keyHeight;
//        key2.getLayoutParams().width = keyWidth;

        Key key3 = (Key) view.findViewById(R.id.key3);
        key3.setDelegate(this);
        key3.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
//        key3.getLayoutParams().height = keyHeight;
//        key3.getLayoutParams().width = keyWidth;

        Key key4 = (Key) view.findViewById(R.id.key4);
        key4.setDelegate(this);
        key4.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
//        key4.getLayoutParams().height = keyHeight;
//        key4.getLayoutParams().width = keyWidth;

        Key key5 = (Key) view.findViewById(R.id.key5);
        key5.setDelegate(this);
        key5.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
//        key5.getLayoutParams().height = keyHeight;
//        key5.getLayoutParams().width = keyWidth;

        Key key6 = (Key) view.findViewById(R.id.key6);
        key6.setDelegate(this);
        key6.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
//        key6.getLayoutParams().height = keyHeight;
//        key6.getLayoutParams().width = keyWidth;

        Key key7 = (Key) view.findViewById(R.id.key7);
        key7.setDelegate(this);
        key7.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
//        key7.getLayoutParams().height = keyHeight;
//        key7.getLayoutParams().width = keyWidth;

        Key key8 = (Key) view.findViewById(R.id.key8);
        key8.setDelegate(this);
        key8.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
//        key8.getLayoutParams().height = keyHeight;
//        key8.getLayoutParams().width = keyWidth;

        Key key9 = (Key) view.findViewById(R.id.key9);
        key9.setDelegate(this);
        key9.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));
//        key9.getLayoutParams().height = keyHeight;
//        key9.getLayoutParams().width = keyWidth;

        circleViews[0] = (FillableCircleView) view.findViewById(R.id.circle1ImageView);
        circleViews[1] = (FillableCircleView) view.findViewById(R.id.circle2ImageView);
        circleViews[2] = (FillableCircleView) view.findViewById(R.id.circle3ImageView);
        circleViews[3] = (FillableCircleView) view.findViewById(R.id.circle4ImageView);

        circlesContainerView = view.findViewById(R.id.circlesContainerView);
        return view;
    }

    private void updatePasscode() {
        if (enteredKeysBuffer.length() < passcodeSize && currentKey != null && currentKey.length() > 0) {
            enteredKeysBuffer.append(currentKey);
            currentKey = "";
        }
    }

    private void updateCircles() {
        if (enteredKeysBuffer.length() < passcodeSize) {
            if (currentKey == null || currentKey.isEmpty()) {
                circleViews[enteredKeysBuffer.length()].setFilled(false);
            } else {
                circleViews[enteredKeysBuffer.length()].setFilled(true);
            }
        }
    }

    private void checkPasscode() {
        if (enteredKeysBuffer.length() == passcode.length()) {
            String enteredPasscode = enteredKeysBuffer.toString();
            passcodeMatch = enteredPasscode.equals(passcode);
            securityFailed = !passcodeMatch;
        }
    }

    private void shakeCircles() {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(SHAKE_CIRCLES_VIBRATION_DURATION);
        }
        final Animation animation3 = new TranslateAnimation(SHAKE_CIRCLES_SIZE * DENSITY, 0.0f, 0.0f, 0.0f);
        animation3.setDuration(SHAKE_CIRCLES_DURATION_MILLIS);
        animation3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                reset();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final Animation animation2 = new TranslateAnimation(-SHAKE_CIRCLES_SIZE * DENSITY, SHAKE_CIRCLES_SIZE * DENSITY, 0.0f, 0.0f);
        animation2.setRepeatCount(SHAKE_CIRCLES_REPETITIONS);
        animation2.setRepeatMode(Animation.REVERSE);
        animation2.setDuration(2 * SHAKE_CIRCLES_DURATION_MILLIS);
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circlesContainerView.startAnimation(animation3);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final Animation animation1 = new TranslateAnimation(0.0f, -SHAKE_CIRCLES_SIZE * DENSITY, 0.0f, 0.0f);
        animation1.setDuration(SHAKE_CIRCLES_DURATION_MILLIS);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circlesContainerView.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        circlesContainerView.startAnimation(animation1);

//        circlesContainerView.animate().translationX(-500).setDuration(20).setListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                circlesContainerView.animate().translationX(30).setDuration(40).setListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        circlesContainerView.animate().translationX(-30).setDuration(40).setListener(new Animator.AnimatorListener() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                circlesContainerView.animate().translationX(0).setDuration(20).setListener(new Animator.AnimatorListener() {
//                                    @Override
//                                    public void onAnimationStart(Animator animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animator animation) {
//                                        reset();
//                                    }
//
//                                    @Override
//                                    public void onAnimationCancel(Animator animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animator animation) {
//
//                                    }
//                                }).start();
//
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//
//                            }
//                        }).start();
//
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
//                }).start();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        }).start();
    }

    public void reset() {
        enteredKeysBuffer.delete(0, enteredKeysBuffer.length());
        for (FillableCircleView circleView : circleViews) {
            circleView.setFilled(false);
        }
        processing = false;
        keysAnimating = 0;
        passcodeMatch = false;
        securityFailed = false;
    }

    private void unlock() {
        reset();
        delegate.onPasscodePassed();
    }

    private boolean isAnimatingKeys() {
        return keysAnimating > 0;
    }

/////////////////////////////////////////////////
// KeyDelegate methods
/////////////////////////////////////////////////

    @Override
    public void onKeyDown(View key) {
        if (!processing) {
            processing = true;
            currentKey = (String) key.getTag();
            updateCircles();
            processing = false;
        }
    }

    @Override
    public void onKeyUp(View key) {
        if (!processing) {
            processing = true;
            updatePasscode();
            checkPasscode();
            processing = false;
        }
    }

    @Override
    public void onKeyCanceled(View key) {
        if (!processing) {
            processing = true;
            currentKey = "";
            updateCircles();
            processing = false;
        }
    }

    @Override
    public void onAnimationStarted(View key) {
        if (keysAnimating < passcodeSize) {
            keysAnimating++;
        }
    }

    @Override
    public void onAnimationEnded(View key) {
        if (!processing) {
            if (isAnimatingKeys()) {
                keysAnimating--;
            }
            if (!isAnimatingKeys()) {
                processing = true;
                if (passcodeMatch) {
                    unlock();
                } else if (securityFailed) {
                    shakeCircles();
                } else {
                    processing = false;
                }
            }
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        delegate.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public void onPause() {
        super.onPause();
        reset();
    }

    @Override
    public void onResume() {
        super.onResume();
        reset();
    }

    @Override
    public Bitmap getWallpaper() {
//        ImageView wallpaperImageView = ((LockerMainActivity) getActivity()).getWallpaperView();
//        Bitmap bitmap = ((BitmapDrawable) wallpaperImageView.getDrawable()).getBitmap();
//        return bitmap;

        return ((LockerMainActivity) getActivity()).getBlurredBackground();
    }

    /////////////////////////////////////////////////
// View.OnClickListener methods
/////////////////////////////////////////////////

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelTextView: {
                ((LockerMainActivity) getActivity()).reset();
                break;
            }
            default: {
                break;
            }
        }

    }

}
