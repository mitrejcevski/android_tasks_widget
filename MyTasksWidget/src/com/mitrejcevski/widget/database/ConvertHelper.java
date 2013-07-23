package com.mitrejcevski.widget.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.model.MyTask;

/**
 * Helper class used for converting DB records into objects and vice - versa.
 *
 * @author jovche.mitrejchevski
 */
public class ConvertHelper {

    /**
     * Collects the data from the {@link com.mitrejcevski.widget.model.MyTask} object into
     * {@link android.content.ContentValues}.
     *
     * @param task The task object.
     * @return ContentValues object collecting data from the task object.
     */
    public static ContentValues collectTask(MyTask task) {
        ContentValues values = new ContentValues();
        values.put(ContentData.TasksTable.TASK_TITLE, task.getName());
        values.put(ContentData.TasksTable.GROUP, task.getGroup());
        values.put(ContentData.TasksTable.FINISHED, task.isFinished() ? ContentData.TRUE : ContentData.FALSE);
        values.put(ContentData.TasksTable.HAS_TIME, task.hasTimeAttached() ? ContentData.TRUE : ContentData.FALSE);
        if (task.hasTimeAttached())
            values.put(ContentData.TasksTable.DATETIME, task.getDateTime().getTimeInMillis());
        return values;
    }

    /**
     * Creates group object from a record in the database.
     *
     * @param cursor The cursor to a particular group record.
     * @return Group object.
     */
    public static Group cursorToGroup(Cursor cursor) {
        try {
            Group group = new Group();
            group.setId(cursor.getInt(cursor.getColumnIndex(ContentData._ID)));
            group.setGroupTitle(cursor.getString(cursor.getColumnIndex(ContentData.GroupsTable.GROUP_TITLE)));
            return group;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates task object from a record in the database.
     *
     * @param cursor The cursor to a particular task record.
     * @return Task object.
     */
    public static MyTask cursorToTask(Cursor cursor) {
        try {
            MyTask task = new MyTask();
            task.setId(cursor.getInt(cursor.getColumnIndex(ContentData._ID)));
            task.setName(cursor.getString(cursor.getColumnIndex(ContentData.TasksTable.TASK_TITLE)));
            int finished = cursor.getInt(cursor.getColumnIndex(ContentData.TasksTable.FINISHED));
            task.setFinished(finished == ContentData.TRUE ? true : false);
            String datetime = cursor.getString(cursor.getColumnIndex(ContentData.TasksTable.DATETIME));
            if (datetime != null && !datetime.equals(""))
                task.setDateTime(Long.parseLong(datetime));
            int hasTimeAttached = cursor.getInt(cursor.getColumnIndex(ContentData.TasksTable.HAS_TIME));
            task.setHasTimeAttached(hasTimeAttached == ContentData.TRUE ? true : false);
            task.setGroup(cursor.getString(cursor.getColumnIndex(ContentData.TasksTable.GROUP)));
            return task;
        } catch (Exception e) {
            Log.e("DB Manipulator", "Cursor to task ", e);
            return null;
        }
    }
}
