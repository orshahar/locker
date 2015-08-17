package com.yorshahar.locker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.service.LockService;

/**
 * Created by yorshahar on 8/6/15.
 */
public class SettingsActivity extends Activity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        startService(new Intent(this, LockService.class));

        textView = (TextView) findViewById(R.id.textView);
        textView.setText("Welcome to Locker");
        textView.setTextSize(30);
    }

}
