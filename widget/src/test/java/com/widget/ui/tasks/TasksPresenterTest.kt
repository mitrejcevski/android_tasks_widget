package com.widget.ui.tasks

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class TasksPresenterTest {

    private val tasksView = mock(TasksContract.TasksView::class.java)
    private val tasksRepository = mock(TasksRepository::class.java)
    private var presenter: TasksPresenter? = null

    @Before
    fun setUp() {
        presenter = TasksPresenter(tasksView, tasksRepository);
    }

    @Test
    fun presenterShouldTriggerViewLoadingDisplayBeforeFetchingData() {
        presenter?.loadTasks("")
        verify(tasksView).showLoading()
    }

    @Test
    fun presenterShouldHideLoadingOnViewWhenTasksLoaded() {
        presenter?.onTasksLoaded(mockTasks(10))
        verify(tasksView).hideLoading()
    }

    @Test
    fun presenterShouldApplyResultIntoView() {
        presenter?.loadTasks("groupId")
        verify(tasksRepository).fetchTasksForId("groupId", presenter!!)
    }

    @Test
    fun presenterShouldTriggerViewLoadingWhenSavingTask() {
        val task = Task(1, "title", "description")
        presenter?.saveTask("1", task)
        verify(tasksView).showLoading()
    }

    @Test
    fun presenterShouldDelegateTaskToRepositoryOnSave() {
        val task = Task(1, "title", "description")
        presenter?.saveTask("1", task)
        verify(tasksRepository).saveTask("1", task, presenter!!)
    }

    @Test
    fun presenterShouldReloadTasksListWhenSuccessfullySavedTask() {
        val task = Task(1, "title", "description")
        presenter?.onTaskSaved("groupId", task)
        verify(tasksView).showLoading()
        verify(tasksRepository).fetchTasksForId("groupId", presenter!!)
    }

    fun mockTasks(count: Int): List<Task> {
        val list = mutableListOf<Task>()
        for (i in 0..count) {
            list.add(Task(i, "Task $i", "Description: $i"))
        }
        return list
    }
}