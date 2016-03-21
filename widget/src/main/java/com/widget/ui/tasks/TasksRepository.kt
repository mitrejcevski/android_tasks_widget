package com.widget.ui.tasks

interface TasksRepository {

    interface TasksRepositoryCallback {
        fun onTasksLoaded(tasks: List<Task>)

        fun onTaskSaved(groupId: String, task: Task)
    }

    fun fetchTasksForId(groupId: String, callback: TasksRepositoryCallback)

    fun saveTask(groupId: String, task: Task, callback: TasksRepositoryCallback)
}