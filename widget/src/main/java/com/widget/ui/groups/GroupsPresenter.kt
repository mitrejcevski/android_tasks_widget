package com.widget.ui.groups

import com.widget.R

internal class GroupsPresenter(private val groupsView: GroupsContract.GroupsView,
                               private val groupsRepository: GroupsRepository) :
        GroupsContract.GroupsUserAction, GroupsRepository.GroupsRepositoryCallback {

    override fun loadGroups() {
        groupsView.showLoading()
        groupsRepository.fetchGroups(this)
    }

    override fun onGroupsLoaded(groups: List<Group>) {
        groupsView.hideLoading()
        groupsView.setGroups(groups)
    }

    override fun makeNewGroup(title: String) {
        groupsView.showLoading()
        groupsRepository.saveGroup(title, this)
    }

    override fun onGroupSaved() {
        groupsView.showSnackBar(R.string.successMessageLabel)
        loadGroups()
    }
}