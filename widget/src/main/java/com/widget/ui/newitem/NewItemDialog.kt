package com.widget.ui.newitem

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.EditText
import com.widget.R

class NewItemDialog : DialogFragment() {

    private var callback: OnDoneCallback = EmptyCallback()
    private var layout: View? = null

    interface OnDoneCallback {
        fun onItemReady(title: String)

        fun onItemError()
    }

    fun withDoneCallback(callback: OnDoneCallback): NewItemDialog {
        this.callback = callback
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        layout = activity.layoutInflater.inflate(R.layout.new_item_dialog_layout, null)
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.newItemDialogTitle)
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
        val titleEdit = layout?.findViewById(R.id.itemTitleEditText) as EditText
        val title = titleEdit.text.toString()
        if (title.length < 1) callback.onItemError() else callback.onItemReady(title)
    }

    private fun addNegativeButton(builder: AlertDialog.Builder) {
        builder.setNegativeButton(R.string.actionCancel) { dialog, button -> dialog.dismiss() }
    }

    private inner class EmptyCallback : OnDoneCallback {
        override fun onItemReady(title: String) {

        }

        override fun onItemError() {

        }
    }
}
