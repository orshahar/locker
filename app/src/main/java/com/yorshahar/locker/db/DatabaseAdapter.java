package com.yorshahar.locker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yorshahar.locker.model.settings.Settings;

/**
 * Created by yorshahar on 9/16/15.
 */
public class DatabaseAdapter {

    DatabaseHelper databaseHelper;

    public DatabaseAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public long updateSettings(boolean lockerEnabled, boolean notificationsEnabled, boolean securityEnabled, boolean statusBarHidden, String passcode) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_LOCKER_ENABLED, lockerEnabled);
        contentValues.put(DatabaseHelper.COLUMN_NOTIFICATIONS_ENABLED, notificationsEnabled);
        contentValues.put(DatabaseHelper.COLUMN_SECURITY_ENABLED, securityEnabled);
        contentValues.put(DatabaseHelper.COLUMN_HIDE_STATUS_BAR, statusBarHidden);
        contentValues.put(DatabaseHelper.COLUMN_PASSCODE, passcode);
        String[] args = new String[0];
        return db.update(DatabaseHelper.TABLE_SETTINGS, contentValues, null, args);
    }

    public Settings getSettings() {
        Settings settings = new Settings();

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String[] columns = {
                DatabaseHelper.COLUMN_UID,
                DatabaseHelper.COLUMN_LOCKER_ENABLED,
                DatabaseHelper.COLUMN_NOTIFICATIONS_ENABLED,
                DatabaseHelper.COLUMN_SECURITY_ENABLED,
                DatabaseHelper.COLUMN_HIDE_STATUS_BAR,
                DatabaseHelper.COLUMN_PASSCODE
        };
        Cursor cursor = null;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_SETTINGS,
                    columns,
                    null,
                    new String[0],
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                int columnIndex = 0;
                settings.setId(cursor.getLong(columnIndex++));
                settings.setLockerEnabled(cursor.getInt(columnIndex++) == 1);
                settings.setNotificationsEnabled(cursor.getInt(columnIndex++) == 1);
                settings.setSecurityEnabled(cursor.getInt(columnIndex++) == 1);
                settings.setHideStatusBar(cursor.getInt(columnIndex++) == 1);
                settings.setPasscode(cursor.getString(columnIndex));
            }
        } catch (Exception e) {
            int a = 2;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return settings;
    }

    public void createDb() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "LOCKERDB";
        private static final int DATABASE_VERSION = 1;

        // Table names
        private static final String TABLE_SETTINGS = "settings";

        // Column names
        private static final String COLUMN_LOCKER_ENABLED = "LockerEnabled";
        private static final String COLUMN_NOTIFICATIONS_ENABLED = "NotificationsEnabled";
        private static final String COLUMN_PASSCODE = "Passcode";
        private static final String COLUMN_SECURITY_ENABLED = "SecurityEnabled";
        private static final String COLUMN_HIDE_STATUS_BAR = "HideStatusBar";
        private static final String COLUMN_UID = "_id";

        // Queries
        private static final String QUERY_CREATE_TABLE_SETTINGS = "CREATE TABLE " + TABLE_SETTINGS + " ("
                + COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LOCKER_ENABLED + " VARCHAR(1) NOT NULL, "
                + COLUMN_NOTIFICATIONS_ENABLED + " VARCHAR(1) NOT NULL, "
                + COLUMN_SECURITY_ENABLED + " VARCHAR(1) NOT NULL, "
                + COLUMN_HIDE_STATUS_BAR + " VARCHAR(1) NOT NULL, "
                + COLUMN_PASSCODE + " VARCHAR(16))";

        private Context context;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(QUERY_CREATE_TABLE_SETTINGS);

                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_LOCKER_ENABLED, false);
                contentValues.put(COLUMN_NOTIFICATIONS_ENABLED, false);
                contentValues.put(COLUMN_SECURITY_ENABLED, false);
                contentValues.put(COLUMN_HIDE_STATUS_BAR, false);
                contentValues.put(COLUMN_PASSCODE, "2483");
                db.insert(TABLE_SETTINGS, null, contentValues);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
