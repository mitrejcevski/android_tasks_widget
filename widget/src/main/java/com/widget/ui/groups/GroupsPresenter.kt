package com.widget.ui.groups

class GroupsPresenter(val groupsView: GroupsContract.GroupsView, val groupsRepository: GroupsRepository) :
        GroupsContract.GroupsUserAction, GroupsRepository.LoadGroupsCallback {

    override fun loadGroups() {
        groupsView.showLoading()
        groupsRepository.fetchGroups(this)
    }

    override fun onGroupsLoaded(groups: List<Group>) {
        groupsView.hideLoading()
        groupsView.setGroups(groups)
    }
}