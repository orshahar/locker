package com.yorshahar.locker.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Formats the date of the notifications.
 * <p/>
 * Created by yorshahar on 8/16/15.
 */
public class TimeUtil {

    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long THREE_HOURS = 3 * ONE_HOUR;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;
    public final static long TWO_DAYS = 2 * ONE_DAY;

    private TimeUtil() {
    }

    /**
     * converts time (in milliseconds) to human-readable format
     * "<w> days, <x> hours, <y> minutes and (z) seconds"
     */
    public static String formatDuration(Date then) {
        Date now = new Date();
        long duration = now.getTime() - then.getTime();
        StringBuilder res = new StringBuilder();
        int amount = 0;
        if (duration >= TWO_DAYS) {
            amount = Long.valueOf(duration / ONE_DAY).intValue();
            res.append("Yesterday");
        } else if (duration > ONE_DAY) {
            amount = Long.valueOf(duration / ONE_DAY).intValue();
            res.append(amount).append("d ago");
        } else if (duration > THREE_HOURS) {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
            res.append(sdf.format(then));
        } else if (duration >= ONE_HOUR) {
            amount = Long.valueOf(duration / ONE_HOUR).intValue();
            res.append(amount).append("h ago");
        } else if (duration >= ONE_MINUTE) {
            amount = Long.valueOf(duration / ONE_MINUTE).intValue();
            res.append(amount).append("m ago");
        } else {
            res.append("now");
        }

        return res.toString();
    }

    public static void main(String args[]) {
        Date then = new Date(new Date().getTime() - 43976123L);

        System.out.println(formatDuration(then));

    }
}
