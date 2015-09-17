package com.yorshahar.locker.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.model.settings.Settings;
import com.yorshahar.locker.service.SettingsService;
import com.yorshahar.locker.service.connection.AbstractServiceConnectionImpl;

/**
 * The activity for the settings main page
 * <p/>
 * Created by yorshahar on 8/6/15.
 */
public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private enum SettingItemType {
        ENABLE_LOCKER("Enable Locker"),
        HIDE_STATUS_BAR("Hide Status Bar"),
        ENABLE_NOTIFICATIONS("Enable Notifications"),
        CHANGE_WALLPAPER("Change Wallpaper"),
        SECURITY("Security"),
        DATE_FORMAT("Date Format"),
        OTHER("");

        private String label;

        SettingItemType(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }

        public static SettingItemType getSettingsItem(int position) {
            SettingItemType item = SettingItemType.OTHER;

            for (SettingItemType settingItem : values()) {
                if (settingItem.ordinal() == position) {
                    item = settingItem;
                    break;
                }
            }

            return item;
        }
    }

    private SettingsService settingsService;
    private boolean isSettingsServiceBound;
    private SettingsServiceConnection settingsServiceConnection;

    Settings settings;

    ListView listView;
    BaseAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        settingsServiceConnection = new SettingsServiceConnection(SettingsService.class);
        bindToService();

        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new MyListAdapter();
        listView.setOnItemClickListener(this);
    }

    private void bindToService() {
        if (!isSettingsServiceBound) {
            Intent intent = new Intent(this, settingsServiceConnection.getClazz());
            startService(intent);
            bindService(intent, settingsServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        boolean success = settingsService.saveSetings(settings);
        stopService(new Intent(this, SettingsService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean success = settingsService.saveSetings(settings);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (settingsService != null) {
            settings = settingsService.getSettings();
//            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean success = settingsService.saveSetings(settings);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (settingsService != null) {
            settings = settingsService.getSettings();
//            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (SettingItemType.getSettingsItem(position)) {
            case CHANGE_WALLPAPER: {
                Intent intent = new Intent(this, ChangeWallpaperActivity.class);
                startActivity(intent);

                break;
            }
            default: {
                break;
            }
        }
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
            settings = settingsService.getSettings();

            listView.setAdapter(listAdapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isSettingsServiceBound = false;
        }
    }


    private class MyListAdapter extends BaseAdapter {
        private static final int TYPE_SWITCH = 0;
        private static final int TYPE_ITEM = 1;

        private LayoutInflater inflater;

        public MyListAdapter() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            int itemViewType;

            switch (position) {
                case 0:
                case 1:
                case 2: {
                    itemViewType = TYPE_SWITCH;
                    break;
                }
                default: {
                    itemViewType = TYPE_ITEM;
                    break;
                }
            }

            return itemViewType;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                switch (getItemViewType(position)) {
                    case TYPE_SWITCH: {
                        view = inflater.inflate(R.layout.settings_item_switch, parent, false);

                        Switch switchView = (Switch) view.findViewById(R.id.switchView);
                        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                int buttonPosition = (Integer) buttonView.getTag();
                                SettingItemType itemType = SettingItemType.getSettingsItem(buttonPosition);
                                switch (itemType) {
                                    case ENABLE_LOCKER: {
                                        if (isChecked) {
                                            enableLocker();
                                        } else {
                                            disableLocker();
                                        }
                                        break;
                                    }
                                    case HIDE_STATUS_BAR: {
                                        if (isChecked) {
                                            hideStatusBar();
                                        } else {
                                            showStatusBar();
                                        }
                                        break;
                                    }
                                    case ENABLE_NOTIFICATIONS: {
                                        if (isChecked) {
                                            enableNotifications();
                                        } else {
                                            disableNotifications();
                                        }
                                        break;
                                    }
                                }
                            }
                        });

                        break;
                    }
                    case TYPE_ITEM: {
                        view = inflater.inflate(R.layout.settings_item, parent, false);
                        break;
                    }
                    default: {
                        view = inflater.inflate(R.layout.settings_item, parent, false);
                        break;
                    }
                }
            }

            SettingItemType itemType = SettingItemType.getSettingsItem(position);
            switch (itemType) {
                case ENABLE_LOCKER: {
                    Switch switchView = (Switch) view.findViewById(R.id.switchView);
                    switchView.setTag(position);
                    switchView.setChecked(settings.isLockerEnabled());
                    switchView.setEnabled(true);

                    TextView labelView = (TextView) view.findViewById(R.id.labelTextView);
                    labelView.setText(itemType.label());

                    break;
                }
                case HIDE_STATUS_BAR: {
                    Switch switchView = (Switch) view.findViewById(R.id.switchView);
                    switchView.setTag(position);
                    switchView.setChecked(settings.isHideStatusBar());
                    switchView.setEnabled(settings.isLockerEnabled());

                    TextView labelView = (TextView) view.findViewById(R.id.labelTextView);
                    labelView.setText(itemType.label());

                    break;
                }
                case ENABLE_NOTIFICATIONS: {
                    Switch switchView = (Switch) view.findViewById(R.id.switchView);
                    switchView.setTag(position);
                    switchView.setChecked(settings.isNotificationsEnabled());
                    switchView.setEnabled(settings.isLockerEnabled());

                    TextView labelView = (TextView) view.findViewById(R.id.labelTextView);
                    labelView.setText(itemType.label());

                    break;
                }
                case CHANGE_WALLPAPER: {
                    TextView labelView = (TextView) view.findViewById(R.id.labelTextView);
                    labelView.setText(itemType.label());

                    break;
                }
                case SECURITY: {
                    TextView labelView = (TextView) view.findViewById(R.id.labelTextView);
                    labelView.setText(itemType.label());

                    break;
                }
                case DATE_FORMAT: {
                    TextView labelView = (TextView) view.findViewById(R.id.labelTextView);
                    labelView.setText(itemType.label());

                    break;
                }
                default: {
                    TextView labelView = (TextView) view.findViewById(R.id.labelTextView);
                    labelView.setText("Configure " + position);

                    break;
                }
            }

            return view;
        }
    }

    private void enableLocker() {
        if (!settings.isLockerEnabled()) {
            settingsService.enableLocker();
            settings.setLockerEnabled(true);
            listAdapter.notifyDataSetChanged();
        }
    }

    private void disableLocker() {
        if (settings.isLockerEnabled()) {
            settingsService.disableLocker();
            settings.setLockerEnabled(false);
            listAdapter.notifyDataSetChanged();
        }
    }

    private void enableNotifications() {
        if (!settings.isNotificationsEnabled()) {
            settingsService.enableNotifications();
            settings.setNotificationsEnabled(true);
        }
    }

    private void disableNotifications() {
        if (settings.isNotificationsEnabled()) {
            settingsService.disableNotifications();
            settings.setNotificationsEnabled(false);
        }
    }

    private void hideStatusBar() {
        if (!settings.isHideStatusBar()) {
            settingsService.hideStatusBar();
            settings.setHideStatusBar(true);
        }
    }

    private void showStatusBar() {
        if (settings.isNotificationsEnabled()) {
            settingsService.showStatusBar();
            settings.setHideStatusBar(false);
        }
    }

}
