package com.mitrejcevski.widget.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

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
public enum DatabaseManipulator {
	// Singleton instance.
	INSTANCE;

	/**
	 * Determines if the task is already in the database and update it. If not
	 * it creates new one.
	 * 
	 * @param context
	 * @param task
	 */
	public void createUpdateTask(Context context, MyTask task) {
		// Check if record is already in the database
		String selection = ContentData._ID + " = ?";
		String[] args = new String[] { String.valueOf(task.getId()) };
		int id = ContentData.NO_ID;
		Cursor cursor = context.getContentResolver().query(
				TasksTable.CONTENT_URI, null, selection, args, null);
		// if there is a record like this, get its id.
		if (cursor != null && cursor.getCount() != 0) {
			cursor.moveToFirst();
			id = cursor.getInt(cursor.getColumnIndex(ContentData._ID));
			cursor.close();
		}
		Uri recordUri = null;
		ContentValues values = collectTask(task);
		if (id == ContentData.NO_ID) {
			// in this case create new record in the database.
			recordUri = context.getContentResolver().insert(
					TasksTable.CONTENT_URI, values);
		} else {
			// in this case just update the record.
			recordUri = ContentUris.withAppendedId(TasksTable.CONTENT_URI, id);
			context.getContentResolver().update(recordUri, values, null, null);
		}
	}

	/**
	 * Collects the data from the {@link MyTask} object into
	 * {@link ContentValues}.
	 * 
	 * @param task
	 * @return
	 */
	private ContentValues collectTask(MyTask task) {
		ContentValues values = new ContentValues();
		values.put(TasksTable.TASK_TITLE, task.getName());
		values.put(TasksTable.GROUP, task.getGroup());
		values.put(TasksTable.FINISHED, task.isFinished() ? ContentData.TRUE
				: ContentData.FALSE);
		values.put(TasksTable.HASTIME,
				task.hasTimeAttached() ? ContentData.TRUE : ContentData.FALSE);
		if (task.hasTimeAttached())
			values.put(TasksTable.DATETIME, task.getDateTime()
					.getTimeInMillis());
		return values;
	}

