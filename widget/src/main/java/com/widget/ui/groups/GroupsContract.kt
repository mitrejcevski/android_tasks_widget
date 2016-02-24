package com.widget.ui.groups

internal interface GroupsContract {

    interface GroupsView {
        fun showLoading()

        fun hideLoading()

        fun setGroups(groups: List<Group>)
    }

    interface GroupsUserAction {
        fun loadGroups()
    }
}