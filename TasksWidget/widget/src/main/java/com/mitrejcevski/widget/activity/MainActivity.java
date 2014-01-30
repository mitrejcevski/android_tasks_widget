package com.mitrejcevski.widget.activity;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.adapter.TasksListAdapter;
import com.mitrejcevski.widget.database.DBManipulator;
import com.mitrejcevski.widget.dialog.TasksDeletionDialog;
import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.model.MyTask;
import com.mitrejcevski.widget.provider.ListWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * The main activity of the application.
 *
 * @author jovche.mitrejchevski
 */
public class MainActivity extends Activity implements OnNavigationListener {

    private static final String TAG = "MainActivity";

    private ArrayAdapter<Group> mDropDownAdapter;
    private TasksListAdapter mTaskListAdapter;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }

    /**
     * Initializes the UI of this activity.
     */
    private void setupUI() {
        setupActionbar();
        ListView taskListView = (ListView) findViewById(R.id.task_list_view);
        taskListView.setEmptyView(findViewById(R.id.empty));
        mTaskListAdapter = new TasksListAdapter(this);
        taskListView.setAdapter(mTaskListAdapter);
        setEmptyClickListener();
    }

    /**
     * Sets a listener the empty view button for adding new tasks.
     */
    private void setEmptyClickListener() {
        Button emptyItem = (Button) findViewById(R.id.empty_add_new_button);
        emptyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewTaskActivity();
            }
        });
    }

    /**
     * Initializes the action bar.
     */
    private void setupActionbar() {
        mDropDownAdapter = new ArrayAdapter<Group>(this, android.R.layout.simple_list_item_1, getAllGroups());
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getActionBar().setListNavigationCallbacks(mDropDownAdapter, this);
        getActionBar().setSelectedNavigationItem(0);
    }

    /**
     * Retrieves all the groups.
     *
     * @return An {@link java.util.ArrayList} of {@link Group} objects.
     */
    private ArrayList<Group> getAllGroups() {
        ArrayList<Group> allGroups = DBManipulator.INSTANCE.getAllGroups(this);
        if (allGroups.isEmpty())
            allGroups.add(makeInitialGroup());
        return allGroups;
    }

    /**
     * Creates a new default group and saves it into the database.
     *
     * @return The newly created group.
     */
    private Group makeInitialGroup() {
        Group group = new Group();
        group.setGroupTitle(getString(R.string.defaultGroupName));
        DBManipulator.INSTANCE.saveGroup(this, group);
        return group;
    }

    /**
     * Called when creating the options menu.
     *
     * @param menu The menu where the resource menu is attached.
     * @return True always.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Called when the user clicks on an option menu.
     *
     * @param item The clicked menu item.
     * @return True if the clicked item is known. #super.onOptionsItemSelected() otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openApplicationSettings();
                return true;
            case R.id.action_delete_done_items:
                TasksDeletionDialog.newInstance().show(getFragmentManager(), TAG);
                return true;
            case R.id.action_add_task:
                openNewTaskActivity();
                return true;
            case R.id.action_group_editor:
                openGroupsEditor();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Opens the application settings screen.
     */
    private void openApplicationSettings() {
        // TODO Open Settings (After implementing them)
        Toast.makeText(this, R.string.actionSettings, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when an item from the action bar drop down is selected.
     *
     * @param itemPosition The position of the clicked item.
     * @param itemId       The id of the item
     * @return True always.
     */
    @Override
    public boolean onNavigationItemSelected(final int itemPosition, final long itemId) {
        List<MyTask> tasks = DBManipulator.INSTANCE.getAllTasksForGroup(this, getSelectedGroupTitle());
        mTaskListAdapter.setTasks(tasks);
        return true;
    }

    /**
     * Get the currently selected group on the actionbar drop down.
     *
     * @return The selected group.
     */
    private Group getSelectedGroup() {
        int selected = getActionBar().getSelectedNavigationIndex();
        if ((mDropDownAdapter.getCount() - 1) < selected) {
            selected = 0;
        }
        return mDropDownAdapter.getItem(selected);
    }

    /**
     * Get the title of the selected group.
     *
     * @return The selected group title.
     */
    private String getSelectedGroupTitle() {
        return getSelectedGroup().getGroupTitle();
    }

    /**
     * Deletes all the marked tasks inside the current group.
     */
    public void deleteDoneTasks() {
        DBManipulator.INSTANCE.deleteDoneTasks(this, getSelectedGroupTitle());
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
        mDropDownAdapter.clear();
        mDropDownAdapter.addAll(DBManipulator.INSTANCE.getAllGroups(this));
        mTaskListAdapter.setTasks(DBManipulator.INSTANCE.getAllTasksForGroup(this, getSelectedGroupTitle()));
    }

    /**
     * Looking for the task at a specific position in the current group, and
     * sends it for editing.
     *
     * @param task The task that has to be edited.
     */
    public void editTask(final MyTask task) {
        Group group = getSelectedGroup();
        openTaskEditor(task, group);
    }

    /**
     * Opens an activity for adding/editing tasks.
     *
     * @param model Task that has to be edited.
     * @param group Group where this task belongs.
     */
    private void openTaskEditor(final MyTask model, final Group group) {
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
}
