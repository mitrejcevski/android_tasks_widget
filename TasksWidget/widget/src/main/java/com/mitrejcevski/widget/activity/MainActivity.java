package com.mitrejcevski.widget.activity;

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

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.database.DBManipulator;
import com.mitrejcevski.widget.dialog.TasksDeletionDialog;
import com.mitrejcevski.widget.fragment.MainFragment;
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
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerContent;
    private ActionBarDrawerToggle mDrawerToggle;
    private MainFragment mMainFragment;
    private ArrayAdapter<Group> mSideMenuAdapter;

    private int mSelectedGroupPosition = 0;
    private Group mSelectedGroup;
    private boolean mIsMultiPane;

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
     * Called when the activity's life cycle is resumed.
     */
    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }

    /**
     * Called when configuration is changed (i.e. orientation).
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!mIsMultiPane)
            mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Called when the activity creation process is done.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * Initializes the UI of this activity.
     */
    private void setupUI() {
        mIsMultiPane = getResources().getBoolean(R.bool.isTablet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(!mIsMultiPane);
        if (!mIsMultiPane)
            setupSideMenu();
        replaceFragment(loadMainFragment(null));
    }

    /**
     * Loads and initializes the side menu.
     */
    private void setupSideMenu() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerContent = (LinearLayout) findViewById(R.id.drawer_content);
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, 0, 0);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mSideMenuAdapter = new ArrayAdapter<Group>(this, R.layout.side_menu_item_layout);//, getAllGroups()
        drawerList.setAdapter(mSideMenuAdapter);
        drawerList.setOnItemClickListener(this);
    }

    /**
     * Called when the user clicks on an item of a list that has registered on item click listener
     * from this activity.
     *
     * @param parent   The parent adapter view.
     * @param view     The clicked view.
     * @param position The position of the clicked view in the adapter.
     * @param id       The id of the clicked view.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        prepareSelection(position);
        if (!mIsMultiPane)
            closeSideMenu();
    }

    /**
     * Checks if the newly selected group is different then the currently showing one,
     * and if true it opens the new one.
     *
     * @param position The position of the newly selected group inside the adapter.
     */
    private void prepareSelection(final int position) {
        if (position != mSelectedGroupPosition) {
            mSelectedGroupPosition = position;
            mSelectedGroup = mSideMenuAdapter.getItem(position);
            loadData();
        }
    }

    /**
     * Takes an action to replace the current fragment in the placeholder with
     * the one that is passed in the argument.
     *
     * @param fragment Fragment instance.
     */
    private void replaceFragment(final Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        closeSideMenu();
    }

    /**
     * Called after fragment replacement to close the side menu. This happens when the device is
     * handset (because on tablet there is no side menu).
     */
    private void closeSideMenu() {
        if (!mIsMultiPane)
            mDrawerLayout.closeDrawer(mDrawerContent);
    }

    /**
     * Obtain the {@link com.mitrejcevski.widget.fragment.MainFragment} instance.
     *
     * @param arguments Arguments bundle to be sent in the extras.
     * @return Fragment instance.
     */
    private Fragment loadMainFragment(final Bundle arguments) {
        if (mMainFragment == null) {
            mMainFragment = new MainFragment();
            mMainFragment.setArguments(arguments);
        }
        return mMainFragment;
    }

    /**
     * Loads the data that has to be shown from the database. At first it loads all the groups and
     * populates the side menu. Then it checks the selected group. If the group that was selected
     * is deleted, it resets to the first group. Then it loads the tasks for the selected group
     * and populates them on the screen.
     */
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

    /**
     * Retrieves all the groups.
     *
     * @return A list of Group objects.
     */
    private List<Group> getAllGroups() {
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

    /**
     * Toggles the side menu. It checks if the menu is opened and if true, it
     * closes it, or it opens it if it`s closed.
     */
    public void toggleSideMenu() {
        if (mDrawerLayout.isDrawerOpen(mDrawerContent))
            mDrawerLayout.closeDrawer(mDrawerContent);
        else
            mDrawerLayout.openDrawer(mDrawerContent);
    }

    /**
     * Opens the application settings screen.
     */
    private void openApplicationSettings() {
        // TODO Open Settings (After implementing them)
        Toast.makeText(this, R.string.actionSettings, Toast.LENGTH_SHORT).show();
    }

    /**
     * Deletes all the marked tasks inside the current group.
     */
    public void deleteDoneTasks() {
        DBManipulator.INSTANCE.deleteDoneTasks(this, mSideMenuAdapter.getItem(mSelectedGroupPosition).getGroupTitle());
        loadData();
        notifyWidget();
    }

    /**
     * Opens the activity for adding/editing tasks.
     */
    public void openNewTaskActivity() {
        Intent intent = new Intent(this, NewItemActivity.class);
        intent.putExtra(NewItemActivity.GROUP_EXTRA, mSelectedGroup.getGroupTitle());
        startActivity(intent);
    }

    /**
     * Opens the group management activity.
     */
    private void openGroupsEditor() {
        Intent intent = new Intent(this, GroupsListActivity.class);
        startActivity(intent);
    }

    /**
     * Looking for the task at a specific position in the current group, and
     * sends it for editing.
     *
     * @param task The task that has to be edited.
     */
    public void editTask(final MyTask task) {
        Group group = mSideMenuAdapter.getItem(mSelectedGroupPosition);
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
