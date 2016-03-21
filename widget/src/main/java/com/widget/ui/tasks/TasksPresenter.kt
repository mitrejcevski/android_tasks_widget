package com.widget.ui.tasks

internal class TasksPresenter(private val view: TasksContract.TasksView,
                              private val repository: TasksRepository) :
        TasksContract.TasksUserAction, TasksRepository.TasksRepositoryCallback {

    override fun loadTasks(groupId: String) {
        view.showLoading()
        repository.fetchTasksForId(groupId, this)
    }

    override fun onTasksLoaded(tasks: List<Task>) {
        view.hideLoading()
        view.applyItems(tasks)
    }

    override fun saveTask(groupId: String, task: Task) {
        view.showLoading()
        repository.saveTask(groupId, task, this)
    }

    override fun onTaskSaved(groupId: String, task: Task) {
        loadTasks(groupId)
    }
}