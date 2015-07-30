package com.yorshahar.locker.fragment;


import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.font.FontLoader;
import com.yorshahar.locker.ui.widget.Key;

/**
 * The passcode entry fragment
 * <p/>
 * Created by yorshahar on 7/23/15.
 */
public class PasscodeFragment extends Fragment implements Key.KeyDelegate {

    public interface FragmentDelegate {

        void onPasscodePassed();

    }

    private FragmentDelegate delegate;
    private int passcodeSize;
    private String passcode;
    private StringBuffer enteredKeysBuffer;
    private String currentKey = "";

    ImageView[] circleImageViews;
    View circlesContainerView;
    private static final int SMALL_EMPTY_CIRCLE_IMAGE = R.drawable.white_circle_empty;
    private static final int SMALL_FILLED_CIRCLE_IMAGE = R.drawable.white_circle_filled;
    private static final int LARGE_EMPTY_CIRCLE_IMAGE = R.drawable.gray_circle_empty;
    private static final int LARGE_FILLED_CIRCLE_IMAGE = R.drawable.gray_circle_filled;
    private boolean processing = false;
    private boolean passcodeMatch = false;
    private boolean securityFailed = false;

    public FragmentDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(FragmentDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        passcodeSize = 4;
        passcode = "1234";
        enteredKeysBuffer = new StringBuffer(passcodeSize);
        circleImageViews = new ImageView[passcodeSize];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, container, false);

        WindowManager.LayoutParams windowManager = getActivity().getWindow().getAttributes();
        windowManager.dimAmount = 0.75f;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        TextView enterPasscodeLabelView = (TextView) view.findViewById(R.id.enterPasscodeView);
        enterPasscodeLabelView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.APPLE_THIN));

        TextView cancelTextView = (TextView) view.findViewById(R.id.cancelTextView);
        cancelTextView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.APPLE_THIN));

        Key key0 = (Key) view.findViewById(R.id.key0);
        key0.setDelegate(this);
        key0.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        Key key1 = (Key) view.findViewById(R.id.key1);
        key1.setDelegate(this);
        key1.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        Key key2 = (Key) view.findViewById(R.id.key2);
        key2.setDelegate(this);
        key2.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        Key key3 = (Key) view.findViewById(R.id.key3);
        key3.setDelegate(this);
        key3.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        Key key4 = (Key) view.findViewById(R.id.key4);
        key4.setDelegate(this);
        key4.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        Key key5 = (Key) view.findViewById(R.id.key5);
        key5.setDelegate(this);
        key5.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        Key key6 = (Key) view.findViewById(R.id.key6);
        key6.setDelegate(this);
        key6.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        Key key7 = (Key) view.findViewById(R.id.key7);
        key7.setDelegate(this);
        key7.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        Key key8 = (Key) view.findViewById(R.id.key8);
        key8.setDelegate(this);
        key8.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        Key key9 = (Key) view.findViewById(R.id.key9);
        key9.setDelegate(this);
        key9.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        circleImageViews[0] = (ImageView) view.findViewById(R.id.circle1ImageView);
        circleImageViews[1] = (ImageView) view.findViewById(R.id.circle2ImageView);
        circleImageViews[2] = (ImageView) view.findViewById(R.id.circle3ImageView);
        circleImageViews[3] = (ImageView) view.findViewById(R.id.circle4ImageView);

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
                circleImageViews[enteredKeysBuffer.length()].setImageResource(SMALL_EMPTY_CIRCLE_IMAGE);
            } else {
                circleImageViews[enteredKeysBuffer.length()].setImageResource(SMALL_FILLED_CIRCLE_IMAGE);
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
        circlesContainerView.animate().rotation(10).setDuration(50).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                circlesContainerView.animate().rotation(-10).setDuration(100).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        circlesContainerView.animate().rotation(10).setDuration(100).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                circlesContainerView.animate().rotation(0).setDuration(50).setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        reset();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                }).start();

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void reset() {
        enteredKeysBuffer.delete(0, enteredKeysBuffer.length());
        for (ImageView circleImageView : circleImageViews) {
            circleImageView.setImageResource(SMALL_EMPTY_CIRCLE_IMAGE);
        }
        processing = false;
        passcodeMatch = false;
        securityFailed = false;
    }

    private void unlock() {
        delegate.onPasscodePassed();
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
    public void onAnimationEnded(View key) {
        if (!processing) {
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
