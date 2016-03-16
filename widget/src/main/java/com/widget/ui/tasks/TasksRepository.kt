package com.widget.ui.tasks

interface TasksRepository {

    interface TasksRepositoryCallback {
        fun onTasksLoaded(tasks: List<Task>)
    }

    fun fetchTasksForId(groupId: String, callback: TasksRepositoryCallback)
}