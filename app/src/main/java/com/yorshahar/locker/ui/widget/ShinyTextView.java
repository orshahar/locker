package com.yorshahar.locker.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

/**
 * TextView with the effect of light shining on it and moving across it.
 * <p/>
 * Created by yorshahar on 8/12/15.
 */
public class ShinyTextView extends TextView {

    public static final int COLOR_OFF = 0x55ffffff;
    public static final int COLOR_ON = 0xffffffff;
    public static final float GRADIENT_DIAMETER = 0.3f;
    public static final int DURATION = 6;

    private ValueAnimator mAnimator;
    private float mGradientCenter;
    private PaintDrawable mShineDrawable;

    public ShinyTextView(final Context context) {
        this(context, null);
    }

    public ShinyTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShinyTextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!isInEditMode()) {
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            mShineDrawable = new PaintDrawable();
            mShineDrawable.setBounds(0, 0, w, h);
            mShineDrawable.getPaint().setShader(generateGradientShader(getWidth(), 0, 0, 0));
            mShineDrawable.getPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            mAnimator = ValueAnimator.ofFloat(0, 1);
            mAnimator.setDuration(DURATION * w); // custom
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setInterpolator(new LinearInterpolator()); // Custom
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation) {
                    final float value = animation.getAnimatedFraction();
                    mGradientCenter = (1 + 2 * GRADIENT_DIAMETER) * value - GRADIENT_DIAMETER;
                    final float gradientStart = mGradientCenter - GRADIENT_DIAMETER;
                    final float gradientEnd = mGradientCenter + GRADIENT_DIAMETER;
                    Shader shader = generateGradientShader(w, gradientStart, mGradientCenter, gradientEnd);
                    mShineDrawable.getPaint().setShader(shader);
                    invalidate();
                }
            });
            mAnimator.start();
        }
    }

    @Override
    protected void onDraw(@NonNull final Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && mShineDrawable != null) {
            mShineDrawable.draw(canvas);
        }
    }

    private Shader generateGradientShader(int width, float... positions) {
        int[] colorRepartition = {COLOR_OFF, COLOR_ON, COLOR_OFF};
        return new LinearGradient(
                0,
                0,
                width,
                0,
                colorRepartition,
                positions,
                Shader.TileMode.REPEAT
        );
    }
}
