package com.yorshahar.locker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yorshahar.locker.model.notification.Notification;
import com.yorshahar.locker.model.settings.Settings;
import com.yorshahar.locker.util.BitmapUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yorshahar on 9/16/15.
 */
public class DatabaseAdapter {

    private DatabaseHelper databaseHelper;
    private Context context;
    private final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();


    public DatabaseAdapter(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public long updateSettings(boolean lockerEnabled, boolean notificationsEnabled, boolean securityEnabled, boolean statusBarHidden, String passcode, int wallpaper) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_LOCKER_ENABLED, lockerEnabled);
        contentValues.put(DatabaseHelper.COLUMN_NOTIFICATIONS_ENABLED, notificationsEnabled);
        contentValues.put(DatabaseHelper.COLUMN_SECURITY_ENABLED, securityEnabled);
        contentValues.put(DatabaseHelper.COLUMN_HIDE_STATUS_BAR, statusBarHidden);
        contentValues.put(DatabaseHelper.COLUMN_PASSCODE, passcode);
        contentValues.put(DatabaseHelper.COLUMN_WALLPAPER, wallpaper);
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
                DatabaseHelper.COLUMN_PASSCODE,
                DatabaseHelper.COLUMN_WALLPAPER
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
                settings.setPasscode(cursor.getString(columnIndex++));
                settings.setWallpaper(cursor.getInt(columnIndex));
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

    public List<Notification> getNotifications() {
        List<Notification> notifications = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String[] columns = {
                DatabaseHelper.COLUMN_UID,
                DatabaseHelper.COLUMN_BODY,
                DatabaseHelper.COLUMN_DATE_ARRIVED,
                DatabaseHelper.COLUMN_ICON,
                DatabaseHelper.COLUMN_SOURCE,
                DatabaseHelper.COLUMN_TITLE,
        };
        Cursor cursor = null;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_NOTIFICATION,
                    columns,
                    null,
                    new String[0],
                    null,
                    null,
                    DatabaseHelper.COLUMN_DATE_ARRIVED + " desc"
            );

            while (cursor.moveToNext()) {
                int columnIndex = 0;
                Notification notification = new Notification();
                notification.setId(cursor.getLong(columnIndex++));
                notification.setBody(cursor.getString(columnIndex++));
                notification.setDateArrived(dateFormat.parse(cursor.getString(columnIndex++)));
                notification.setIcon(BitmapUtil.convertByteArrayToDrawable(context, cursor.getBlob(columnIndex++)));
                notification.setSource(cursor.getString(columnIndex++));
                notification.setTitle(cursor.getString(columnIndex));

                notifications.add(notification);
            }
        } catch (Exception e) {
            int a = 2;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return notifications;
    }

    public long saveNotification(Notification notification) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_BODY, notification.getBody());
        contentValues.put(DatabaseHelper.COLUMN_DATE_ARRIVED, dateFormat.format(notification.getDateArrived()));
        contentValues.put(DatabaseHelper.COLUMN_ICON, BitmapUtil.getAsByteArray(notification.getIcon()));
        contentValues.put(DatabaseHelper.COLUMN_SOURCE, notification.getSource());
        contentValues.put(DatabaseHelper.COLUMN_TITLE, notification.getTitle());

        return db.insert(DatabaseHelper.TABLE_NOTIFICATION, null, contentValues);
    }

    public boolean deleteNotification(Notification notification) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] args = {String.valueOf(notification.getId())};

        return db.delete(
                DatabaseHelper.TABLE_NOTIFICATION,
                DatabaseHelper.COLUMN_UID + " = ?",
                args) > 0;
    }

    public void createDb() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "LOCKERDB";
        private static final int DATABASE_VERSION = 1;

        // Table names
        private static final String TABLE_SETTINGS = "settings";
        private static final String TABLE_NOTIFICATION = "notification";

        // Column names
        private static final String COLUMN_BODY = "Body";
        private static final String COLUMN_DATE_ARRIVED = "DateArrived";
        private static final String COLUMN_HIDE_STATUS_BAR = "HideStatusBar";
        private static final String COLUMN_ICON = "Icon";
        private static final String COLUMN_LOCKER_ENABLED = "LockerEnabled";
        private static final String COLUMN_NOTIFICATIONS_ENABLED = "NotificationsEnabled";
        private static final String COLUMN_PASSCODE = "Passcode";
        private static final String COLUMN_SECURITY_ENABLED = "SecurityEnabled";
        private static final String COLUMN_SOURCE = "Source";
        private static final String COLUMN_TITLE = "Title";
        private static final String COLUMN_UID = "_id";
        private static final String COLUMN_WALLPAPER = "wallpaper";

        // Queries
        private static final String QUERY_CREATE_TABLE_SETTINGS = "CREATE TABLE " + TABLE_SETTINGS + " ("
                + COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LOCKER_ENABLED + " VARCHAR(1) NOT NULL, "
                + COLUMN_NOTIFICATIONS_ENABLED + " VARCHAR(1) NOT NULL, "
                + COLUMN_SECURITY_ENABLED + " VARCHAR(1) NOT NULL, "
                + COLUMN_HIDE_STATUS_BAR + " VARCHAR(1) NOT NULL, "
                + COLUMN_PASSCODE + " VARCHAR(16), "
                + COLUMN_WALLPAPER + " INTEGER)";

        private static final String QUERY_CREATE_TABLE_NOTIFICATION = "CREATE TABLE " + TABLE_NOTIFICATION + " ("
                + COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BODY + " VARCHAR(256) NOT NULL, "
                + COLUMN_DATE_ARRIVED + " DATETIME NOT NULL, "
                + COLUMN_ICON + " BLOB NOT NULL, "
                + COLUMN_SOURCE + " VARCHAR(32) NOT NULL, "
                + COLUMN_TITLE + " VARCHAR(256))";

        private Context context;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(QUERY_CREATE_TABLE_SETTINGS);
                db.execSQL(QUERY_CREATE_TABLE_NOTIFICATION);

                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_LOCKER_ENABLED, false);
                contentValues.put(COLUMN_NOTIFICATIONS_ENABLED, false);
                contentValues.put(COLUMN_SECURITY_ENABLED, false);
                contentValues.put(COLUMN_HIDE_STATUS_BAR, false);
                contentValues.putNull(COLUMN_PASSCODE);
                contentValues.putNull(COLUMN_WALLPAPER);
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
