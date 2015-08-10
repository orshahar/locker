package com.yorshahar.locker.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yorshahar.locker.R;

/**
 * Layout of a single key
 * <p/>
 * Created by yorshahar on 7/24/15.
 */
public class Key extends View implements Runnable {

    public interface KeyDelegate {

        void onKeyDown(View key);

        void onKeyUp(View key);

        void onKeyCanceled(View key);

        void onAnimationStarted(View key);

        void onAnimationEnded(View key);

    }

    public static final float CIRCLE_WIDTH =3.0f;
    public static final int CIRCLE_COLOR = Color.argb(255, 81, 74, 85);

    private Typeface typeface;
    private String value;
    private KeyDelegate delegate;
    private Paint paint;
    private Paint paint2;
    private Rect bounds = new Rect();
    private boolean animating = false;
    private int alpha = 0;

    public Key(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Key,
                0, 0);

        try {
            value = a.getString(R.styleable.Key_value);
            setTag(value);

            paint = new Paint();
            paint.setTextSize(70f);
            paint.getTextBounds(value, 0, value.length(), bounds);

            paint2 = new Paint();
        } finally {
            a.recycle();
        }
    }

    public KeyDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(KeyDelegate delegate) {
        this.delegate = delegate;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewWidthHalf = this.getMeasuredWidth() / 2;
        int viewHeightHalf = this.getMeasuredHeight() / 2;
        int radius = (viewWidthHalf > viewHeightHalf) ? viewHeightHalf - 15 : viewWidthHalf - 20;


        // Draw the outer circle
        paint.setAntiAlias(true);
        paint.setStrokeWidth(CIRCLE_WIDTH);
        paint.setColor(CIRCLE_COLOR);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, paint);

        // Draw the circle filling
        paint2.setAntiAlias(true);
        paint2.setColor(CIRCLE_COLOR);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setAlpha(alpha);
//        paint2.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.INNER));
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius - CIRCLE_WIDTH / 2, paint2);

        // Draw the text
        paint.setTypeface(typeface);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1.0f);
        canvas.drawText(value, viewWidthHalf - bounds.centerX(), viewHeightHalf - bounds.centerY(), paint);

        // Invalidate view at about 62fps
        postDelayed(this, 16);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
//        return super.onTouchEvent(event);

        boolean handled = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                delegate.onKeyDown(this);
                setPressed(true);
                alpha = 255;
                invalidate();
                handled = true;
                break;
            }
            case MotionEvent.ACTION_UP: {
                delegate.onKeyUp(this);
                setPressed(false);
                startFadeOutAnimation();
                handled = true;
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                delegate.onKeyCanceled(this);
                setPressed(false);
                animating = true;
                invalidate();
                handled = true;
                break;
            }
        }

        return handled;
    }

    private void startFadeOutAnimation() {
        animating = true;
        delegate.onAnimationStarted(this);
        invalidate();
    }

    @Override
    public void run() {
        if (animating) {
            alpha -= 25;
            if (alpha < 0) {
                alpha = 0;
                animating = false;
                delegate.onAnimationEnded(this);
            }
            invalidate();
        }
    }

}
