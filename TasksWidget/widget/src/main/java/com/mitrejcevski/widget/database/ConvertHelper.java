package com.mitrejcevski.widget.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.model.MyTask;

import static com.mitrejcevski.widget.database.ContentData.FALSE;
import static com.mitrejcevski.widget.database.ContentData.GroupsTable.GROUP_TITLE;
import static com.mitrejcevski.widget.database.ContentData.TRUE;
import static com.mitrejcevski.widget.database.ContentData.TasksTable.DATETIME;
import static com.mitrejcevski.widget.database.ContentData.TasksTable.FINISHED;
import static com.mitrejcevski.widget.database.ContentData.TasksTable.GROUP;
import static com.mitrejcevski.widget.database.ContentData.TasksTable.HAS_TIME;
import static com.mitrejcevski.widget.database.ContentData.TasksTable.TASK_TITLE;
import static com.mitrejcevski.widget.database.ContentData._ID;

public class ConvertHelper {

    private static final String TAG = "DB Manipulator";

    public static ContentValues collectTask(MyTask task) {
        ContentValues values = new ContentValues();
        values.put(TASK_TITLE, task.getName());
        values.put(GROUP, task.getGroup());
        values.put(FINISHED, task.isFinished() ? TRUE : FALSE);
        values.put(HAS_TIME, task.hasTimeAttached() ? TRUE : FALSE);
        if (task.hasTimeAttached())
            values.put(DATETIME, task.getDateTime().getTimeInMillis());
        return values;
    }

    public static Group cursorToGroup(Cursor cursor) {
        try {
            Group group = new Group();
            group.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
            group.setGroupTitle(cursor.getString(cursor.getColumnIndex(GROUP_TITLE)));
            return group;
        } catch (Exception e) {
            Log.e(TAG, "Cursor to group ", e);
            return new Group();
        }
    }

    public static MyTask cursorToTask(Cursor cursor) {
        try {
            MyTask task = new MyTask();
            task.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
            task.setName(cursor.getString(cursor.getColumnIndex(TASK_TITLE)));
            int finished = cursor.getInt(cursor.getColumnIndex(FINISHED));
            task.setFinished(finished == TRUE);
            String datetime = cursor.getString(cursor.getColumnIndex(DATETIME));
            if (datetime != null && !datetime.equals(""))
                task.setDateTime(Long.parseLong(datetime));
            int hasTimeAttached = cursor.getInt(cursor.getColumnIndex(HAS_TIME));
            task.setHasTimeAttached(hasTimeAttached == TRUE);
            task.setGroup(cursor.getString(cursor.getColumnIndex(GROUP)));
            return task;
        } catch (Exception e) {
            Log.e(TAG, "Cursor to task ", e);
            return new MyTask();
        }
    }
}
