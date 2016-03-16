package com.widget.ui.tasks

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class TasksPresenterTest {

    private val tasksView = mock(TasksContract.TasksView::class.java)
    private val tasksRepository = TestRepository()
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
        presenter?.loadTasks("")
        verify(tasksView).hideLoading()
    }

    @Test
    fun presenterShouldApplyResultIntoView() {
        presenter?.loadTasks("")
        verify(tasksView).applyItems(anyListOf(Task::class.java))
    }

    class TestRepository : TasksRepository {

        override fun fetchTasksForId(groupId: String, callback: TasksRepository.TasksRepositoryCallback) {
            callback.onTasksLoaded(mockTasks(10))
        }

        fun mockTasks(count: Int): List<Task> {
            val list = mutableListOf<Task>()
            for (i in 0..count) {
                list.add(Task(i, "Task $i", "Description: $i"))
            }
            return list
        }
    }
}