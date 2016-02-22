package com.widget.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.widget.R;
import com.widget.activity.GroupsListActivity;

public class GroupsDeletionDialog extends DialogFragment {

    public static GroupsDeletionDialog newInstance() {
        return new GroupsDeletionDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.deleteDialogTitle);
        builder.setMessage(R.string.deleteGroupsDialogMessage);
        addPositiveButton(builder);
        addNegativeButton(builder);
        return builder.create();
    }

    private void addPositiveButton(final AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.acceptButtonLabel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((GroupsListActivity) getActivity()).deleteSelectedGroups();
                        dialog.dismiss();
                    }
                });
    }

    private void addNegativeButton(final AlertDialog.Builder builder) {
        builder.setNegativeButton(R.string.rejectButtonLabel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }
}
