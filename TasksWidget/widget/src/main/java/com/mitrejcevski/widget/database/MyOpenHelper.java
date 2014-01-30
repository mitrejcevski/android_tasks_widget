package com.mitrejcevski.widget.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database open helper class.
 *
 * @author jovche.mitrejchevski
 */
public class MyOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_database";
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructor.
     *
     * @param context
     */
    public MyOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called on creating of the database.
     *
     * @param database The SQLite database reference.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        ContentData.GroupsTable.onCreate(database);
        ContentData.TasksTable.onCreate(database);
    }

    /**
     * Called on database update.
     *
     * @param database   The SQLite database reference.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        ContentData.GroupsTable.onUpgrade(database, oldVersion, newVersion);
        ContentData.TasksTable.onUpgrade(database, oldVersion, newVersion);
    }
}
