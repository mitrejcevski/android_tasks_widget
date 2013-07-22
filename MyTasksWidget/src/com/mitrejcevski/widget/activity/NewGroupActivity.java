package com.mitrejcevski.widget.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.database.DBManipulator;
import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.provider.ListWidget;

/**
 * Dialog activity for adding new groups in the database.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class NewGroupActivity extends Activity implements OnClickListener {

	public static final String GROUP_ID_EXTRA = "group_id_extra";

	private EditText mGroupName;
	private Button mSaveAction;
	private Button mCancelAction;
	private Group mGroup = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quick_adder_layout);
		int groupId = getIntent().getIntExtra(GROUP_ID_EXTRA, -1);
		setupSize();
		initialize();
		// if there is id in the extras, edit the group instead of creating new
		// one.
		if (groupId > -1)
			showValues(groupId);
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
	 * Initializes the UI.
	 */
	private void initialize() {
		mGroupName = (EditText) findViewById(R.id.quick_adder_task_label);
		mSaveAction = (Button) findViewById(R.id.quick_adder_save_aciton);
		mCancelAction = (Button) findViewById(R.id.quick_adder_cancel_action);
		mSaveAction.setOnClickListener(this);
		mCancelAction.setOnClickListener(this);
	}

	/**
	 * If the activity is called with extra group id, that means that the group
	 * should be edited.
	 * 
	 * @param id
	 */
	private void showValues(int id) {
		mGroup = DBManipulator.INSTANCE.getGroupById(this, id);
		mGroupName.setText(mGroup.getGroupTitle());
	}

	/**
	 * Notifies the widget to refresh the data.
	 */
	private void notifyWidget() {
		final Intent fillInIntent = new Intent(this, ListWidget.class);
		fillInIntent.setAction(ListWidget.UPDATE_ACTION);
		sendBroadcast(fillInIntent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.quick_adder_save_aciton:
			prepareGroup();
			break;
		case R.id.quick_adder_cancel_action:
			finish();
			break;
		}
	}

	/**
	 * Check if the group is ready to be saved in database, and proceed if true.
	 */
	private void prepareGroup() {
		String name = mGroupName.getText().toString();
		if (name.equals(""))
			mGroupName.setError(getString(R.string.empty_field_error_message));
		else
			saveGroup(name);
	}

	/**
	 * Save the group in the database.
	 * 
	 * @param name
	 */
	private void saveGroup(String name) {
		Group group = mGroup == null ? new Group() : mGroup;
		group.setGroupTitle(name);
		DBManipulator.INSTANCE.saveGroup(this, group);
		Toast.makeText(this, R.string.tab_added_message, Toast.LENGTH_SHORT)
				.show();
		notifyWidget();
		finish();
	}
}
