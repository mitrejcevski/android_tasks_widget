package com.widget.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.widget.R;
import com.widget.database.DBManipulator;
import com.widget.dialog.TasksDeletionDialog;
import com.widget.fragment.MainFragment;
import com.widget.model.Group;
import com.widget.model.MyTask;
import com.widget.provider.ListWidget;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerContent;
    private ActionBarDrawerToggle mDrawerToggle;
    private MainFragment mMainFragment;
    private ArrayAdapter<Group> mSideMenuAdapter;

    private int mSelectedGroupPosition = 0;
    private Group mSelectedGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }

    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void setupUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setupSideMenu();
        replaceFragment(loadMainFragment(null));
    }

    private void setupSideMenu() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerContent = (LinearLayout) findViewById(R.id.drawer_content);
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_menu, 0, 0);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mSideMenuAdapter = new ArrayAdapter<>(this, R.layout.side_menu_item_layout);
        drawerList.setAdapter(mSideMenuAdapter);
        drawerList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        prepareSelection(position);
        closeSideMenu();
    }

    private void prepareSelection(final int position) {
        if (position != mSelectedGroupPosition) {
            mSelectedGroupPosition = position;
            mSelectedGroup = mSideMenuAdapter.getItem(position);
            loadData();
        }
    }

    private void replaceFragment(final Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        closeSideMenu();
    }

    private void closeSideMenu() {
        mDrawerLayout.closeDrawer(mDrawerContent);
    }

    private Fragment loadMainFragment(final Bundle arguments) {
        if (mMainFragment == null) {
            mMainFragment = new MainFragment();
            mMainFragment.setArguments(arguments);
        }
        return mMainFragment;
    }

    private void loadData() {
        List<Group> allGroups = getAllGroups();
        mSideMenuAdapter.clear();
        mSideMenuAdapter.addAll(allGroups);
        if (allGroups.size() <= mSelectedGroupPosition)
            mSelectedGroupPosition = 0;
        mSelectedGroup = mSideMenuAdapter.getItem(mSelectedGroupPosition);
        setTitle(mSelectedGroup.getGroupTitle());
        mMainFragment.applyData(DBManipulator.INSTANCE.getAllTasksForGroup(this, mSelectedGroup.getGroupTitle()));
    }

    private List<Group> getAllGroups() {
        ArrayList<Group> allGroups = DBManipulator.INSTANCE.getAllGroups(this);
        if (allGroups.isEmpty())
            allGroups.add(makeInitialGroup());
        return allGroups;
    }

    private Group makeInitialGroup() {
        Group group = new Group();
        group.setGroupTitle(getString(R.string.defaultGroupName));
        DBManipulator.INSTANCE.saveGroup(this, group);
        return group;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggleSideMenu();
                return true;
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

    public void toggleSideMenu() {
        if (mDrawerLayout.isDrawerOpen(mDrawerContent))
            mDrawerLayout.closeDrawer(mDrawerContent);
        else
            mDrawerLayout.openDrawer(mDrawerContent);
    }

    private void openApplicationSettings() {
        // TODO Open Settings (After implementing them)
        Toast.makeText(this, R.string.actionSettings, Toast.LENGTH_SHORT).show();
    }

    public void deleteDoneTasks() {
        DBManipulator.INSTANCE.deleteDoneTasks(this, mSideMenuAdapter.getItem(mSelectedGroupPosition).getGroupTitle());
        loadData();
        notifyWidget();
    }

    public void openNewTaskActivity() {
        Intent intent = new Intent(this, NewItemActivity.class);
        intent.putExtra(NewItemActivity.GROUP_EXTRA, mSelectedGroup.getGroupTitle());
        startActivity(intent);
    }

    private void openGroupsEditor() {
        Intent intent = new Intent(this, GroupsListActivity.class);
        startActivity(intent);
    }

    public void editTask(final MyTask task) {
        Group group = mSideMenuAdapter.getItem(mSelectedGroupPosition);
        openTaskEditor(task, group);
    }

    private void openTaskEditor(final MyTask model, final Group group) {
        Intent intent = new Intent(this, NewItemActivity.class);
        intent.putExtra(NewItemActivity.MY_TASK_EXTRA, model.getId());
        intent.putExtra(NewItemActivity.GROUP_EXTRA, group.getGroupTitle());
        startActivity(intent);
    }

    public void notifyWidget() {
        final Intent fillInIntent = new Intent(this, ListWidget.class);
        fillInIntent.setAction(ListWidget.UPDATE_ACTION);
        sendBroadcast(fillInIntent);
    }
}
