package com.widget.activity;

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

import com.widget.R;
import com.widget.adapter.TasksListAdapter;
import com.widget.database.DatabaseManipulator;
import com.widget.model.MyTask;

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

	private void initialize() {
		mListView = (ListView) findViewById(R.id.tasks_list);
		mListView.setEmptyView(findViewById(R.id.empty));
		mTasksAdapter = new TasksListAdapter(this);
		mListView.setAdapter(mTasksAdapter);
		mAddNewTaskButton = (Button) findViewById(R.id.add_new_task_button);
		initializeListeners();
	}

	private void initializeListeners() {
		mAddNewTaskButton.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
	}

	private void update() {
		DatabaseManipulator.INSTANCE.open(this);
		mTasksAdapter.addTasks(DatabaseManipulator.INSTANCE.getAllTasks());
		DatabaseManipulator.INSTANCE.close();
	}

	private void openTaskAdder(MyTask model) {
		Intent intent = new Intent(this, NewItemActivity.class);
		intent.putExtra(NewItemActivity.MY_TASK_EXTRA, model);
		startActivity(intent);
	}

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