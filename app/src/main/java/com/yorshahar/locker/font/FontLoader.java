package com.yorshahar.locker.font;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by yorshahar on 7/16/15.
 */
public class FontLoader {

    public static final int HELVETICA_NEUE_REGULAR = 0;
    public static final int HELVETICA_NEUE_LIGHT = 1;
    public static final int HELVETICA_NEUE_ULTRA_LIGHT = 2;
    public static final int HELVETICA_NEUE_BOLD = 3;

    private static final int NUM_OF_CUSTOM_FONTS = 4;

    private static boolean fontsLoaded = false;

    private static Typeface[] fonts = new Typeface[4];

    private static String[] fontPath = {
            "fonts/HelveticaNeue.ttf",
            "fonts/HelveticaNeueLight.ttf",
            "fonts/HelveticaNeueUltraLight.ttf",
            "fonts/HelveticaNeueBold.ttf",
    };

    public static Typeface getTypeface(Context context, int fontIdentifier) {
        if (!fontsLoaded) {
            loadFonts(context);
        }
        return fonts[fontIdentifier];
    }

    private static void loadFonts(Context context) {
        for (int i = 0; i < NUM_OF_CUSTOM_FONTS; i++) {
            fonts[i] = Typeface.createFromAsset(context.getAssets(), fontPath[i]);
        }
        fontsLoaded = true;

    }
}
