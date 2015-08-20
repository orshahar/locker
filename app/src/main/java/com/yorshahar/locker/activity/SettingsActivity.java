package com.yorshahar.locker.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.service.SettingsService;
import com.yorshahar.locker.service.connection.AbstractServiceConnectionImpl;

/**
 * Created by yorshahar on 8/6/15.
 */
public class SettingsActivity extends Activity {

    private TextView textView;
    private SettingsService settingsService;
    private boolean isSettingsServiceBound;
    private SettingsServiceConnection settingsServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        textView = (TextView) findViewById(R.id.textView);
        textView.setText("Welcome to Locker");
        textView.setTextSize(30);

        settingsServiceConnection = new SettingsServiceConnection(SettingsService.class);
//        startService(new Intent(this, SettingsService.class));
        bindToService();
    }

    private void bindToService() {
        if (!isSettingsServiceBound) {
            Intent intent = new Intent(this, settingsServiceConnection.getClazz());
            bindService(intent, settingsServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(this, SettingsService.class));
    }

    private class SettingsServiceConnection extends AbstractServiceConnectionImpl {

        public SettingsServiceConnection(Class clazz) {
            super(clazz);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SettingsService.MyLocalBinder binder = (SettingsService.MyLocalBinder) service;
            settingsService = binder.getService();
            isSettingsServiceBound = true;

            settingsService.setActivity(SettingsActivity.this);

            settingsService.enableLocker();
            settingsService.enableNotifications();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isSettingsServiceBound = false;
        }
    }

}
