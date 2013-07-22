package com.mitrejcevski.widget.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.activity.MainActivity;

/**
 * @author jovche.mitrejchevski
 */
public class TasksDeletionDialog extends DialogFragment {

    /**
     * Create a new instance of TasksDeletionDialog.
     */
    public static TasksDeletionDialog newInstance() {
        return new TasksDeletionDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_dialog_title);
        builder.setMessage(R.string.delete_dialog_messaeg);
        addPositiveButton(builder);
        addNegativeButton(builder);
        return builder.create();
    }

    private void addPositiveButton(final AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.accept_button_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity) getActivity()).deleteDoneTasks();
                        dialog.dismiss();
                    }
                });
    }

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
