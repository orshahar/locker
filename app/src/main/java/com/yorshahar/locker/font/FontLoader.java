package com.yorshahar.locker.font;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by yorshahar on 7/16/15.
 */
public class FontLoader {

    public static final int APPLE_THIN = 0;
    public static final int HERCULANUM = 1;
    public static final int TIME = 2;
    public static final int HELVETICA_NEUE_ULTRA_LIGHT = 3;
    public static final int HELVETICA_NEUE = 4;

    private static final int NUM_OF_CUSTOM_FONTS = 5;

    private static boolean fontsLoaded = false;

    private static Typeface[] fonts = new Typeface[5];

    private static String[] fontPath = {
            "fonts/AppleSDGothicNeo-Thin.otf",
            "fonts/Herculanum.ttf",
            "fonts/time.ttf",
            "fonts/hlul.ttf",
            "fonts/HelveticaNeue.dfont"
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
