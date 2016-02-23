package com.widget.ui.groups

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class GroupsPresenterTest {

    private val groupsView = mock(GroupsContract.GroupsView::class.java)
    private val groupsRepository = mock(GroupsRepository::class.java)
    private var presenter: GroupsPresenter? = null

    @Before
    fun setUp() {
        presenter = GroupsPresenter(groupsView, groupsRepository)
    }

    @Test
    fun presenterShouldTriggerViewLoadingBeforeFetchingData() {
        presenter?.loadGroups()
        verify(groupsView).showLoading()
    }

    @Test
    fun presenterShouldFetchDataFromRepository() {
        presenter?.loadGroups()
        verify(groupsRepository).fetchGroups(presenter!!)
    }

    @Test
    fun presenterShouldTriggerViewLoadingHideAfterDataLoaded() {
        presenter?.onGroupsLoaded(mockGroups())
        verify(groupsView).hideLoading()
    }

    @Test
    fun presenterShouldApplyLoadedItemsIntoView() {
        val items = mockGroups()
        presenter?.onGroupsLoaded(items)
        verify(groupsView).setGroups(items)
    }

    private fun mockGroups(): List<Group> {
        return listOf(
                Group(1, "Title 1"),
                Group(2, "Title 2"),
                Group(3, "Title 3"),
                Group(4, "Title 4"),
                Group(5, "Title 5")
        )
    }
}