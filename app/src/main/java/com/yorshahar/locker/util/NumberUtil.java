package com.yorshahar.locker.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yorshahar on 9/10/15.
 */
public class NumberUtil {

    public static Set<Integer> getSetBits(Integer val) {
        Set<Integer> setBits = new HashSet<>();

        if (null != val) {
            int valInt = val;
            int bit = 1;
            while (valInt > 0) {
                if ((valInt & 0x1) == 1) {
                    setBits.add(bit);
                }
                valInt = valInt >> 1;
                bit = bit << 1;
            }
        }

        return setBits;
    }

}
