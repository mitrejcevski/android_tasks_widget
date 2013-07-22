package com.mitrejcevski.widget.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.database.DBManipulator;
import com.mitrejcevski.widget.model.Group;
import com.mitrejcevski.widget.model.MyTask;
import com.mitrejcevski.widget.provider.ListWidget;
import com.mitrejcevski.widget.utilities.AppSettings;

import java.util.Calendar;

/**
 * Activity for adding or editing tasks.
 * 
 * @author jovche.mitrejchevski
 */
public class NewItemActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener {

	public static final String MY_TASK_EXTRA = "task_extra";
	public static final String GROUP_EXTRA = "group_extra";
	private EditText mTaskTitle;
	private MyTask mMyTask;
	private String mGroupCalling;
	private ToggleButton mReminder;
	private Button mDateSelector;
	private Calendar mSelectedDateTime = null;
	private Spinner mGroupSelector;
	private ArrayAdapter<Group> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_item_layout);
		int selectedTaskId = getIntent().getIntExtra(MY_TASK_EXTRA, -1);
		mGroupCalling = getIntent().getStringExtra(GROUP_EXTRA);
		if (selectedTaskId > -1)
			mMyTask = DBManipulator.INSTANCE.getTaskById(this,
					selectedTaskId);
		initialize();
	}

	/**
	 * Initializes the UI.
	 */
	private void initialize() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mTaskTitle = (EditText) findViewById(R.id.task_title_edit_text);
		mReminder = (ToggleButton) findViewById(R.id.checkbox_reminder_enable);
		mDateSelector = (Button) findViewById(R.id.date_spinner);
		mGroupSelector = (Spinner) findViewById(R.id.group_selector);
		setupGroupSelector();
		if (mMyTask != null)
			setupViewsValues(mMyTask);
		else {
			mDateSelector.setEnabled(false);
		}
		mReminder.setOnCheckedChangeListener(this);
		mDateSelector.setOnClickListener(this);
	}

	/**
	 * Sets up the data in the group dropdown.
	 */
	private void setupGroupSelector() {
		mAdapter = new ArrayAdapter<Group>(this,
				android.R.layout.simple_spinner_item,
				DBManipulator.INSTANCE.getAllGroups(this));
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mGroupSelector.setAdapter(mAdapter);
		mGroupSelector.setSelection(findGroupIndex(mGroupCalling));
	}

	/**
	 * If there is a task provided when opening the activity, edit it.
	 * 
	 * @param task
	 */
	private void setupViewsValues(MyTask task) {
		mTaskTitle.setText(mMyTask.getName());
		if (task.hasTimeAttached()) {
			mReminder.setChecked(true);
			mDateSelector.setEnabled(mReminder.isChecked());
			Calendar calendar = task.getDateTime();
			mDateSelector.setText(AppSettings.DT_FORMATTER.format(calendar
					.getTime()));
		}
	}

	/**
	 * Returns the position of the group in the adapter if exist. 0 otherwise.
	 * 
	 * @param groupName
	 * @return
	 */
	private int findGroupIndex(String groupName) {
		for (int i = 0; i < mAdapter.getCount(); i++)
			if (mAdapter.getItem(i).toString().equals(groupName))
				return i;
		return 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_task_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_menu_item:
			prepareItem();
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Prepares task for saving. If the task title is empty, shows an error.
	 */
	private void prepareItem() {
		if (mTaskTitle.getText().toString().equals("")) {
			mTaskTitle.setError(getString(R.string.save_task_error));
			return;
		}
		saveItem();
	}

	/**
	 * Creates/Updates a task in the database.
	 */
	private void saveItem() {
		MyTask task = mMyTask == null ? new MyTask() : mMyTask;
		populateTask(task);
		DBManipulator.INSTANCE.createUpdateTask(this, task);
		notifyWidget();
		createAlarm(task);
		Intent intent = getIntent();
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * Creates an alarm if the reminder for the task is enabled.
	 * 
	 * @param task
	 */
	private void createAlarm(MyTask task) {
		if (!mReminder.isChecked())
			return;
		// Create a new PendingIntent and add it to the AlarmManager
		Intent intent = new Intent(this, AlarmReceiverActivity.class);
		intent.putExtra(AlarmClock.EXTRA_MESSAGE, task.getName());
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDateTime()
				.getTimeInMillis(), pendingIntent);
	}

	/**
	 * Sets values to a task object regarding on the fields.
	 * 
	 * @param task
	 *            MyTask object
	 */
	private void populateTask(MyTask task) {
		task.setName(mTaskTitle.getText().toString());
		task.setGroup(mAdapter
				.getItem(mGroupSelector.getSelectedItemPosition()).toString());
		if (mReminder.isChecked()) {
			task.setHasTimeAttached(true);
			if (mSelectedDateTime == null) {
				mSelectedDateTime = Calendar.getInstance();
				mSelectedDateTime.setTimeInMillis(System.currentTimeMillis());
			}
			task.setDateTime(mSelectedDateTime);
		}
	}

	/**
	 * Notifies the widget to reload the data.
	 */
	private void notifyWidget() {
		final Intent fillInIntent = new Intent(this, ListWidget.class);
		fillInIntent.setAction(ListWidget.ADD_ACTION);
		sendBroadcast(fillInIntent);
	}

	/**
	 * If the reminder for the task is enabled, set the data and default value
	 * for the field. Clear the data otherwise.
	 * 
	 * @param isChecked
	 */
	private void enableTimeAndDate(boolean isChecked) {
		mDateSelector.setEnabled(isChecked);
		if (isChecked) {
			mSelectedDateTime = Calendar.getInstance();
			mSelectedDateTime.setTimeInMillis(System.currentTimeMillis());
			mDateSelector.setText(AppSettings.DT_FORMATTER
					.format(mSelectedDateTime.getTime()));
		} else {
			mDateSelector.setText("");
			mSelectedDateTime = null;
		}
	}

	/**
	 * Shows a dialog for selecting date/time.
	 */
	private void selectDate() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.pick_date_dialog);
		dialog.setTitle(R.string.select_date_label);
		Button choose = (Button) dialog
				.findViewById(R.id.choose_date_button_positive);
		Button cancel = (Button) dialog
				.findViewById(R.id.choose_date_button_negative);
		final DatePicker datePicker = (DatePicker) dialog
				.findViewById(R.id.date_picker);
		final TimePicker timePicker = (TimePicker) dialog
				.findViewById(R.id.time_picker);
		choose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateSelectedDate(datePicker, timePicker);
				dialog.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * Update the fields regarding on the selected data.
	 * 
	 * @param datePicker
	 * @param timePicker
	 */
	private void updateSelectedDate(DatePicker datePicker, TimePicker timePicker) {
		int year = datePicker.getYear();
		int month = datePicker.getMonth();
		int day = datePicker.getDayOfMonth();
		int hour = timePicker.getCurrentHour();
		int minute = timePicker.getCurrentMinute();
		mSelectedDateTime = Calendar.getInstance();
		mSelectedDateTime.set(year, month, day, hour, minute);
		mDateSelector.setText(AppSettings.DT_FORMATTER.format(mSelectedDateTime
				.getTime()));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.checkbox_reminder_enable:
			enableTimeAndDate(isChecked);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.date_spinner:
			selectDate();
			break;
		}
	}
}
