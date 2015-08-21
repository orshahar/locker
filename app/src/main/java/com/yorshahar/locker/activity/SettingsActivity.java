package com.yorshahar.locker.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.service.SettingsService;
import com.yorshahar.locker.service.connection.AbstractServiceConnectionImpl;

/**
 * The activity for the settings page
 * <p/>
 * Created by yorshahar on 8/6/15.
 */
public class SettingsActivity extends Activity {

    private SettingsService settingsService;
    private boolean isSettingsServiceBound;
    private SettingsServiceConnection settingsServiceConnection;

    ListView settingsListView;
    BaseAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        settingsListView = (ListView) findViewById(R.id.settingsListView);
        listAdapter = new MyListAdapter();
        settingsListView.setAdapter(listAdapter);

        settingsServiceConnection = new SettingsServiceConnection(SettingsService.class);
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
            if (convertView == null) {
                switch (getItemViewType(position)) {
                    case TYPE_SWITCH: {
                        convertView = inflater.inflate(R.layout.settings_item_switch, parent, false);
                        break;
                    }
                    case TYPE_ITEM: {
                        convertView = inflater.inflate(R.layout.abc_list_menu_item_layout, parent, false);
                        break;
                    }
                    default: {
                        convertView = inflater.inflate(R.layout.abc_list_menu_item_layout, parent, false);
                        break;
                    }
                }
            }

            switch (position) {
                case 0: {
                    Switch switchView = (Switch) convertView.findViewById(R.id.switchView);
                    switchView.setText("Enable locker");
                    switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                settingsService.enableLocker();
                            } else {
                                settingsService.disableLocker();
                            }
                        }
                    });

                    break;
                }
                case 1: {
                    Switch switchView = (Switch) convertView.findViewById(R.id.switchView);
                    switchView.setText("Hide status bar");
                    switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
//                                settingsService.enableLocker();
                            } else {
//                                settingsService.disableLocker();
                            }
                        }
                    });

                    break;
                }
                case 2: {
                    Switch switchView = (Switch) convertView.findViewById(R.id.switchView);
                    switchView.setText("Notifications");
                    switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                settingsService.enableNotifications();
                            } else {
                                settingsService.disableNotifications();
                            }
                        }
                    });

                    break;
                }
                default: {
                    TextView titleView = (TextView) convertView.findViewById(R.id.title);
                    titleView.setText("Configure " + position);

                    break;
                }
            }

            return convertView;
        }
    }

    ;

}
