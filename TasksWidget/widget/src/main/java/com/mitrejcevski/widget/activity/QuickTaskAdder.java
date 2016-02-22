package com.mitrejcevski.widget.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.database.DBManipulator;
import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.model.MyTask;
import com.mitrejcevski.widget.provider.ListWidget;

public class QuickTaskAdder extends AppCompatActivity implements OnClickListener {

    private EditText mTaskLabel;
    private Spinner mGroupSelector;
    private ArrayAdapter<Group> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_adder_layout);
        setupSize();
        initialize();
    }

    private void initialize() {
        mTaskLabel = (EditText) findViewById(R.id.quick_adder_task_label);
        Button saveAction = (Button) findViewById(R.id.quick_adder_save_action);
        Button cancelAction = (Button) findViewById(R.id.quick_adder_cancel_action);
        mGroupSelector = (Spinner) findViewById(R.id.quick_group_dropdown);
        mGroupSelector.setVisibility(View.VISIBLE);
        setupGroupSelector();
        saveAction.setOnClickListener(this);
        cancelAction.setOnClickListener(this);
    }

    private void setupGroupSelector() {
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DBManipulator.INSTANCE.getAllGroups(this));
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGroupSelector.setAdapter(mAdapter);
    }

    private void setupSize() {
        LayoutParams params = getWindow().getAttributes();
        params.width = getScreenSize().widthPixels - 100;
        getWindow().setAttributes(params);
    }

    private DisplayMetrics getScreenSize() {
        // TODO Need some more logic if the screen is from tablet, because that
        // case
        // the dialog will be very big.
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }

    private void doSave() {
        String taskLabel = mTaskLabel.getText().toString();
        if (taskLabel.length() < 1) {
            mTaskLabel.setError(getString(R.string.emptyFieldErrorMessage));
            return;
        }
        saveItem(taskLabel);
    }

    private void saveItem(String taskName) {
        MyTask task = new MyTask();
        task.setName(taskName);
        task.setGroup(mAdapter.getItem(mGroupSelector.getSelectedItemPosition()).toString());
        task.setHasTimeAttached(false);
        DBManipulator.INSTANCE.createUpdateTask(this, task);
        notifyWidget();
        finish();
    }

    private void notifyWidget() {
        final Intent fillInIntent = new Intent(this, ListWidget.class);
        fillInIntent.setAction(ListWidget.ADD_ACTION);
        sendBroadcast(fillInIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quick_adder_save_action:
                doSave();
                break;
            case R.id.quick_adder_cancel_action:
                finish();
                break;
        }
    }
}
