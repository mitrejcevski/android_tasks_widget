package com.mitrejcevski.widget.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.activity.MainActivity;
import com.mitrejcevski.widget.adapter.TasksListAdapter;
import com.mitrejcevski.widget.database.DatabaseManipulator;
import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.model.MyTask;

/**
 * Fragment that represents a section of the application (one page in the view
 * pager).
 * 
 * @author jovche.mitrejchevski
 */
public class TaskListFragment extends Fragment implements OnItemClickListener {

	private ListView mListView;
	private TasksListAdapter mTasksAdapter;
	private Group mGroup;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_layout, container, false);
		initializeViews(view);
		loadData();
		return view;
	}

	/**
	 * Initializes the views inside the inflated layout.
	 * 
	 * @param view
	 */
	private void initializeViews(View view) {
		mListView = (ListView) view.findViewById(R.id.tasks_list);
		registerForContextMenu(mListView);
		mListView.setOnItemClickListener(this);
		mListView.setEmptyView(view.findViewById(R.id.empty));
		mTasksAdapter = new TasksListAdapter(getActivity());
		mListView.setAdapter(mTasksAdapter);
	}

	/**
	 * Returns the adapter object.
	 * 
	 * @return
	 */
	public TasksListAdapter getListAdapter() {
		return mTasksAdapter;
	}

	/**
	 * Sets which group is currently active in view pager.
	 * 
	 * @param group
	 */
	public void setCurrentGroup(Group group) {
		mGroup = group;
	}

	/**
	 * Loads the data and populates the list.
	 */
	public void loadData() {
		// TODO in async loader to prevent the
		// UI blocking when the amount of data is LARGE.
		mTasksAdapter.addTasks(DatabaseManipulator.INSTANCE
				.getAllTasksForGroup(getActivity(), mGroup.getGroupTitle()));
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
		int position = info.position;
		switch (item.getItemId()) {
		case 0:
			recheckTask(mTasksAdapter.getItem(position));
			break;
		case 1:
			((MainActivity) getActivity()).editTask(position);
			break;
		}
		return true;
	}

	/**
	 * Marks a task as done.
	 * 
	 * @param currentTask
	 *            {@link MyTask} object that has to be marked as done.
	 */
	private void recheckTask(MyTask currentTask) {
		currentTask.setFinished(!currentTask.isFinished());
		mTasksAdapter.notifyDataSetChanged();
		DatabaseManipulator.INSTANCE.createUpdateTask(getActivity(),
				currentTask);
		((MainActivity) getActivity()).notifyWidget();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		recheckTask(mTasksAdapter.getItem(position));
	}
}
