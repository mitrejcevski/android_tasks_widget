package com.mitrejcevski.widget.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.activity.MainActivity;

/**
 * Dialog shown when the user is about to delete the completed tasks.
 *
 * @author jovche.mitrejchevski
 */
public class TasksDeletionDialog extends DialogFragment {

    /**
     * Create a new instance of TasksDeletionDialog.
     */
    public static TasksDeletionDialog newInstance() {
        return new TasksDeletionDialog();
    }

    /**
     * Called when the dialog is creating. We are setting cancellable to false here.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    /**
     * Prepare the dialog and create it from AlertDialog.Builder
     *
     * @param savedInstanceState The saved instance state.
     * @return New Dialog instance.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.deleteDialogTitle);
        builder.setMessage(R.string.deleteDialogMessage);
        addPositiveButton(builder);
        addNegativeButton(builder);
        return builder.create();
    }

    /**
     * Adds a positive button on the dialog with assigned specific action on it (confirmation).
     *
     * @param builder The builder used to create the dialog.
     */
    private void addPositiveButton(final AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.acceptButtonLabel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity) getActivity()).deleteDoneTasks();
                        dialog.dismiss();
                    }
                });
    }

    /**
     * Adds a negative button on the dialog that dismisses the dialog.
     *
     * @param builder The builder used to create the dialog.
     */
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
