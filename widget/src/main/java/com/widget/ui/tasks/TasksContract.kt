package com.widget.ui.tasks

internal interface TasksContract {

    interface TasksView {
        fun showLoading()

        fun hideLoading()

        fun applyItems(tasks: List<Task>)
    }

    interface TasksUserAction {
        fun loadTasks(groupId: String)

        fun saveTask(groupId: String, task: Task)
    }
}