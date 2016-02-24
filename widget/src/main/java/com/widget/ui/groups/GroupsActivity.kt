package com.widget.ui.groups

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.widget.R
import com.widget.database.DBManipulator

class GroupsActivity : AppCompatActivity(), GroupsContract.GroupsView {

    private val groupsPresenter: GroupsPresenter
    private val recyclerAdapter: GroupsAdapter

    init {
        recyclerAdapter = GroupsAdapter()
        groupsPresenter = GroupsPresenter(this, DbGroupRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
        initLayout()
    }

    override fun onResume() {
        super.onResume()
        groupsPresenter.loadGroups()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionNewItem -> openGroupManagement()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openGroupManagement() {
        throw UnsupportedOperationException("not implemented")
    }

    private fun initLayout() {
        val swipeLayout = findViewById(R.id.groupsSwipeContainer) as SwipeRefreshLayout
        swipeLayout.setColorSchemeResources(R.color.accent)
        swipeLayout.setOnRefreshListener { groupsPresenter.loadGroups() }
        val recyclerView = findViewById(R.id.groupsRecycler) as RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = recyclerAdapter
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
    }
}
