package com.mitrejcevski.widget.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mitrejcevski.widget.database.ContentData.GroupsTable;
import com.mitrejcevski.widget.database.ContentData.TasksTable;
import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.model.MyTask;

import java.util.ArrayList;

/**
 * Manipulator for the data in the database.
 *
 * @author jovche.mitrejchevski
 */
public enum DBManipulator {
    /**
     * Singleton instance.
     */
    INSTANCE;

    /**
     * Determines if the task is already in the database and updates it. If not
     * it creates new one.
     *
     * @param context Context.
     * @param task    Task object.
     */
    public void createUpdateTask(Context context, MyTask task) {
        final int id = queryTask(context, task);
        ContentValues values = ConvertHelper.collectTask(task);
        if (id == ContentData.NO_ID)
            context.getContentResolver().insert(TasksTable.CONTENT_URI, values);
        else {
            Uri recordUri = ContentUris.withAppendedId(TasksTable.CONTENT_URI, id);
            context.getContentResolver().update(recordUri, values, null, null);
        }
    }

    /**
     * Queries the tasks tables and retrieves the task id.
     *
     * @param context Context.
     * @param task    Task.
     * @return The id of the provided task, or {@link ContentData.NO_ID}
     * if the task does not exists in the database.
     */
    private int queryTask(final Context context, final MyTask task) {
        int id = ContentData.NO_ID;
        Cursor cursor = prepareTaskQuery(context, task);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex(ContentData._ID));
            cursor.close();
        }
        return id;
    }

    /**
     * Prepares a query into the tasks table.
     *
     * @param context Context.
     * @param task    Task.
     * @return The cursor of the query.
     */
    private Cursor prepareTaskQuery(final Context context, final MyTask task) {
        String selection = ContentData._ID + " = ?";
        String[] args = new String[]{String.valueOf(task.getId())};
        return context.getContentResolver().query(TasksTable.CONTENT_URI, null, selection, args, null);
    }

    /**
     * Get all tasks from the database.
     *
     * @param context Context.
     * @return An array list of {@link MyTask} objects.
     */
    public ArrayList<MyTask> getAllTasks(Context context) {
        ArrayList<MyTask> tasks = new ArrayList<MyTask>();
        Cursor cursor = context.getContentResolver().query(TasksTable.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tasks.add(ConvertHelper.cursorToTask(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return tasks;
    }

    /**
     * Returns tasks for specific group.
     *
     * @param context Context.
     * @param group   Group.
     * @return ArrayList of tasks for a particular group.
     */
    public ArrayList<MyTask> getAllTasksForGroup(Context context, String group) {
        ArrayList<MyTask> tasks = new ArrayList<MyTask>();
        if (context != null && group != null) {
            Cursor cursor = prepareTaskQuery(context, group);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    tasks.add(ConvertHelper.cursorToTask(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
        return tasks;
    }

    /**
     * Prepares a query into the tasks table.
     *
     * @param context Context.
     * @param group   Group title.
     * @return Cursor to the first record form the result set.
     */
    public Cursor prepareTaskQuery(Context context, String group) {
        String selection = TasksTable.GROUP + " = ?";
        String[] args = new String[]{group};
        return context.getContentResolver().query(TasksTable.CONTENT_URI, null, selection, args, null);
    }

    /**
     * Deletes a task from database.
     *
     * @param context Context.
     * @param task    Task that needs to be deleted.
     */
    public void deleteTask(Context context, MyTask task) {
        String where = ContentData._ID + " = ?";
        String[] args = new String[]{String.valueOf(task.getId())};
        context.getContentResolver().delete(TasksTable.CONTENT_URI, where, args);
    }

    /**
     * Deletes a task from database.
     *
     * @param context Context.
     * @param taskId  The id of the task that needs to be deleted.
     */
    public void deleteTask(Context context, int taskId) {
        String where = ContentData._ID + " = ?";
        String[] args = new String[]{String.valueOf(taskId)};
        context.getContentResolver().delete(TasksTable.CONTENT_URI, where, args);
    }

    /**
     * Get all the available groups.
     *
     * @param context Context.
     * @return An array list of strings where are all the names of the tabs.
     */
    public ArrayList<Group> getAllGroups(Context context) {
        ArrayList<Group> results = new ArrayList<Group>();
        Cursor cursor = context.getContentResolver().query(GroupsTable.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                results.add(ConvertHelper.cursorToGroup(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return results;
    }

    /**
     * Get group by id.
     *
     * @param context
     * @param id
     * @return
     */
    public Group getGroupById(Context context, int id) {
        Group group = null;
        String selection = ContentData._ID + " = ?";
        String[] args = new String[]{String.valueOf(id)};
        Cursor cursor = context.getContentResolver().query(GroupsTable.CONTENT_URI, null, selection, args, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                group = ConvertHelper.cursorToGroup(cursor);
            }
            cursor.close();
        }
        return group;
    }

    /**
     * If the group exists in the database - updates it. Otherwise it creates
     * new one.
     *
     * @param context
     * @param group
     */
    public void saveGroup(Context context, Group group) {
        // Check if record is already in the database
        String selection = ContentData._ID + " = " + "?";
        String selectionArgs[] = new String[]{String.valueOf(group.getId())};

        int id = ContentData.NO_ID;
        Group oldGroup = null;
        Cursor cursor = context.getContentResolver().query(GroupsTable.CONTENT_URI, null, selection, selectionArgs, null);
        // if there is a record like this, get its id.
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex(ContentData._ID));
            oldGroup = ConvertHelper.cursorToGroup(cursor);
            cursor.close();
        }

        Uri recordUri = null;
        ContentValues values = new ContentValues();
        values.put(GroupsTable.GROUP_TITLE, group.getGroupTitle());
        if (id == ContentData.NO_ID) {
            // in this case create new record in the database.
            context.getContentResolver().insert(GroupsTable.CONTENT_URI, values);
        } else {
            // in this case just update the record.
            recordUri = ContentUris.withAppendedId(GroupsTable.CONTENT_URI, id);
            context.getContentResolver().update(recordUri, values, null, null);
            if (oldGroup != null)
                updateTasksTable(context, group, oldGroup);
        }
    }

    /**
     * This is called on group update, and it updates all the tasks from that
     * group with the new name.
     *
     * @param context
     * @param newGroup
     * @param oldGroup
     */
    private void updateTasksTable(Context context, Group newGroup, Group oldGroup) {
        String where = TasksTable.GROUP + " = ?";
        String[] args = new String[]{oldGroup.getGroupTitle()};
        Cursor cursor = context.getContentResolver().query(TasksTable.CONTENT_URI, null, where, args, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MyTask task = ConvertHelper.cursorToTask(cursor);
                task.setGroup(newGroup.getGroupTitle());
                createUpdateTask(context, task);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    /**
     * Get task by id.
     *
     * @param context
     * @param id
     * @return
     */
    public MyTask getTaskById(Context context, int id) {
        MyTask task = null;
        String selection = ContentData._ID + " = ?";
        String[] args = new String[]{String.valueOf(id)};
        Cursor cursor = context.getContentResolver().query(TasksTable.CONTENT_URI, null, selection, args, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                task = ConvertHelper.cursorToTask(cursor);
            }
            cursor.close();
        }
        return task;
    }

    /**
     * Deletes all the done tasks.
     */
    public void deleteDoneTasks(Context context) {
        String where = TasksTable.FINISHED + " = ?";
        String[] args = new String[]{String.valueOf(ContentData.TRUE)};
        context.getContentResolver().delete(TasksTable.CONTENT_URI, where, args);
    }

    /**
     * Deletes all done tasks for particular group.
     *
     * @param context
     * @param group
     */
    public void deleteDoneTasks(Context context, String group) {
        String where = TasksTable.FINISHED + " = ? AND " + TasksTable.GROUP + " = ?";
        String[] args = new String[]{String.valueOf(ContentData.TRUE), group};
        context.getContentResolver().delete(TasksTable.CONTENT_URI, where, args);
    }

    /**
     * Deletes an array list of groups.
     *
     * @param context
     * @param groups
     */
    public void deleteGroups(Context context, ArrayList<Group> groups) {
        String where = ContentData._ID + " = ?";
        for (Group group : groups) {
            String[] args = new String[]{String.valueOf(group.getId())};
            context.getContentResolver().delete(GroupsTable.CONTENT_URI, where, args);
            deleteTasksForGroup(context, group);
        }
    }

    /**
     * Deletes all the tasks for particular group.
     *
     * @param context
     * @param group
     */
    public void deleteTasksForGroup(Context context, Group group) {
        String where = TasksTable.GROUP + " = ?";
        String[] args = new String[]{group.getGroupTitle()};
        context.getContentResolver().delete(TasksTable.CONTENT_URI, where, args);
    }
}
