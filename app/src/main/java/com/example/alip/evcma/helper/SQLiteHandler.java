package com.example.alip.evcma.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Alip on 4/8/2017.
 */

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PICTURE = "picture";
    private static final String KEY_STATUS = "status";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_DATE = "created_date";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE =
                "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FIRST_NAME + " TEXT,"
                + KEY_LAST_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PICTURE + " TEXT,"
                + KEY_STATUS + " TEXT,"
                + KEY_UID + " TEXT,"
                + KEY_CREATED_DATE + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String first_name, String last_name, String email, String picture, String status, String uid, String created_date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME, first_name); // First Name
        values.put(KEY_LAST_NAME, last_name); // Last Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PICTURE, picture); // Picture
        values.put(KEY_STATUS, status); // Status
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_DATE, created_date); // Created Date

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     *  Searching user details in database
     **/
    public String searchUser() {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT * FROM user";
        String userID = null;

        Cursor row = db.rawQuery(sql, null);

        row.moveToFirst();
        if (row.getCount() > 0) {
            userID = row.getString(row.getColumnIndex("uid"));
        }

        return userID;
    }

    public String searchEmail() {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT * FROM user";
        String email = null;

        Cursor row = db.rawQuery(sql, null);

        row.moveToFirst();
        if (row.getCount() > 0) {
            email = row.getString(row.getColumnIndex("email"));
        }

        return email;
    }

    public String searchName() {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT * FROM user";
        String first_name = null, last_name = null;

        Cursor row = db.rawQuery(sql, null);

        row.moveToFirst();
        if (row.getCount() > 0) {
            first_name = row.getString(row.getColumnIndex("first_name"));
            last_name = row.getString(row.getColumnIndex("last_name"));
        }

        return first_name + " " + last_name;
    }

    public String searchPicture() {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT * FROM user";
        String picture = null;

        Cursor row = db.rawQuery(sql, null);

        row.moveToFirst();
        if (row.getCount() > 0) {
            picture = row.getString(row.getColumnIndex("picture"));
        }

        return picture;
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("first_name", cursor.getString(1));
            user.put("last_name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("status", cursor.getString(4));
            user.put("uid", cursor.getString(5));
            user.put("created_date", cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
