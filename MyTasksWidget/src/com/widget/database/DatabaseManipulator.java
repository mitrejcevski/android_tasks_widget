package com.widget.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.widget.database.DatabaseTable.TaskTable;
import com.widget.model.MyTask;

public enum DatabaseManipulator {

	INSTANCE;

	private SQLiteDatabase mDatabase;
	private DatabaseHelper mDatabaseHelper;

	public void open(Context context) throws SQLException {
		mDatabaseHelper = new DatabaseHelper(context);
		mDatabase = mDatabaseHelper.getWritableDatabase();
	}

	public void close() {
		mDatabaseHelper.close();
	}

	public int createTask(MyTask task) {
		try {
			ContentValues values = new ContentValues();
			values.put(TaskTable.NAME, task.getName());
			return (int) mDatabase.insert(TaskTable.TABLE_NAME, null, values);
		} catch (Exception e) {
			return -1;
		}
	}

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
		return tasks;
	}

	public int updateTask(MyTask task) {
		try {
			ContentValues values = new ContentValues();
			values.put(TaskTable.NAME, task.getName());
			return mDatabase.update(TaskTable.TABLE_NAME, values, TaskTable._ID
					+ "=" + task.getId(), null);
		} catch (Exception e) {
			return -1;
		}
	}

	public void deleteTask(MyTask task) {
		mDatabase.delete(TaskTable.TABLE_NAME,
				TaskTable._ID + "=" + task.getId(), null);
	}

	public void deleteTask(int taskId) {
		mDatabase.delete(TaskTable.TABLE_NAME, TaskTable._ID + "=" + taskId,
				null);
	}

	private MyTask cursorToTask(Cursor cursor) {
		try {
			MyTask task = new MyTask();
			task.setId(cursor.getInt(0));
			task.setName(cursor.getString(1));
			return task;
		} catch (Exception e) {
			return null;
		}
	}
}
