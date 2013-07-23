package com.mitrejcevski.widget.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.activity.GroupsListActivity;

/**
 * Dialog used for user confirmation on deletion of groups.
 *
 * @author jovche.mitrejchevski
 */
public class GroupsDeletionDialog extends DialogFragment {

    /**
     * Obtains a new instance of this dialog.
     *
     * @return New instance of this dialog.
     */
    public static GroupsDeletionDialog newInstance() {
        return new GroupsDeletionDialog();
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
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_dialog_title);
        builder.setMessage(R.string.delete_groups_dialog_message);
        addPositiveButton(builder);
        addNegativeButton(builder);
        return builder.create();
    }

    /**
     * Adds positive button on the builder used to create this dialog.
     *
     * @param builder The builder used to create this dialog.
     */
    private void addPositiveButton(final AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.accept_button_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((GroupsListActivity) getActivity()).deleteSelectedGroups();
                        dialog.dismiss();
                    }
                });
    }

    /**
     * Adds negative button on the builder used to create this dialog.
     *
     * @param builder The builder used to create this dialog.
     */
    private void addNegativeButton(final AlertDialog.Builder builder) {
        builder.setNegativeButton(R.string.reject_button_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }
}
