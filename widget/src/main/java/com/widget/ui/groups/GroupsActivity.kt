package com.widget.ui.groups

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.widget.R
import com.widget.database.DBManipulator
import com.widget.tools.toast

class GroupsActivity : AppCompatActivity(), GroupsContract.GroupsView,
        NewGroupDialog.OnDoneCallback {

    private val groupsPresenter: GroupsPresenter
    private val recyclerAdapter: GroupsAdapter

    init {
        groupsPresenter = GroupsPresenter(this, DbGroupRepository(this))
        recyclerAdapter = GroupsAdapter() { openGroup(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
        initLayout()
    }

    private fun initLayout() {
        initializeSwipeRefreshLayout()
        initializeRecycler()
        initializeNewGroupButton()
    }

    private fun initializeSwipeRefreshLayout() {
        val swipeLayout = findViewById(R.id.groupsSwipeContainer) as SwipeRefreshLayout
        swipeLayout.setColorSchemeResources(R.color.accent)
        swipeLayout.setOnRefreshListener { groupsPresenter.loadGroups() }
    }

    private fun initializeRecycler() {
        val recyclerView = findViewById(R.id.groupsRecycler) as RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = recyclerAdapter
    }

    private fun initializeNewGroupButton() {
        val newGroupButton = findViewById(R.id.addNewGroupButton)
        newGroupButton.setOnClickListener { addNewGroup() }
    }

    override fun onResume() {
        super.onResume()
        groupsPresenter.loadGroups()
    }

    override fun onGroupReady(title: String) {
        makeNewGroup(title)
    }

    override fun onGroupError() {
        toast(R.string.labelInvalidGroup)
    }

    private fun addNewGroup() {
        NewGroupDialog().withDoneCallback(this).show(supportFragmentManager, "");
    }

    private fun makeNewGroup(title: String) {
        groupsPresenter.makeNewGroup(title);
    }

    private fun openGroup(group: Group) {
        toast(group.title)
    }

    override fun showLoading() {
        showRefreshing(true)
    }

    override fun hideLoading() {
        showRefreshing(false)
    }

    override fun setGroups(groups: List<Group>) {
        recyclerAdapter.items = groups
    }

    override fun showToast(resource: Int) {
        toast(resource)
    }

    override fun showSnackBar(resource: Int) {
        Snackbar.make(findViewById(R.id.groupsCoordinator), resource, Snackbar.LENGTH_SHORT).show()
    }

    private fun showRefreshing(refreshing: Boolean) {
        val swipeLayout = findViewById(R.id.groupsSwipeContainer) as SwipeRefreshLayout
        swipeLayout.isRefreshing = refreshing
    }

    //TODO replace this temporal repository once DB accessing is changed
    private class DbGroupRepository(val context: Context) : GroupsRepository {

        override fun fetchGroups(callback: GroupsPresenter) {
            val items = DBManipulator.INSTANCE.getAllGroups(context)
            val converted = items.map { it -> Group(it.id, it.groupTitle) }
            callback.onGroupsLoaded(converted)
        }

        override fun saveGroup(groupTitle: String, callback: GroupsPresenter) {
            val group = com.widget.model.Group()
            group.groupTitle = groupTitle
            DBManipulator.INSTANCE.saveGroup(context, group);
            callback.onGroupSaved()
        }
    }
}
