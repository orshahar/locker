package com.yorshahar.locker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

/**
 * Created by yorshahar on 9/18/15.
 */
public class BitmapUtil {

    public static byte[] getAsByteArray(final Drawable drawable) {
        BitmapDrawable bitDw = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInBytes = stream.toByteArray();

        return imageInBytes;
    }

    public static Bitmap convertByteArrayToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(
                bytes, 0,
                bytes.length);
    }

    public static Drawable convertByteArrayToDrawable(Context context, byte[] bytes) {
        return new BitmapDrawable(context.getResources(), convertByteArrayToBitmap(bytes));
    }

}

