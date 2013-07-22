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

import com.mitrejcevski.widget.database.ContentData.GroupsTable;
import com.mitrejcevski.widget.database.ContentData.TasksTable;

import java.util.ArrayList;

/**
 * The content provider class used for communication with the database.
 *
 * @author jovche.mitrejchevski
 */
public class TasksContentProvider extends ContentProvider {

    private static final int CODE_GROUPS = 1;
    private static final int CODE_GROUP_ID = 2;
    private static final int CODE_TASKS = 3;
    private static final int CODE_TASK_ID = 4;

    /* Exceptions */
    private static final String INSERT_FAILED = "Insert failed";
    private static final String UNKNOWN_URI = "Unknown URI ";

    private MyOpenHelper mDatabaseHelper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * This is executed very first (before anything else).
     */
    static {
        sUriMatcher.addURI(ContentData.AUTHORITY, GroupsTable.PATH, CODE_GROUPS);
        sUriMatcher.addURI(ContentData.AUTHORITY, GroupsTable.PATH + "/#", CODE_GROUP_ID);
        sUriMatcher.addURI(ContentData.AUTHORITY, TasksTable.PATH, CODE_TASKS);
        sUriMatcher.addURI(ContentData.AUTHORITY, TasksTable.PATH + "/#", CODE_TASK_ID);
    }

	/* C R U D */

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case CODE_GROUPS:
                count = database.delete(GroupsTable.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_GROUP_ID:
                count = database.delete(GroupsTable.TABLE_NAME, ContentData._ID + " = ?", new String[]{uri.getPathSegments().get(ContentData.ID_PATH_POSITION)});
                break;
            case CODE_TASKS:
                count = database.delete(TasksTable.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_TASK_ID:
                count = database.delete(TasksTable.TABLE_NAME, ContentData._ID + " = ?", new String[]{uri.getPathSegments().get(ContentData.ID_PATH_POSITION)});
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
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

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database;
        long rowId;
        switch (sUriMatcher.match(uri)) {
            case CODE_GROUPS:
                database = mDatabaseHelper.getWritableDatabase();
                rowId = database.insert(GroupsTable.TABLE_NAME, null, values);
                if (rowId > 0) {
                    Uri userUri = ContentUris.withAppendedId(GroupsTable.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(userUri, null);
                    return userUri;
                }
                throw new SQLiteException(INSERT_FAILED);
            case CODE_TASKS:
                database = mDatabaseHelper.getWritableDatabase();
                rowId = database.insert(TasksTable.TABLE_NAME, null, values);
                if (rowId > 0) {
                    Uri userUri = ContentUris.withAppendedId(TasksTable.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(userUri, null);
                    return userUri;
                }
                throw new SQLiteException(INSERT_FAILED);
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new MyOpenHelper(getContext());
        // We assume that any failures will be reported by a thrown exception.
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case CODE_GROUPS:
                queryBuilder.setTables(GroupsTable.TABLE_NAME);
                break;
            case CODE_GROUP_ID:
                queryBuilder.setTables(GroupsTable.TABLE_NAME);
                queryBuilder.appendWhere(ContentData._ID + "="
                        + uri.getPathSegments().get(ContentData.ID_PATH_POSITION));
                break;
            case CODE_TASKS:
                queryBuilder.setTables(TasksTable.TABLE_NAME);
                break;
            case CODE_TASK_ID:
                queryBuilder.setTables(TasksTable.TABLE_NAME);
                queryBuilder.appendWhere(ContentData._ID + "="
                        + uri.getPathSegments().get(ContentData.ID_PATH_POSITION));
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(database, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case CODE_GROUPS:
                count = database.update(GroupsTable.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CODE_GROUP_ID:
                count = database.update(GroupsTable.TABLE_NAME, values,
                        ContentData._ID + " = ?", new String[]{uri
                        .getPathSegments()
                        .get(ContentData.ID_PATH_POSITION)});
                break;
            case CODE_TASKS:
                count = database.update(TasksTable.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CODE_TASK_ID:
                count = database.update(TasksTable.TABLE_NAME, values,
                        ContentData._ID + " = ?", new String[]{uri
                        .getPathSegments()
                        .get(ContentData.ID_PATH_POSITION)});
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }

        return count;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
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