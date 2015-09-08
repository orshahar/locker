package com.yorshahar.locker.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Class for the app launchers in the control center. The class will render a different square with
 * rounded corners around the icon based on the state.
 * <p/>
 * Created by yorshahar on 9/2/15.
 */
public class AppLauncherView extends ImageView implements View.OnClickListener {

    public enum State {
        OFF,
        ON
    }

    public interface Delegate {

        void onAppLauncherStateChanges(AppLauncherView button, State state);

    }

    private static final int COLOR_OFF = 0x11000000;
    private static final int COLOR_ON = 0xffffffff;
    private static final int ROUNDED_CORNERS_SIZE = 30;


    private Delegate delegate;
    private State state;
    private Paint paint;
    private RectF rect;

    public AppLauncherView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(this);
        state = State.OFF;

        paint = new Paint();
        paint.setColor(COLOR_OFF);

        rect = new RectF();
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
        if (delegate != null) {
            delegate.onAppLauncherStateChanges(this, State.ON);
        }
    }

    public boolean isOff() {
        return state == State.OFF;
    }

    public void turnOff() {
        if (delegate != null) {
            delegate.onAppLauncherStateChanges(this, State.OFF);
        }
    }

    public void toggle() {
        if (isOn()) {
            turnOff();
        } else {
            turnOn();
        }
    }

    public void updateState(State state) {
        if (this.state != state) {
            this.state = state;
            paint.setColor(isOn() ? COLOR_ON : COLOR_OFF);
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        rect.set(0, 0, width, height);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawRoundRect(rect, ROUNDED_CORNERS_SIZE, ROUNDED_CORNERS_SIZE, paint);

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
