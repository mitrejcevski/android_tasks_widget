package com.mitrejcevski.widget.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mitrejcevski.widget.database.DatabaseTable.TaskTable;
import com.mitrejcevski.widget.model.MyTask;

/**
 * Manipulator for the data in the database.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public enum DatabaseManipulator {
	// singletone instance.
	INSTANCE;

	private SQLiteDatabase mDatabase;
	private DatabaseHelper mDatabaseHelper;

	/**
	 * Opens a connection to the database.
	 * 
	 * @param context
	 *            Context from an activity.
	 * @throws SQLException
	 *             If the database is already opened.
	 */
	public void open(Context context) throws SQLException {
		mDatabaseHelper = new DatabaseHelper(context);
		mDatabase = mDatabaseHelper.getWritableDatabase();
	}

	/**
	 * Closes the connection to the database.
	 */
	public void close() {
		mDatabaseHelper.close();
	}

	/**
	 * Creates new task in the database.
	 * 
	 * @param task
	 *            The task that should be written in the database.
	 * @return The id of the created item.
	 */
	public int createTask(MyTask task) {
		try {
			ContentValues values = new ContentValues();
			values.put(TaskTable.NAME, task.getName());
			values.put(TaskTable.FINISHED, task.isFinished() ? 1 : 0);
			values.put(TaskTable.HASTIME, task.hasTimeAttached() ? 1 : 0);
			if (task.hasTimeAttached())
				values.put(TaskTable.DATETIME, task.getDateTime()
						.getTimeInMillis());
			return (int) mDatabase.insert(TaskTable.TABLE_NAME, null, values);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Retrieves all the tasks in the database.
	 * 
	 * @return An array list of all the tasks in the database.
	 */
	public ArrayList<MyTask> getAllTasks() {
		ArrayList<MyTask> tasks = new ArrayList<MyTask>();
		Cursor cursor = mDatabase.query(TaskTable.TABLE_NAME, null, null, null,
				null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MyTask task = cursorToTask(cursor);
			tasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		return tasks;
	}

	/**
	 * Get task by id
	 * 
	 * @param id
	 *            the id of the task;
	 * @return MyTask object
	 */
	public MyTask getTaskById(int id) {
		MyTask task = null;
		Cursor cursor = mDatabase.query(TaskTable.TABLE_NAME, null,
				TaskTable._ID + "=" + id, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			task = cursorToTask(cursor);
			cursor.moveToNext();
		}
		cursor.close();
		return task;
	}

	/**
	 * Updates particular task.
	 * 
	 * @param task
	 *            The task that should be updated.
	 * @return The id of the updated task.
	 */
	public int updateTask(MyTask task) {
		try {
			ContentValues values = new ContentValues();
			values.put(TaskTable.NAME, task.getName());
			values.put(TaskTable.FINISHED, task.isFinished() ? 1 : 0);
			values.put(TaskTable.HASTIME, task.hasTimeAttached() ? 1 : 0);
			if (task.hasTimeAttached())
				values.put(TaskTable.DATETIME, task.getDateTime()
						.getTimeInMillis());
			return mDatabase.update(TaskTable.TABLE_NAME, values, TaskTable._ID
					+ "=" + task.getId(), null);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Deletes task from the database.
	 * 
	 * @param task
	 *            The task that should be deleted.
	 */
	public void deleteTask(MyTask task) {
		mDatabase.delete(TaskTable.TABLE_NAME,
				TaskTable._ID + "=" + task.getId(), null);
	}

	/**
	 * Deletes task from the database.
	 * 
	 * @param taskId
	 *            The id of the task that should be deleted.
	 */
	public void deleteTask(int taskId) {
		mDatabase.delete(TaskTable.TABLE_NAME, TaskTable._ID + "=" + taskId,
				null);
	}

	/**
	 * Deletes all the done tasks.
	 */
	public void deleteDoneTasks() {
		mDatabase.delete(TaskTable.TABLE_NAME, TaskTable.FINISHED + "=" + 1,
				null);
	}

	/**
	 * Created a task object from a record in the database.
	 * 
	 * @param cursor
	 *            The cursor to a particular record.
	 * @return Task object.
	 */
	private MyTask cursorToTask(Cursor cursor) {
		try {
			MyTask task = new MyTask();
			task.setId(cursor.getInt(0));
			task.setName(cursor.getString(1));
			task.setFinished(cursor.getInt(2) == 1 ? true : false);
			String datetime = cursor.getString(3);
			if (datetime != null && datetime != "")
				task.setDateTime(Long.parseLong(datetime));
			task.setHasTimeAttached(cursor.getInt(4) == 1 ? true : false);
			return task;
		} catch (Exception e) {
			return null;
		}
	}
}
