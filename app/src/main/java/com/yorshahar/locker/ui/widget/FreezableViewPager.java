package com.yorshahar.locker.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Custom ViewPager which can be frozen. When frozen pages cannot be manually swiped.
 * <p/>
 * Created by yorshahar on 9/3/15.
 */
public class FreezableViewPager extends ViewPager {
    private boolean frozen;

    public FreezableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        frozen = false;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isFrozen() || super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isFrozen() || super.onInterceptTouchEvent(event);
    }

    public void freeze() {
        setFrozen(true);
    }

    public void unfreeze() {
        setFrozen(false);
    }

}
