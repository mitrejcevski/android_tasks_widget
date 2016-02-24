package com.widget.ui.groups

internal interface GroupsRepository {

    interface LoadGroupsCallback {
        fun onGroupsLoaded(groups: List<Group>)
    }

    fun fetchGroups(callback: GroupsPresenter)
}