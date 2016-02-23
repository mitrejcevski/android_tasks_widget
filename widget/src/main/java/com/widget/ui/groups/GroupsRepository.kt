package com.widget.ui.groups

interface GroupsRepository {

    interface LoadGroupsCallback {
        fun onGroupsLoaded(groups: List<Group>)
    }

    fun fetchGroups(callback: GroupsPresenter)
}