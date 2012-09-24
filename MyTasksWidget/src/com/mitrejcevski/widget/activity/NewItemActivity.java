package com.mitrejcevski.widget.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.database.DatabaseManipulator;
import com.mitrejcevski.widget.model.MyTask;
import com.mitrejcevski.widget.provider.ListWidget;

public class NewItemActivity extends Activity {

	public static final String MY_TASK_EXTRA = "my_task";

	private EditText mTaskTitle;
	private MyTask mMyTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_item_layout);
		initialize();
	}

	private void initialize() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mMyTask = getIntent().getParcelableExtra(MY_TASK_EXTRA);
		mTaskTitle = (EditText) findViewById(R.id.task_title_edit_text);
		if (mMyTask != null)
			mTaskTitle.setText(mMyTask.getName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_task_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_menu_item:
			prepareItem();
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void prepareItem() {
		if (mTaskTitle.getText().toString().equals("")) {
			mTaskTitle.setError(getString(R.string.save_task_error));
			return;
		}
		saveItem();
	}

	private void saveItem() {
		DatabaseManipulator.INSTANCE.open(this);
		MyTask task;
		if (mMyTask == null) {
			task = new MyTask();
			task.setName(mTaskTitle.getText().toString());
			task.setId(DatabaseManipulator.INSTANCE.createTask(task));
		} else {
			task = mMyTask;
			mMyTask.setName(mTaskTitle.getText().toString());
			DatabaseManipulator.INSTANCE.updateTask(mMyTask);
		}
		DatabaseManipulator.INSTANCE.close();
		notifyWidget(task);
		finish();
	}

	private void notifyWidget(MyTask task) {
		final Intent fillInIntent = new Intent(this, ListWidget.class);
		fillInIntent.setAction(ListWidget.ADD_ACTION);
		final Bundle extras = new Bundle();
		extras.putInt(ListWidget.EXTRA_TASK_ID, task.getId());
		fillInIntent.putExtras(extras);
		sendBroadcast(fillInIntent);
	}
}