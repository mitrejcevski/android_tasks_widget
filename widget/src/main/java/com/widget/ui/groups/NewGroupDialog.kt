package com.widget.ui.groups

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.EditText
import com.widget.R

internal class NewGroupDialog : DialogFragment() {

    private var callback: OnDoneCallback = EmptyCallback()
    private var layout: View? = null

    interface OnDoneCallback {
        fun onGroupReady(title: String)

        fun onGroupError()
    }

    fun withDoneCallback(callback: OnDoneCallback): NewGroupDialog {
        this.callback = callback
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        layout = activity.layoutInflater.inflate(R.layout.group_dialog_layout, null)
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.groupEditorTitle)
        builder.setView(layout)
        addPositiveButton(builder)
        addNegativeButton(builder)
        return builder.create()
    }

    private fun addPositiveButton(builder: AlertDialog.Builder) {
        builder.setPositiveButton(R.string.actionSave) { dialog, button ->
            updateGroup()
            dialog.dismiss()
        }
    }

    private fun updateGroup() {
        val titleEdit = layout?.findViewById(R.id.groupTitleEditText) as EditText
        val title = titleEdit.text.toString()
        if (title.length < 1) callback.onGroupError() else callback.onGroupReady(title)
    }

    private fun addNegativeButton(builder: AlertDialog.Builder) {
        builder.setNegativeButton(R.string.actionCancel) { dialog, button -> dialog.dismiss() }
    }

    private inner class EmptyCallback : OnDoneCallback {
        override fun onGroupReady(title: String) {

        }

        override fun onGroupError() {

        }
    }
}