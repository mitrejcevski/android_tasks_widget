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
import java.util.List;

public enum DBManipulator {

    INSTANCE;

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

    private int queryTask(final Context context, final MyTask task) {
        int id = ContentData.NO_ID;
        Cursor cursor = prepareTaskQuery(context, task.getId());
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex(ContentData._ID));
            cursor.close();
        }
        return id;
    }

    public ArrayList<MyTask> getAllTasks(Context context) {
        ArrayList<MyTask> tasks = new ArrayList<>();
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

    public ArrayList<MyTask> getAllTasksForGroup(Context context, String group) {
        ArrayList<MyTask> tasks = new ArrayList<>();
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

    public ArrayList<Group> getAllGroups(Context context) {
        ArrayList<Group> results = new ArrayList<>();
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

    public Group getGroupById(Context context, int id) {
        Group group = null;
        Cursor cursor = prepareGroupQuery(context, id);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                group = ConvertHelper.cursorToGroup(cursor);
            }
            cursor.close();
        }
        return group;
    }

    public void saveGroup(Context context, Group group) {
        Group oldGroup = queryGroupsTable(context, group);
        ContentValues values = new ContentValues();
        values.put(GroupsTable.GROUP_TITLE, group.getGroupTitle());
        if (group.getId() == ContentData.NO_ID) {
            context.getContentResolver().insert(GroupsTable.CONTENT_URI, values);
        } else {
            Uri recordUri = ContentUris.withAppendedId(GroupsTable.CONTENT_URI, group.getId());
            context.getContentResolver().update(recordUri, values, null, null);
            updateTasksTable(context, group, oldGroup);
        }
    }

    private Group queryGroupsTable(final Context context, final Group group) {
        Group oldGroup = new Group();
        Cursor cursor = prepareGroupQuery(context, group.getId());
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            oldGroup = ConvertHelper.cursorToGroup(cursor);
            cursor.close();
        } else
            oldGroup.setId(ContentData.NO_ID);
        return oldGroup;
    }

    private void updateTasksTable(Context context, Group newGroup, Group oldGroup) {
        Cursor cursor = prepareTaskQuery(context, oldGroup.getGroupTitle());
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

    public MyTask getTaskById(Context context, int id) {
        MyTask task = null;
        Cursor cursor = prepareTaskQuery(context, id);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                task = ConvertHelper.cursorToTask(cursor);
            }
            cursor.close();
        }
        return task;
    }

    public void deleteDoneTasks(Context context, String group) {
        String where = TasksTable.FINISHED + " = ? AND " + TasksTable.GROUP + " = ?";
        String[] args = new String[]{String.valueOf(ContentData.TRUE), group};
        context.getContentResolver().delete(TasksTable.CONTENT_URI, where, args);
    }

    public void deleteGroups(Context context, List<Group> groups) {
        String where = ContentData._ID + " = ?";
        for (Group group : groups) {
            String[] args = new String[]{String.valueOf(group.getId())};
            context.getContentResolver().delete(GroupsTable.CONTENT_URI, where, args);
            deleteTasksForGroup(context, group);
        }
    }

    public void deleteTasksForGroup(Context context, Group group) {
        String where = TasksTable.GROUP + " = ?";
        String[] args = new String[]{group.getGroupTitle()};
        context.getContentResolver().delete(TasksTable.CONTENT_URI, where, args);
    }

    public Cursor prepareTaskQuery(Context context, String group) {
        String selection = TasksTable.GROUP + " = ?";
        String[] args = new String[]{group};
        return context.getContentResolver().query(TasksTable.CONTENT_URI, null, selection, args, null);
    }

    private Cursor prepareTaskQuery(final Context context, final int taskId) {
        String selection = ContentData._ID + " = ?";
        String[] args = new String[]{String.valueOf(taskId)};
        return context.getContentResolver().query(TasksTable.CONTENT_URI, null, selection, args, null);
    }

    private Cursor prepareGroupQuery(final Context context, final int id) {
        String selection = ContentData._ID + " = ?";
        String[] args = new String[]{String.valueOf(id)};
        return context.getContentResolver().query(GroupsTable.CONTENT_URI, null, selection, args, null);
    }
}