	/**
	 * Get all tasks from the database.
	 * 
	 * @param context
	 * @return An array list of {@link MyTask} objects.
	 */
	public ArrayList<MyTask> getAllTasks(Context context) {
		ArrayList<MyTask> tasks = new ArrayList<MyTask>();
		Cursor cursor = context.getContentResolver().query(
				TasksTable.CONTENT_URI, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				tasks.add(cursorToTask(cursor));
				cursor.moveToNext();
			}
			cursor.close();
		}
		return tasks;
	}

	/**
	 * Returns tasks for specific group.
	 * 
	 * @param context
	 * @param group
	 * @return
	 */
	public ArrayList<MyTask> getAllTasksForGroup(Context context, String group) {
		ArrayList<MyTask> tasks = new ArrayList<MyTask>();
		if (context == null || group == null) {
			return tasks;
		}
		String selection = TasksTable.GROUP + " = ?";
		String[] args = new String[] { group };
		Cursor cursor = context.getContentResolver().query(
				TasksTable.CONTENT_URI, null, selection, args, null);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				tasks.add(cursorToTask(cursor));
				cursor.moveToNext();
			}
			cursor.close();
		}
		return tasks;
	}

	/**
	 * Deletes a task from database.
	 * 
	 * @param context
	 * @param task
	 *            Task that needs to be deleted.
	 */
	public void deleteTask(Context context, MyTask task) {
		String where = ContentData._ID + " = ?";
		String[] args = new String[] { String.valueOf(task.getId()) };
		context.getContentResolver()
				.delete(TasksTable.CONTENT_URI, where, args);
	}

	/**
	 * Deletes a task from database.
	 * 
	 * @param context
	 * @param taskId
	 *            The id of the task that needs to be deleted.
	 */
	public void deleteTask(Context context, int taskId) {
		String where = ContentData._ID + " = ?";
		String[] args = new String[] { String.valueOf(taskId) };
		context.getContentResolver()
				.delete(TasksTable.CONTENT_URI, where, args);
	}

	/**
	 * Get all the available groups.
	 * 
	 * @param Context
	 * @return An array list of strings where are all the names of the tabs.
	 */
	public ArrayList<Group> getAllGroups(Context context) {
		ArrayList<Group> results = new ArrayList<Group>();
		Cursor cursor = context.getContentResolver().query(
				GroupsTable.CONTENT_URI, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				results.add(pointToGroup(cursor));
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
		String[] args = new String[] { String.valueOf(id) };
		Cursor cursor = context.getContentResolver().query(
				GroupsTable.CONTENT_URI, null, selection, args, null);
		// if there is a record like this, get it.
		if (cursor != null) {
			if (cursor.getCount() != 0) {
				cursor.moveToFirst();
				group = pointToGroup(cursor);
			}
			cursor.close();
		}
		return group;
	}

	/**
	 * If the group exists in the database - updates it. Otherwise it creates
	 * new one.
	 * 
	 * @param Context
	 * @param group
	 */
	public void saveGroup(Context context, Group group) {
		// Check if record is already in the database
		String selection = ContentData._ID + " = " + "?";
		String selectionArgs[] = new String[] { String.valueOf(group.getId()) };

		int id = ContentData.NO_ID;
		Group oldGroup = null;
		Cursor cursor = context.getContentResolver().query(
				GroupsTable.CONTENT_URI, null, selection, selectionArgs, null);
		// if there is a record like this, get its id.
		if (cursor != null && cursor.getCount() != 0) {
			cursor.moveToFirst();
			id = cursor.getInt(cursor.getColumnIndex(ContentData._ID));
			oldGroup = pointToGroup(cursor);
			cursor.close();
		}

		Uri recordUri = null;
		ContentValues values = new ContentValues();
		values.put(GroupsTable.GROUP_TITLE, group.getGroupTitle());
		if (id == ContentData.NO_ID) {
			// in this case create new record in the database.
			recordUri = context.getContentResolver().insert(
					GroupsTable.CONTENT_URI, values);
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
	private void updateTasksTable(Context context, Group newGroup,
			Group oldGroup) {
		String where = TasksTable.GROUP + " = ?";
		String[] args = new String[] { oldGroup.getGroupTitle() };
		Cursor cursor = context.getContentResolver().query(
				TasksTable.CONTENT_URI, null, where, args, null);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				MyTask task = cursorToTask(cursor);
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
		String[] args = new String[] { String.valueOf(id) };
		Cursor cursor = context.getContentResolver().query(
				TasksTable.CONTENT_URI, null, selection, args, null);
		if (cursor != null) {
			if (cursor.getCount() != 0) {
				cursor.moveToFirst();
				task = cursorToTask(cursor);
			}
			cursor.close();
		}
		return task;
	}

	/**
	 * Deletes all the done tasks.
	 * 
	 * @param id
	 */
	public void deleteDoneTasks(Context context) {
		String where = TasksTable.FINISHED + " = ?";
		String[] args = new String[] { String.valueOf(ContentData.TRUE) };
		context.getContentResolver()
				.delete(TasksTable.CONTENT_URI, where, args);
	}

	/**
	 * Deletes all done tasks for particular group.
	 * 
	 * @param context
	 * @param group
	 */
	public void deleteDoneTasks(Context context, String group) {
		String where = TasksTable.FINISHED + " = ? AND " + TasksTable.GROUP
				+ " = ?";
		String[] args = new String[] { String.valueOf(ContentData.TRUE), group };
		context.getContentResolver()
				.delete(TasksTable.CONTENT_URI, where, args);
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
			String[] args = new String[] { String.valueOf(group.getId()) };
			context.getContentResolver().delete(GroupsTable.CONTENT_URI, where,
					args);
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
		String[] args = new String[] { group.getGroupTitle() };
		context.getContentResolver()
				.delete(TasksTable.CONTENT_URI, where, args);
	}

	/**
	 * Creates group object from a record in the database.
	 * 
	 * @param cursor
	 * @return
	 */
	private Group pointToGroup(Cursor cursor) {
		try {
			Group group = new Group();
			group.setId(cursor.getInt(cursor.getColumnIndex(ContentData._ID)));
			group.setGroupTitle(cursor.getString(cursor
					.getColumnIndex(GroupsTable.GROUP_TITLE)));
			return group;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Creates task object from a record in the database.
	 * 
	 * @param cursor
	 *            The cursor to a particular record.
	 * @return Task object.
	 */
	private MyTask cursorToTask(Cursor cursor) {
		try {
			MyTask task = new MyTask();
			task.setId(cursor.getInt(cursor.getColumnIndex(ContentData._ID)));
			task.setName(cursor.getString(cursor
					.getColumnIndex(TasksTable.TASK_TITLE)));
			int finished = cursor.getInt(cursor
					.getColumnIndex(TasksTable.FINISHED));
			task.setFinished(finished == ContentData.TRUE ? true : false);
			String datetime = cursor.getString(cursor
					.getColumnIndex(TasksTable.DATETIME));
			if (datetime != null && datetime != "")
				task.setDateTime(Long.parseLong(datetime));
			int hasTimeAttached = cursor.getInt(cursor
					.getColumnIndex(TasksTable.HASTIME));
			task.setHasTimeAttached(hasTimeAttached == ContentData.TRUE ? true
					: false);
			task.setGroup(cursor.getString(cursor
					.getColumnIndex(TasksTable.GROUP)));
			return task;
		} catch (Exception e) {
			Log.e("DatabaseManipulator", "Cursor to task ", e);
			return null;
		}
	}
}
