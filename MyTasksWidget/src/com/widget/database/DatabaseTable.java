package com.widget.database;

public interface DatabaseTable {

	public static final String CREATE_TASKS_TABLE = "create table "
			+ TaskTable.TABLE_NAME + "( " + TaskTable._ID
			+ " integer primary key autoincrement, " + TaskTable.NAME
			+ " text " + ");";

	public final class TaskTable {
		public static final String TABLE_NAME = "tasks";
		public static final String _ID = "_id";
		public static final String NAME = "name";
	}
}
