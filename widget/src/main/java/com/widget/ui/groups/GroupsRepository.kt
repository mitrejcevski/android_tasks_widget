package com.widget.ui.groups

internal interface GroupsRepository {

    interface GroupsRepositoryCallback {
        fun onGroupsLoaded(groups: List<Group>)
        fun onGroupSaved()
    }

    fun fetchGroups(callback: GroupsPresenter)

    fun saveGroup(groupTitle: String, callback: GroupsPresenter)
}