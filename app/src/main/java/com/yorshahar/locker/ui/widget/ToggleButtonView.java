package com.yorshahar.locker.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Class for the toggle buttons in the control center. The class will render a different circle
 * around the icon based on the state.
 * <p/>
 * Created by yorshahar on 9/2/15.
 */
public class ToggleButtonView extends ImageView implements View.OnClickListener {

    public enum State {
        OFF,
        ON
    }

    public interface Delegate {

        void onToggleButtonStateChanges(ToggleButtonView button, State state);

    }

    private static final int COLOR_OFF = 0x11000000;
    private static final int COLOR_ON = 0xffffffff;


    private Delegate delegate;
    private State state;
    private Paint paint;
    int viewWidthHalf;
    int viewHeightHalf;
    int radius;

    public ToggleButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        setOnClickListener(this);
        state = State.OFF;
        paint.setColor(COLOR_OFF);
    }

    public Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public boolean isOn() {
        return state == State.ON;
    }

    public void turnOn() {
        state = State.ON;
        paint.setColor(COLOR_ON);
        if (delegate != null) {
            delegate.onToggleButtonStateChanges(this, state);
        }
        invalidate();
    }

    public boolean isOff() {
        return state == State.OFF;
    }

    public void turnOff() {
        state = State.OFF;
        paint.setColor(COLOR_OFF);
        if (delegate != null) {
            delegate.onToggleButtonStateChanges(this, state);
        }
        invalidate();
    }

    public void toggle() {
        if (isOn()) {
            turnOff();
        } else {
            turnOn();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        viewWidthHalf = width / 2;
        viewHeightHalf = height / 2;
        radius = (viewWidthHalf > viewHeightHalf) ? viewHeightHalf - 3 : viewWidthHalf - 3;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, paint);

        super.onDraw(canvas);
    }


///////////////////////////////////////////////////////////////////
//
// View.OnClickListener
//
///////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View v) {
        toggle();
    }


}
