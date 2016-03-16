package com.widget.ui.tasks

internal class TasksPresenter(val view: TasksContract.TasksView, val repository: TasksRepository) :
        TasksContract.TasksUserAction, TasksRepository.TasksRepositoryCallback {

    override fun loadTasks(groupId: String) {
        view.showLoading()
        repository.fetchTasksForId(groupId, this)
    }

    override fun onTasksLoaded(tasks: List<Task>) {
        view.hideLoading()
        view.applyItems(tasks)
    }

}