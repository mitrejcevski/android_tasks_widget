package com.mitrejcevski.widget.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.activity.GroupsListActivity;
import com.mitrejcevski.widget.database.DBManipulator;
import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.provider.ListWidget;

/**
 * Dialog for adding or editing groups.
 *
 * @author jovche.mitrejchevski
 */
public class NewGroupDialog extends DialogFragment {

    /**
     * Used for passing extra values in bundle.
     */
    private static final String GROUP_ID_EXTRA = "group_id_extra";
    private Group mGroup;
    private View mView;

    /**
     * Obtain new instance of this dialog.
     *
     * @param groupId In case the dialog is opening to edit a group, this is the
     *                group id of the editing group. In case of creating a new
     *                group, this has to be -1 (or any negative value).
     * @return New instance of this dialog.
     */
    public static NewGroupDialog newInstance(final int groupId) {
        NewGroupDialog dialog = new NewGroupDialog();
        Bundle args = new Bundle();
        args.putInt(GROUP_ID_EXTRA, groupId);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Called when the dialog is creating.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    /**
     * Called when the dialog is creating.
     *
     * @param savedInstanceState The saved instance state.
     * @return New Dialog instance.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int id = getArguments().getInt(GROUP_ID_EXTRA);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.group_editing_title);
        builder.setView(obtainView(id));
        addPositiveButton(builder);
        addNegativeButton(builder);
        return builder.create();
    }

    /**
     * Load the view for this dialog from resources.
     *
     * @param groupId Group ID used when opening this dialog. If it is -1 means that the user
     *                is going to create a new group.
     * @return View from resources.
     */
    private View obtainView(final int groupId) {
        mView = getActivity().getLayoutInflater().inflate(R.layout.group_dialog_layout, null);
        if (groupId != -1) {
            mGroup = DBManipulator.INSTANCE.getGroupById(getActivity(), groupId);
            ((EditText) mView.findViewById(R.id.group_title_edit_text)).setText(mGroup.getGroupTitle());
        }
        return mView;
    }

    /**
     * Adds positive button to the builder that is used to create this dialog.
     *
     * @param builder The builder that is used to create this dialog
     */
    private void addPositiveButton(final AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.save_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                updateGroup();
                dialog.dismiss();
            }
        });
    }

    /**
     * Makes a check if everything is OK before saving the group. In case something
     * is missing the group wont be saved.
     */
    private void updateGroup() {
        EditText titleEdit = (EditText) mView.findViewById(R.id.group_title_edit_text);
        final String title = titleEdit.getText().toString();
        if (title.length() < 1)
            Toast.makeText(getActivity(), R.string.invalid_group_label, Toast.LENGTH_SHORT).show();
        else {
            completeSaving(title);
            ((GroupsListActivity) getActivity()).update();
        }
    }

    /**
     * Saves the group into the database and notifies some parts to update the content.
     *
     * @param title The new title of the group.
     */
    private void completeSaving(final String title) {
        if (mGroup == null)
            mGroup = new Group();
        mGroup.setGroupTitle(title);
        DBManipulator.INSTANCE.saveGroup(getActivity(), mGroup);
        Toast.makeText(getActivity(), R.string.group_added_message, Toast.LENGTH_SHORT).show();
        notifyWidget();
    }

    /**
     * Adds a negative button on the builder used to create this dialog.
     *
     * @param builder The builder used to create this dialog.
     */
    private void addNegativeButton(final AlertDialog.Builder builder) {
        builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Notifies the widget to refresh the data.
     */
    private void notifyWidget() {
        final Intent fillInIntent = new Intent(getActivity(), ListWidget.class);
        fillInIntent.setAction(ListWidget.UPDATE_ACTION);
        getActivity().sendBroadcast(fillInIntent);
    }
}
