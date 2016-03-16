package com.widget.ui.tasks

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.widget.R
import com.widget.database.DBManipulator
import com.widget.tools.toast
import com.widget.ui.groups.GroupsActivity

class TasksActivity : AppCompatActivity(), TasksContract.TasksView {

    private val tasksPresenter: TasksPresenter
    private val tasksAdapter: TasksAdapter

    init {
        tasksPresenter = TasksPresenter(this, DbTasksRepository(this))
        tasksAdapter = TasksAdapter { openTaskDetails(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        loadLayout()
    }

    private fun loadLayout() {
        initializeSwipeRefreshLayout()
        initializeRecycler()
        initializeNewTaskButton()
    }

    private fun initializeSwipeRefreshLayout() {
        val swipeLayout = findViewById(R.id.tasksSwipeContainer) as SwipeRefreshLayout
        swipeLayout.setColorSchemeResources(R.color.accent)
        swipeLayout.setOnRefreshListener { tasksPresenter.loadTasks(groupId()) }
    }

    private fun initializeRecycler() {
        val recyclerView = findViewById(R.id.tasksRecycler)as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = tasksAdapter
    }

    private fun initializeNewTaskButton() {
        val newTaskButton = findViewById(R.id.addNewTaskButton)
        newTaskButton.setOnClickListener { addNewTask() }
    }

    private fun addNewTask() {
        toast("New task")
    }

    override fun showLoading() {
        showRefreshing(true)
    }

    override fun hideLoading() {
        showRefreshing(false)
    }

    override fun applyItems(tasks: List<Task>) {
        toast(tasks.toString())
    }

    private fun showRefreshing(refreshing: Boolean) {
        val swipeLayout = findViewById(R.id.tasksSwipeContainer) as SwipeRefreshLayout
        swipeLayout.post { swipeLayout.isRefreshing = refreshing }
    }

    private fun openTaskDetails(task: Task) {
        toast("Opening: " + task)
    }

    fun groupId(): String = intent.getStringExtra(GroupsActivity.GroupActivityExtra.groupIdExtra)

    //TODO replace this temporal repository
    private class DbTasksRepository(val context: Context) : TasksRepository {

        override fun fetchTasksForId(groupId: String, callback: TasksRepository.TasksRepositoryCallback) {
            val items = DBManipulator.INSTANCE.getAllTasksForGroup(context, groupId)
            val converted = items.map { it -> Task(it.id, it.name, "Description") }
            callback.onTasksLoaded(converted)
        }
    }
}