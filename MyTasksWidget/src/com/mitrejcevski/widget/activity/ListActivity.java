package com.mitrejcevski.widget.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.adapter.TasksListAdapter;
import com.mitrejcevski.widget.database.DatabaseManipulator;
import com.mitrejcevski.widget.model.MyTask;
import com.mitrejcevski.widget.provider.ListWidget;

/**
 * This is a list activity where the user can see his tasks and edit them.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class ListActivity extends Activity implements OnItemClickListener {

	private ListView mListView;
	private TasksListAdapter mTasksAdapter;

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
		registerForContextMenu(mListView);
		mListView.setEmptyView(findViewById(R.id.empty));
		mTasksAdapter = new TasksListAdapter(this);
		mListView.setAdapter(mTasksAdapter);
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
		case R.id.delete_done_menu_item:
			askForDeleting();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(mTasksAdapter.getItem(info.position).getName());
		String[] menuItems = new String[] {
				getString(R.string.mark_completed_label),
				getString(R.string.edit_label) };
		for (int i = 0; i < menuItems.length; i++) {
			menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case 0:
			recheckTask(info.position);
			break;
		case 1:
			openTaskAdder(mTasksAdapter.getItem(info.position));
			break;
		}
		return true;
	}

	/**
	 * Opens a dialog for accepting the deletion.
	 */
	private void askForDeleting() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete_dialog_title);
		builder.setMessage(R.string.delete_dialog_messaeg);
		builder.setPositiveButton(R.string.accept_button_label,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteDoneTasks();
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(R.string.reject_button_label,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/**
	 * Sends a request to the database to delete all the done tasks.
	 */
	private void deleteDoneTasks() {
		DatabaseManipulator.INSTANCE.open(this);
		DatabaseManipulator.INSTANCE.deleteDoneTasks();
		mTasksAdapter.addTasks(DatabaseManipulator.INSTANCE.getAllTasks());
		DatabaseManipulator.INSTANCE.close();
		notifyWidget(null);
	}

	/**
	 * Updates the task.
	 * 
	 * @param task
	 *            The task that shoudl be updated.
	 */
	private void updateTaskInDatabase(MyTask task) {
		DatabaseManipulator.INSTANCE.open(this);
		DatabaseManipulator.INSTANCE.updateTask(task);
		DatabaseManipulator.INSTANCE.close();
	}

	/**
	 * Notifies the widget to reload the data.
	 * 
	 * @param task
	 *            The task that was managed in this activity if any. Provide
	 *            null if you just want to update the widget.
	 */
	private void notifyWidget(MyTask task) {
		final Intent fillInIntent = new Intent(this, ListWidget.class);
		fillInIntent.setAction(ListWidget.UPDATE_ACTION);
		if (task != null) {
			final Bundle extras = new Bundle();
			extras.putInt(ListWidget.EXTRA_TASK_ID, task.getId());
			fillInIntent.putExtras(extras);
		}
		sendBroadcast(fillInIntent);
	}

	private void recheckTask(int position) {
		MyTask current = mTasksAdapter.getItem(position);
		current.setFinished(!current.isFinished());
		mTasksAdapter.notifyDataSetChanged();
		updateTaskInDatabase(current);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		recheckTask(position);
		notifyWidget(null);
	}
}