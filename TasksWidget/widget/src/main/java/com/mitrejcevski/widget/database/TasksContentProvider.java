package com.mitrejcevski.widget.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.mitrejcevski.widget.database.ContentData.GroupsTable;
import com.mitrejcevski.widget.database.ContentData.TasksTable;

import java.util.ArrayList;

public class TasksContentProvider extends ContentProvider {

    private static final int CODE_GROUPS = 1;
    private static final int CODE_GROUP_ID = 2;
    private static final int CODE_TASKS = 3;
    private static final int CODE_TASK_ID = 4;
    private static final String INSERT_FAILED = "Insert failed";
    private static final String UNKNOWN_URI = "Unknown URI ";
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ContentData.AUTHORITY, GroupsTable.PATH, CODE_GROUPS);
        sUriMatcher.addURI(ContentData.AUTHORITY, GroupsTable.PATH + "/#", CODE_GROUP_ID);
        sUriMatcher.addURI(ContentData.AUTHORITY, TasksTable.PATH, CODE_TASKS);
        sUriMatcher.addURI(ContentData.AUTHORITY, TasksTable.PATH + "/#", CODE_TASK_ID);
    }

    private MyOpenHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new MyOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        setupTables(uri, queryBuilder);
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    private void setupTables(final Uri uri, final SQLiteQueryBuilder queryBuilder) {
        switch (sUriMatcher.match(uri)) {
            case CODE_GROUPS:
                queryBuilder.setTables(GroupsTable.TABLE_NAME);
                break;
            case CODE_GROUP_ID:
                queryBuilder.setTables(GroupsTable.TABLE_NAME);
                queryBuilder.appendWhere(ContentData._ID + "=" + uri.getPathSegments().get(ContentData.ID_PATH_POSITION));
                break;
            case CODE_TASKS:
                queryBuilder.setTables(TasksTable.TABLE_NAME);
                break;
            case CODE_TASK_ID:
                queryBuilder.setTables(TasksTable.TABLE_NAME);
                queryBuilder.appendWhere(ContentData._ID + "=" + uri.getPathSegments().get(ContentData.ID_PATH_POSITION));
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case CODE_GROUPS:
                return doInsert(GroupsTable.TABLE_NAME, GroupsTable.CONTENT_URI, values);
            case CODE_TASKS:
                return doInsert(TasksTable.TABLE_NAME, TasksTable.CONTENT_URI, values);
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }
    }

    private Uri doInsert(String tableName, Uri contentUri, ContentValues values) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        long rowId = database.insert(tableName, null, values);
        if (rowId > 0) {
            Uri insertUri = ContentUris.withAppendedId(contentUri, rowId);
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(insertUri, null);
            }
            return insertUri;
        }
        throw new SQLiteException(INSERT_FAILED);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case CODE_GROUPS:
                return doUpdate(GroupsTable.TABLE_NAME, values, selection, selectionArgs);
            case CODE_GROUP_ID:
                final String groupIdSelection = ContentData._ID + " = ?";
                final String[] groupIdArgs = {uri.getPathSegments().get(ContentData.ID_PATH_POSITION)};
                return doUpdate(GroupsTable.TABLE_NAME, values, groupIdSelection, groupIdArgs);
            case CODE_TASKS:
                return doUpdate(TasksTable.TABLE_NAME, values, selection, selectionArgs);
            case CODE_TASK_ID:
                final String taskIdSelection = ContentData._ID + " = ?";
                final String[] taskIdSelectionArgs = {uri.getPathSegments().get(ContentData.ID_PATH_POSITION)};
                return doUpdate(TasksTable.TABLE_NAME, values, taskIdSelection, taskIdSelectionArgs);
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }
    }

    private int doUpdate(String tableName, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        return database.update(tableName, values, selection, selectionArgs);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case CODE_GROUPS:
                return doDelete(GroupsTable.TABLE_NAME, selection, selectionArgs);
            case CODE_GROUP_ID:
                final String groupIdSelection = ContentData._ID + " = ?";
                final String[] groupIdSelectionArgs = {uri.getPathSegments().get(ContentData.ID_PATH_POSITION)};
                return doDelete(GroupsTable.TABLE_NAME, groupIdSelection, groupIdSelectionArgs);
            case CODE_TASKS:
                return doDelete(TasksTable.TABLE_NAME, selection, selectionArgs);
            case CODE_TASK_ID:
                final String taskIdSelection = ContentData._ID + " = ?";
                final String[] taskIdSelectionArgs = {uri.getPathSegments().get(ContentData.ID_PATH_POSITION)};
                return doDelete(TasksTable.TABLE_NAME, taskIdSelection, taskIdSelectionArgs);
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }
    }

    private int doDelete(String tableName, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        return database.delete(tableName, selection, selectionArgs);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CODE_GROUPS:
                return GroupsTable.CONTENT_TYPE;
            case CODE_GROUP_ID:
                return GroupsTable.CONTENT_ITEM_TYPE;
            case CODE_TASKS:
                return TasksTable.CONTENT_TYPE;
            case CODE_TASK_ID:
                return TasksTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        ContentProviderResult[] result;
        database.beginTransaction();
        try {
            result = super.applyBatch(operations);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        return result;
    }
}