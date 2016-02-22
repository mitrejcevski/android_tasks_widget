package com.widget.database;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ContentData {

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.widget.tasksContentProvider";
    public static final int ID_PATH_POSITION = 1;
    public static final String _ID = "_id";
    public static final int NO_ID = 0;
    public static final int FALSE = 0;
    public static final int TRUE = 1;

    public static final class GroupsTable {
        public static final String TABLE_NAME = "Groups";
        public static final String PATH = "groups";
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + PATH);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.widget.group";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.widget.group";
        public static final String GROUP_TITLE = "group_title";
        private static final String CREATE_GROUPS_TABLE_SQL = "CREATE TABLE "
                + GroupsTable.TABLE_NAME + " (" + _ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GroupsTable.GROUP_TITLE + " TEXT NOT NULL " + ");";

        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(CREATE_GROUPS_TABLE_SQL);
        }

        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + GroupsTable.TABLE_NAME);
            onCreate(database);
        }
    }

    public static final class TasksTable {
        public static final String TABLE_NAME = "Tasks";
        public static final String PATH = "tasks";
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + PATH);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.widget.task";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.widget.task";
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

        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(CREATE_TASKS_TABLE_SQL);
        }

        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + TasksTable.TABLE_NAME);
            onCreate(database);
        }
    }
}
