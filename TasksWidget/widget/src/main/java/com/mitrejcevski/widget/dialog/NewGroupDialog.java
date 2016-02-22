package com.mitrejcevski.widget.dialog;

import android.annotation.SuppressLint;
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

public class NewGroupDialog extends DialogFragment {

    private static final String GROUP_ID_EXTRA = "group_id_extra";
    private Group mGroup;
    private View mView;

    public static NewGroupDialog newInstance(final int groupId) {
        NewGroupDialog dialog = new NewGroupDialog();
        Bundle args = new Bundle();
        args.putInt(GROUP_ID_EXTRA, groupId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int id = getArguments().getInt(GROUP_ID_EXTRA);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.groupEditorTitle);
        builder.setView(obtainView(id));
        addPositiveButton(builder);
        addNegativeButton(builder);
        return builder.create();
    }

    @SuppressLint("InflateParams")
    private View obtainView(final int groupId) {
        mView = getActivity().getLayoutInflater().inflate(R.layout.group_dialog_layout, null);
        if (groupId != -1) {
            mGroup = DBManipulator.INSTANCE.getGroupById(getActivity(), groupId);
            ((EditText) mView.findViewById(R.id.group_title_edit_text)).setText(mGroup.getGroupTitle());
        }
        return mView;
    }

    private void addPositiveButton(final AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.actionSave, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                updateGroup();
                dialog.dismiss();
            }
        });
    }

    private void updateGroup() {
        EditText titleEdit = (EditText) mView.findViewById(R.id.group_title_edit_text);
        final String title = titleEdit.getText().toString();
        if (title.length() < 1)
            Toast.makeText(getActivity(), R.string.labelInvalidGroup, Toast.LENGTH_SHORT).show();
        else {
            completeSaving(title);
            ((GroupsListActivity) getActivity()).update();
        }
    }

    private void completeSaving(final String title) {
        if (mGroup == null)
            mGroup = new Group();
        mGroup.setGroupTitle(title);
        DBManipulator.INSTANCE.saveGroup(getActivity(), mGroup);
        Toast.makeText(getActivity(), R.string.successMessageLabel, Toast.LENGTH_SHORT).show();
        notifyWidget();
    }

    private void addNegativeButton(final AlertDialog.Builder builder) {
        builder.setNegativeButton(R.string.actionCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
    }

    private void notifyWidget() {
        final Intent fillInIntent = new Intent(getActivity(), ListWidget.class);
        fillInIntent.setAction(ListWidget.UPDATE_ACTION);
        getActivity().sendBroadcast(fillInIntent);
    }
}
