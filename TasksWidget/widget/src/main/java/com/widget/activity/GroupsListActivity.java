package com.widget.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.widget.R;
import com.widget.adapter.GroupsListAdapter;
import com.widget.database.DBManipulator;
import com.widget.dialog.GroupsDeletionDialog;
import com.widget.dialog.NewGroupDialog;
import com.widget.model.Group;
import com.widget.provider.ListWidget;

public class GroupsListActivity extends AppCompatActivity {

    private static final String TAG = "GroupsListActivity";
    private GroupsListAdapter mGroupsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        initialize();
    }

    @Override
    protected void onResume() {
        update();
        super.onResume();
    }

    private void initialize() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ListView listView = (ListView) findViewById(R.id.groups_list);
        listView.setEmptyView(findViewById(R.id.empty));
        mGroupsAdapter = new GroupsListAdapter(this);
        listView.setAdapter(mGroupsAdapter);
        setEmptyClickListener();
    }

    private void setEmptyClickListener() {
        Button newGroup = (Button) findViewById(R.id.empty_new_group_button);
        newGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewGroupDialog.newInstance(-1).show(getFragmentManager(), TAG);
            }
        });
    }

    public void update() {
        mGroupsAdapter.addGroups(DBManipulator.INSTANCE.getAllGroups(this));
    }

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
            case R.id.actionAddGroup:
                NewGroupDialog.newInstance(-1).show(getFragmentManager(), TAG);
                return true;
            case R.id.actionDeleteSelectedGroups:
                GroupsDeletionDialog.newInstance().show(getFragmentManager(), TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openGroupEditor(final Group group) {
        NewGroupDialog.newInstance(group.getId()).show(getFragmentManager(), TAG);
    }

    public void deleteSelectedGroups() {
        DBManipulator.INSTANCE.deleteGroups(this, mGroupsAdapter.getMarkedGroups());
        update();
        notifyWidget();
    }

    public void notifyWidget() {
        final Intent fillInIntent = new Intent(this, ListWidget.class);
        fillInIntent.setAction(ListWidget.UPDATE_ACTION);
        sendBroadcast(fillInIntent);
    }
}
