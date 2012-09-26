package com.mitrejcevski.widget.database;

/**
 * This is where are all the definitions needed for the database.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public interface DatabaseTable {

	// Statement for creating the table in the database.
	public static final String CREATE_TASKS_TABLE = "create table "
			+ TaskTable.TABLE_NAME + "( " + TaskTable._ID
			+ " integer primary key autoincrement, " + TaskTable.NAME
			+ " text " + ");";

	/**
	 * Keeps all the constants for the database.
	 * 
	 * @author jovche.mitrejchevski
	 * 
	 */
	public final class TaskTable {
		public static final String TABLE_NAME = "tasks";
		public static final String _ID = "_id";
		public static final String NAME = "name";
	}
}
