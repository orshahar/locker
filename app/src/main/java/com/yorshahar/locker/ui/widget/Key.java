package com.yorshahar.locker.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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

        void requestDisallowInterceptTouchEvent(boolean disallowIntercept);

        Bitmap getWallpaper();

    }

    public static final float CIRCLE_SKROKE_WIDTH = 3.0f;
    public static final int MAX_ADD_AMOUNT = 100;

    private Typeface typeface;
    private String value;
    private KeyDelegate delegate;
    private Paint outlinePaint;
    private Paint fillPaint;
    private Paint textPaint;
    private Rect bounds = new Rect();
    //    private Paint transparentPaint;
    private Bitmap xferBitmap;
    private Canvas xferCanvas;
    private boolean animating = false;
    private int addAmount = 0;
    int viewWidthHalf;
    int viewHeightHalf;
    int radius;

    public Key(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Key,
                0, 0);

        try {
            value = a.getString(R.styleable.Key_value);
            setTag(value);

            outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            outlinePaint.setStyle(Paint.Style.STROKE);
            outlinePaint.setStrokeWidth(CIRCLE_SKROKE_WIDTH);
            outlinePaint.setColor(Color.argb(255, MAX_ADD_AMOUNT, MAX_ADD_AMOUNT, MAX_ADD_AMOUNT));
            outlinePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));

            fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            fillPaint.setColor(Color.argb(255, MAX_ADD_AMOUNT, MAX_ADD_AMOUNT, MAX_ADD_AMOUNT));
            fillPaint.setStyle(Paint.Style.FILL);
            fillPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
//            fillPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.INNER));

            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(70f);
            bounds = new Rect();
            textPaint.getTextBounds(value, 0, value.length(), bounds);
            textPaint.setTypeface(typeface);
            textPaint.setColor(Color.WHITE);


            // Generate bitmap used for background
//            xferBitmap = delegate.getWallpaper();
//            xferBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.iphone_wallpaper);
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
        textPaint.setTypeface(typeface);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

//        if (xferBitmap == null || xferBitmap.getWidth() != width
//                || xferBitmap.getHeight() != height) {
//
//            if (xferBitmap != null) {
//                xferBitmap.recycle();
//            }
//
//            xferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            xferCanvas = new Canvas(xferBitmap);
//        }

        viewWidthHalf = width / 2;
        viewHeightHalf = height / 2;
        radius = (viewWidthHalf > viewHeightHalf) ? viewHeightHalf - 15 : viewWidthHalf - 20;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Generate bitmap used for background
//        Bitmap bm = delegate.getWallpaper();
//        temp.drawBitmap(bm, 0, 0, transparentPaint);
//        canvas.setBitmap(bm);

        // Draw the outer circle
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, outlinePaint);

        // Draw the circle filling
//        fillPaint.setAlpha(alpha);
        fillPaint.setColor(Color.argb(255, addAmount, addAmount, addAmount));
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius - CIRCLE_SKROKE_WIDTH / 2, fillPaint);

        // Draw the text
        canvas.drawText(value, viewWidthHalf - bounds.centerX(), viewHeightHalf - bounds.centerY(), textPaint);

//        canvas.drawBitmap(xferBitmap, 0, 0, null);

        // Invalidate view at about 62fps
        postDelayed(this, 8);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
//        return super.onTouchEvent(event);

        boolean handled = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                delegate.requestDisallowInterceptTouchEvent(true);
                delegate.onKeyDown(this);
                setPressed(true);
                addAmount = MAX_ADD_AMOUNT;
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
                delegate.requestDisallowInterceptTouchEvent(false);
                delegate.onKeyCanceled(this);
                setPressed(false);
                animating = true;
                invalidate();
                handled = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (event.getX() < -10 || event.getX() > 10) {
                    delegate.requestDisallowInterceptTouchEvent(true);
                } else {
                    delegate.requestDisallowInterceptTouchEvent(false);
                }
                handled = true;
                break;
            }
            default: {
                break;
            }
        }

        return handled;
    }

    private void startFadeOutAnimation() {
        if (animating) {
            delegate.onAnimationEnded(this);
        }
        animating = true;
        delegate.onAnimationStarted(this);
        invalidate();
    }

    @Override
    public void run() {
        if (animating) {
//            alpha -= 20;
//            if (alpha < 0) {
//                alpha = 0;
//                animating = false;
//                delegate.onAnimationEnded(this);
//            }
            addAmount -= 5;
            if (addAmount < 0) {
                addAmount = 0;
                animating = false;
                delegate.onAnimationEnded(this);
            }
            invalidate();
        }
    }

}
