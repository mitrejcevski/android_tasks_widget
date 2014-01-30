package com.mitrejcevski.widget.database;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Definition for the database and the stuff about it.
 *
 * @author jovche.mitrejchevski
 */
public class ContentData {

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.mitrejcevski.taskscontentprovider";
    public static final int ID_PATH_POSITION = 1;
    public static final String _ID = "_id";
    public static final int NO_ID = 0;
    public static final int FALSE = 0;
    public static final int TRUE = 1;

    /**
     * Groups table.
     *
     * @author jovche.mitrejchevski
     */
    public static final class GroupsTable {
        public static final String TABLE_NAME = "Groups";
        public static final String PATH = "groups";
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + PATH);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.jovche.group";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.jovche.group";
        public static final String GROUP_TITLE = "group_title";
        private static final String CREATE_GROUPS_TABLE_SQL = "CREATE TABLE "
                + GroupsTable.TABLE_NAME + " (" + _ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GroupsTable.GROUP_TITLE + " TEXT NOT NULL " + ");";

        /**
         * This is called on the creation of the database, and it creates the
         * {@link com.mitrejcevski.widget.database.ContentData.GroupsTable}
         *
         * @param database The database reference.
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(CREATE_GROUPS_TABLE_SQL);
        }

        /**
         * This is called on database upgrade.
         *
         * @param database   The database reference.
         * @param oldVersion The old version of the database.
         * @param newVersion The new version of the database.
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + GroupsTable.TABLE_NAME);
            onCreate(database);
        }
    }

    /**
     * Tasks table.
     *
     * @author jovche.mitrejchevski
     */
    public static final class TasksTable {
        public static final String TABLE_NAME = "Tasks";
        public static final String PATH = "tasks";
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + PATH);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.jovche.task";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.jovche.task";
        public static final String TASK_TITLE = "task_title";
        public static final String FINISHED = "finished";
        public static final String DATETIME = "datetime";
        public static final String HAS_TIME = "hastime";
        public static final String GROUP = "task_group";
        private static final String CREATE_TASKS_TABLE_SQL = "CREATE TABLE "
                + TasksTable.TABLE_NAME + "( " + _ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TasksTable.TASK_TITLE + " TEXT NOT NULL, "
                + TasksTable.FINISHED + " INTEGER, " + TasksTable.DATETIME
                + " TEXT, " + TasksTable.HAS_TIME + " INTEGER, "
                + TasksTable.GROUP + " TEXT NOT NULL " + ");";

        /**
         * This is called on the creation of the database, and it creates the
         * {@link com.mitrejcevski.widget.database.ContentData.TasksTable}
         *
         * @param database The database reference.
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(CREATE_TASKS_TABLE_SQL);
        }

        /**
         * * This is called on database upgrade.
         *
         * @param database   The database reference.
         * @param oldVersion The old version of the database.
         * @param newVersion The new version of the database.
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + TasksTable.TABLE_NAME);
            onCreate(database);
        }
    }
}
