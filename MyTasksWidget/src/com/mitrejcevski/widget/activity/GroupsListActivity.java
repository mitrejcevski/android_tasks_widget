package com.mitrejcevski.widget.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.adapter.GroupsListAdapter;
import com.mitrejcevski.widget.database.DBManipulator;
import com.mitrejcevski.widget.dialog.GroupsDeletionDialog;
import com.mitrejcevski.widget.dialog.NewGroupDialog;
import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.provider.ListWidget;

/**
 * This is a list activity where the user can manage his groups.
 *
 * @author jovche.mitrejchevski
 */
public class GroupsListActivity extends Activity {

    private static final String TAG = "GroupsListActivity";
    private ListView mListView;
    private GroupsListAdapter mGroupsAdapter;

    /**
     * Called when activity is creating.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        initialize();
    }

    /**
     * Called when activity life-cycle is continuing.
     */
    @Override
    protected void onResume() {
        update();
        super.onResume();
    }

    /**
     * Initializes the UI.
     */
    private void initialize() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = (ListView) findViewById(R.id.groups_list);
        mListView.setEmptyView(findViewById(R.id.empty));
        mGroupsAdapter = new GroupsListAdapter(this);
        mListView.setAdapter(mGroupsAdapter);
        setEmptyClickListener();
    }

    /**
     * Sets a click listener to the empty view for the list.
     */
    private void setEmptyClickListener() {
        Button newGroup = (Button) findViewById(R.id.empty_new_group_button);
        newGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewGroupDialog.newInstance(-1).show(getFragmentManager(), TAG);
            }
        });
    }

    /**
     * Loads the tasks from the database, and populates the list.
     */
    public void update() {
        mGroupsAdapter.addGroups(DBManipulator.INSTANCE.getAllGroups(this));
    }

    /**
     * Inflates a menu from the menu folder in resources.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_management_menu, menu);
        return true;
    }

    /**
     * Called on click on a particular menu item.
     *
     * @param item The clicked menu item.
     * @return True if clicked some of the inflated menu items.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_add_group:
                NewGroupDialog.newInstance(-1).show(getFragmentManager(), TAG);
                return true;
            case R.id.menu_delete_selected_groups:
                GroupsDeletionDialog.newInstance().show(getFragmentManager(), TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Opens the group editing dialog, and it passes the current group for
     * editing.
     *
     * @param group The group that has to be edited.
     */
    public void openGroupEditor(final Group group) {
        NewGroupDialog.newInstance(group.getId()).show(getFragmentManager(), TAG);
    }

    /**
     * Sends a request to the database to delete all the selected groups.
     */
    public void deleteSelectedGroups() {
        DBManipulator.INSTANCE.deleteGroups(this, mGroupsAdapter.getMarkedGroups());
        update();
        notifyWidget();
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
