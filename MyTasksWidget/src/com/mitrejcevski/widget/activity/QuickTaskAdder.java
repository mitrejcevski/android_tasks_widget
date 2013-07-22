package com.mitrejcevski.widget.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Dialog activity for quick task adding. It is called from the widget.
 * 
 * @author jovche.mitrejchevski
 */
public class QuickTaskAdder extends Activity implements OnClickListener {

	private EditText mTaskLabel;
	private Button mSaveAction;
	private Button mCancelAction;
	private Spinner mGroupSelector;
	private ArrayAdapter<Group> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quick_adder_layout);
		setupSize();
		initialize();
	}

	/**
	 * Initializes the UI.
	 */
	private void initialize() {
		mTaskLabel = (EditText) findViewById(R.id.quick_adder_task_label);
		mSaveAction = (Button) findViewById(R.id.quick_adder_save_aciton);
		mCancelAction = (Button) findViewById(R.id.quick_adder_cancel_action);
		mGroupSelector = (Spinner) findViewById(R.id.quick_group_dropdown);
		mGroupSelector.setVisibility(View.VISIBLE);
		setupGroupSelector();
		mSaveAction.setOnClickListener(this);
		mCancelAction.setOnClickListener(this);
	}

	/**
	 * Sets up the values for the group dropdown.
	 */
	private void setupGroupSelector() {
		mAdapter = new ArrayAdapter<Group>(this,
				android.R.layout.simple_spinner_item,
				DBManipulator.INSTANCE.getAllGroups(this));
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mGroupSelector.setAdapter(mAdapter);
	}

	/**
	 * Setup the size of the dialog in the screen.
	 */
	private void setupSize() {
		LayoutParams params = getWindow().getAttributes();
		params.width = getScreenSize().widthPixels - 100;
		getWindow().setAttributes(params);
	}

	/**
	 * Get the actual screen size of the device.
	 * 
	 * @return DisplayMetrics Object that contains all the metrics for the
	 *         actual screen.
	 */
	private DisplayMetrics getScreenSize() {
		// TODO Need some more logic if the screen is from tablet, because that
		// case
		// the dialog will be very big.
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics;
	}

	/**
	 * Prepares the task for save. If the task name is empty it is not going to
	 * save the task, but it will show an error message.
	 */
	private void doSave() {
		String taskLabel = mTaskLabel.getText().toString();
		if (taskLabel.equals("")) {
			mTaskLabel.setError(getString(R.string.save_task_error));
			return;
		}
		saveItem(taskLabel);
	}

	/**
	 * Saves the task in the database.
	 * 
	 * @param taskName
	 *            The label of the task.
	 */
	private void saveItem(String taskName) {
		MyTask task = new MyTask();
		task.setName(taskName);
		task.setGroup(mAdapter
				.getItem(mGroupSelector.getSelectedItemPosition()).toString());
		task.setHasTimeAttached(false);
		DBManipulator.INSTANCE.createUpdateTask(this, task);
		notifyWidget();
		finish();
	}

	/**
	 * Notifies the widget to refresh the data.
	 */
	private void notifyWidget() {
		final Intent fillInIntent = new Intent(this, ListWidget.class);
		fillInIntent.setAction(ListWidget.ADD_ACTION);
		sendBroadcast(fillInIntent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.quick_adder_save_aciton:
			doSave();
			break;
		case R.id.quick_adder_cancel_action:
			finish();
			break;
		}
	}
}
