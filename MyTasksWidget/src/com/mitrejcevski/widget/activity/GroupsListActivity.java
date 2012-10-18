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
import com.mitrejcevski.widget.adapter.GroupsListAdapter;
import com.mitrejcevski.widget.database.DatabaseManipulator;
import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.provider.ListWidget;

/**
 * This is a list activity where the user can manage his groups.
 * 
 * @author jovche.mitrejchevski
 */
public class GroupsListActivity extends Activity implements OnItemClickListener {

	private ListView mListView;
	private GroupsListAdapter mGroupsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_layout);
		initialize();
	}

	@Override
	protected void onResume() {
		update();
		super.onResume();
	}

	@Override
	protected void onPause() {
		setResult(RESULT_OK, getIntent());
		super.onPause();
	}

	/**
	 * Initializes the UI.
	 */
	private void initialize() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mListView = (ListView) findViewById(R.id.tasks_list);
		registerForContextMenu(mListView);
		mListView.setEmptyView(findViewById(R.id.empty));
		mGroupsAdapter = new GroupsListAdapter(this);
		mListView.setAdapter(mGroupsAdapter);
		mListView.setOnItemClickListener(this);
	}

	/**
	 * Loads the tasks from the database, and populates the list.
	 */
	private void update() {
		// TODO needs to be in a different thread because if the list is big,
		// this will block the UI.
		mGroupsAdapter.addGroups(DatabaseManipulator.INSTANCE
				.getAllGroups(this));
	}

	/**
	 * Inflates a menu from the menu folder in resources.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.group_management_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_add_group:
			startActivity(new Intent(this, NewGroupActivity.class));
			return true;
		case R.id.menu_delete_selected_groups:
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
		menu.setHeaderTitle(mGroupsAdapter.getItem(info.position)
				.getGroupTitle());
		String[] menuItems = new String[] {
				getString(R.string.group_mark_for_delete),
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
			openGroupEditor(mGroupsAdapter.getItem(info.position));
			break;
		}
		return true;
	}

	/**
	 * Opens the new group activity, and it passes the current group for
	 * editing.
	 * 
	 * @param group
	 */
	private void openGroupEditor(Group group) {
		Intent intent = new Intent(this, NewGroupActivity.class);
		intent.putExtra(NewGroupActivity.GROUP_ID_EXTRA, group.getId());
		startActivity(intent);
	}

	/**
	 * Opens a dialog for accepting the deletion.
	 */
	private void askForDeleting() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete_dialog_title);
		builder.setMessage(R.string.delete_groups_dialog_message);
		builder.setPositiveButton(R.string.accept_button_label,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteSelectedGroups();
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
	 * Sends a request to the database to delete all the selected groups.
	 */
	private void deleteSelectedGroups() {
		DatabaseManipulator.INSTANCE.deleteGroups(this,
				mGroupsAdapter.getMarkedGroups());
		update();
		notifyWidget();
	}

	/**
	 * Notifies the widget to reload the data.
	 * 
	 */
	private void notifyWidget() {
		final Intent fillInIntent = new Intent(this, ListWidget.class);
		fillInIntent.setAction(ListWidget.UPDATE_ACTION);
		sendBroadcast(fillInIntent);
	}

	/**
	 * It will mark the item on a specific position for deleting.
	 * 
	 * @param position
	 */
	private void recheckTask(int position) {
		Group current = mGroupsAdapter.getItem(position);
		current.setShoudlDelete(!current.shouldDelete());
		mGroupsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		recheckTask(position);
		notifyWidget();
	}
}
