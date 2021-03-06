package com.yorshahar.locker.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Custom view that can draw either a stroke circle or a filled circle with Xfremode of ADD
 * <p/>
 * Created by yorshahar on 8/30/15.
 */
public class FillableCircleView extends View {

    public static final float CIRCLE_SKROKE_WIDTH = 2.0f;
    public static final int ADD_AMOUNT = 150;

    private float DENSITY;

    private Paint paint;
    int viewWidthHalf;
    int viewHeightHalf;
    int radius;
    private boolean filled;

    public FillableCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        DENSITY = dm.density;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(CIRCLE_SKROKE_WIDTH * DENSITY);
        paint.setColor(Color.argb(255, ADD_AMOUNT, ADD_AMOUNT, ADD_AMOUNT));
//        LightingColorFilter filter = new LightingColorFilter(0xffffffff, 0x88888888);
//        paint.setColorFilter(filter);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        viewWidthHalf = width / 2;
        viewHeightHalf = height / 2;
        radius = (viewWidthHalf > viewHeightHalf) ? viewHeightHalf - 1 : viewWidthHalf - 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, paint);
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;

        paint.setStyle(isFilled() ? Paint.Style.FILL : Paint.Style.STROKE);
        invalidate();
    }

}
