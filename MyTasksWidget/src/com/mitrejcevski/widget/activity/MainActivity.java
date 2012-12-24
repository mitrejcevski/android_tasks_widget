package com.mitrejcevski.widget.activity;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.adapter.TasksListAdapter;
import com.mitrejcevski.widget.database.DatabaseManipulator;
import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.model.MyTask;
import com.mitrejcevski.widget.provider.ListWidget;

import java.util.ArrayList;

/**
 * The main activity of the application.
 * 
 * @author jovche.mitrejchevski
 */
public class MainActivity extends Activity implements OnNavigationListener,
		OnItemClickListener {

	private static final String SELECTED_GROUP = "selected_group";

	private ArrayAdapter<Group> mDropdownAdapter;
	private TasksListAdapter mTaskListAdapter;
	private ListView mTaskListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupUI();
		openPreviousGroup(savedInstanceState);
	}

	/**
	 * Initializes the UI of this activity.
	 */
	private void setupUI() {
		setupActionbar();
		mTaskListView = (ListView) findViewById(R.id.task_list_view);
		mTaskListAdapter = new TasksListAdapter(this);
		mTaskListView.setAdapter(mTaskListAdapter);
		mTaskListView.setOnItemClickListener(this);
		registerForContextMenu(mTaskListView);
	}

	/**
	 * Initializes the action bar.
	 * 
	 * @param savedInstanceState
	 *            The saved state.
	 */
	private void setupActionbar() {
		mDropdownAdapter = new ArrayAdapter<Group>(this,
				android.R.layout.simple_list_item_1, getAllGroups());
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setListNavigationCallbacks(mDropdownAdapter, this);
	}

	/**
	 * Open previously opened group, for example on orientation change or when
	 * closing another activity that is above this on the stack.
	 * 
	 * @param savedInstanceState
	 *            The {@link Bundle} with data.
	 */
	private void openPreviousGroup(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			int savedIndex = savedInstanceState.getInt(SELECTED_GROUP);
			getActionBar().setSelectedNavigationItem(savedIndex);
		}
	}

	/**
	 * Retrieves all the groups.
	 * 
	 * @return An {@link ArrayList} of {@link Group} objects.
	 */
	private ArrayList<Group> getAllGroups() {
		ArrayList<Group> allGroups = DatabaseManipulator.INSTANCE
				.getAllGroups(this);
		if (allGroups.isEmpty()) {
			Group group = new Group();
			group.setGroupTitle(getString(R.string.default_tab_name));
			DatabaseManipulator.INSTANCE.saveGroup(this, group);
			allGroups.add(group);
		}
		return allGroups;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Toast.makeText(this, R.string.menu_settings, Toast.LENGTH_SHORT)
					.show();
			return true;
		case R.id.menu_delete_done_items:
			askForDeleting();
			return true;
		case R.id.menu_add_group:
			openNewGroupActivity();
			return true;
		case R.id.menu_add_task:
			openNewTaskActivity();
			return true;
		case R.id.menu_group_editor:
			openGroupsEditor();
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
		menu.setHeaderTitle(mTaskListAdapter.getItem(info.position).getName());
		String[] menuItems = new String[] {
				getString(R.string.edit_task_label),
				getString(R.string.mark_completed_label) };
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
			editTask(info.position);
			break;
		case 1:
			recheckTask(mTaskListAdapter.getItem(info.position));
			break;
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(SELECTED_GROUP, getActionBar()
				.getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		mTaskListAdapter.setTasks(DatabaseManipulator.INSTANCE
				.getAllTasksForGroup(this, getSelectedGroup().getGroupTitle()));
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

	private Group getSelectedGroup() {
		int selected = getActionBar().getSelectedNavigationIndex();
		if ((mDropdownAdapter.getCount() - 1) < selected) {
			selected = 0;
		}
		return mDropdownAdapter.getItem(selected);
	}

	/**
	 * Deletes all the marked tasks inside the current group.
	 */
	private void deleteDoneTasks() {
		DatabaseManipulator.INSTANCE.deleteDoneTasks(this, getSelectedGroup()
				.getGroupTitle());
		refreshAdapters();
		notifyWidget();
	}

	/**
	 * Opens the group management activity.
	 */
	private void openGroupsEditor() {
		Intent intent = new Intent(this, GroupsListActivity.class);
		startActivity(intent);
	}

	/**
	 * Opens the activity for adding/editing tasks.
	 */
	private void openNewTaskActivity() {
		Intent intent = new Intent(this, NewItemActivity.class);
		Group group = getSelectedGroup();
		intent.putExtra(NewItemActivity.GROUP_EXTRA, group.getGroupTitle());
		startActivity(intent);
	}

	/**
	 * Opens activity for quick group adding.
	 */
	private void openNewGroupActivity() {
		Intent intent = new Intent(this, NewGroupActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		refreshAdapters();
		super.onResume();
	}

	/**
	 * Refreshes the adapters (The action bar drop down adapter and the tasks
	 * adapter).
	 */
	private void refreshAdapters() {
		mDropdownAdapter.clear();
		mDropdownAdapter
				.addAll(DatabaseManipulator.INSTANCE.getAllGroups(this));
		mTaskListAdapter.setTasks(DatabaseManipulator.INSTANCE
				.getAllTasksForGroup(this, getSelectedGroup().getGroupTitle()));
	}

	/**
	 * Looking for the task at a specific position in the current group, and
	 * sends it for editing.
	 * 
	 * @param position
	 */
	private void editTask(int position) {
		MyTask currentTask = mTaskListAdapter.getItem(position);
		Group group = getSelectedGroup();
		openTaskEditor(currentTask, group);
	}

	/**
	 * Rechecks a {@link MyTask} item as done/undone.
	 * 
	 * @param task
	 *            The {@link MyTask} object that has to be rechecked.
	 */
	private void recheckTask(MyTask task) {
		task.setFinished(!task.isFinished());
		DatabaseManipulator.INSTANCE.createUpdateTask(this, task);
		mTaskListAdapter.notifyDataSetChanged();
	}

	/**
	 * Opens an activity for adding/editing tasks.
	 * 
	 * @param model
	 *            Task that has to be edited.
	 * @param group
	 *            Group where this task belongs.
	 */
	private void openTaskEditor(MyTask model, Group group) {
		Intent intent = new Intent(this, NewItemActivity.class);
		intent.putExtra(NewItemActivity.MY_TASK_EXTRA, model.getId());
		intent.putExtra(NewItemActivity.GROUP_EXTRA, group.getGroupTitle());
		startActivity(intent);
	}

	/**
	 * Notifies the widget to reload the data.
	 */
	public void notifyWidget() {
		final Intent fillInIntent = new Intent(this, ListWidget.class);
		fillInIntent.setAction(ListWidget.UPDATE_ACTION);
		sendBroadcast(fillInIntent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		MyTask task = mTaskListAdapter.getItem(position);
		recheckTask(task);
	}
}
