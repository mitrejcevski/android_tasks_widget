package com.mitrejcevski.widget.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.adapter.TasksListAdapter;
import com.mitrejcevski.widget.database.DatabaseManipulator;
import com.mitrejcevski.widget.model.MyTask;

/**
 * This is a list activity where the user can see his tasks and edit them.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class ListActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private ListView mListView;
	private TasksListAdapter mTasksAdapter;
	private Button mAddNewTaskButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasks_list_layout);
		initialize();
	}

	@Override
	protected void onResume() {
		update();
		super.onResume();
	}

	/**
	 * Initializes the UI.
	 */
	private void initialize() {
		mListView = (ListView) findViewById(R.id.tasks_list);
		mListView.setEmptyView(findViewById(R.id.empty));
		mTasksAdapter = new TasksListAdapter(this);
		mListView.setAdapter(mTasksAdapter);
		mAddNewTaskButton = (Button) findViewById(R.id.add_new_task_button);
		initializeListeners();
	}

	/**
	 * Initializes the listeners to the actions of the UI.
	 */
	private void initializeListeners() {
		mAddNewTaskButton.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
	}

	/**
	 * Loads the tasks from the database, and populates the list.
	 */
	private void update() {
		// TODO needs to be in a different thread because if the list is big,
		// this will block the UI.
		DatabaseManipulator.INSTANCE.open(this);
		mTasksAdapter.addTasks(DatabaseManipulator.INSTANCE.getAllTasks());
		DatabaseManipulator.INSTANCE.close();
	}

	/**
	 * Opens an activity for adding/editing tasks.
	 * 
	 * @param model
	 *            The tasks that needs to be edited, or null for adding new
	 *            task.
	 */
	private void openTaskAdder(MyTask model) {
		Intent intent = new Intent(this, NewItemActivity.class);
		intent.putExtra(NewItemActivity.MY_TASK_EXTRA, model);
		startActivity(intent);
	}

	/**
	 * Inflates a menu from the menu folder in resources.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_new_menu_item:
			openTaskAdder(null);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		openTaskAdder(null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		openTaskAdder(mTasksAdapter.getItem(position));
	}
}