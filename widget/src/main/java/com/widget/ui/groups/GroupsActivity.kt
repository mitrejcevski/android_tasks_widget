package com.widget.ui.groups

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class GroupsActivity : AppCompatActivity(), GroupsContract.GroupsView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun showLoading() {
        throw UnsupportedOperationException()
    }

    override fun hideLoading() {
        throw UnsupportedOperationException()
    }

    override fun setGroups(groups: List<Group>) {
        throw UnsupportedOperationException()
    }
}
