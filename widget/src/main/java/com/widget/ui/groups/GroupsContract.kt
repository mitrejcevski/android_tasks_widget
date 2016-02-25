package com.widget.ui.groups

internal interface GroupsContract {

    interface GroupsView {
        fun showLoading()

        fun hideLoading()

        fun showToast(resource: Int)

        fun showSnackBar(resource: Int)

        fun setGroups(groups: List<Group>)
    }

    interface GroupsUserAction {
        fun loadGroups()

        fun makeNewGroup(title: String)
    }
}