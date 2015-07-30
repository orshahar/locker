package com.yorshahar.locker.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.font.FontLoader;
import com.yorshahar.locker.ui.widget.CustomDigitalClock;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * The lock screen main view fragment
 * <p/>
 * Created by yorshahar on 7/23/15.
 */
public class LockerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locker, container, false);

        CustomDigitalClock timeView = (CustomDigitalClock) view.findViewById(R.id.textClock);
        timeView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        TextView dateView = (TextView) view.findViewById(R.id.dateTextView);
        dateView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd", Locale.US);
        String dateString = sdf.format(System.currentTimeMillis());
        dateView.setText(dateString);

        TextView unlockTextView = (TextView) view.findViewById(R.id.unlockTextView);
        unlockTextView.setTypeface(FontLoader.getTypeface(getActivity().getApplicationContext(), FontLoader.HELVETICA_NEUE_ULTRA_LIGHT));

        return view;
    }

}
