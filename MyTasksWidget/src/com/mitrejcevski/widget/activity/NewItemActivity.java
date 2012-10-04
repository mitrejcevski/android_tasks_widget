package com.mitrejcevski.widget.activity;

import java.util.Calendar;

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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.database.DatabaseManipulator;
import com.mitrejcevski.widget.model.MyTask;
import com.mitrejcevski.widget.provider.ListWidget;
import com.mitrejcevski.widget.utilities.Constants;

/**
 * Activity for adding or editing tasks.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class NewItemActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener {

	public static final String MY_TASK_EXTRA = "my_task";

	private EditText mTaskTitle;
	private MyTask mMyTask;
	private ToggleButton mReminder;
	private Button mDateSelector;
	private Calendar mSelectedDateTime = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_item_layout);
		initialize();
	}

	/**
	 * Initializes the UI.
	 */
	private void initialize() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mMyTask = getIntent().getParcelableExtra(MY_TASK_EXTRA);
		mTaskTitle = (EditText) findViewById(R.id.task_title_edit_text);
		mReminder = (ToggleButton) findViewById(R.id.checkbox_reminder_enable);
		mDateSelector = (Button) findViewById(R.id.date_spinner);
		if (mMyTask != null)
			setupViewsValues(mMyTask);
		else {
			mDateSelector.setEnabled(false);
		}
		mReminder.setOnCheckedChangeListener(this);
		mDateSelector.setOnClickListener(this);
	}

	private void setupViewsValues(MyTask task) {
		mTaskTitle.setText(mMyTask.getName());
		if (task.hasTimeAttached()) {
			mReminder.setChecked(true);
			mDateSelector.setEnabled(mReminder.isChecked());
			Calendar calendar = task.getDateTime();
			mDateSelector.setText(Constants.DT_FORMATTER.format(calendar
					.getTime()));
		}
	}

	/**
	 * Inflates a menu from the menu folder in resources
	 */
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
		DatabaseManipulator.INSTANCE.open(this);
		MyTask task = mMyTask == null ? new MyTask() : mMyTask;
		populateTask(task);
		if (mMyTask == null)
			task.setId(DatabaseManipulator.INSTANCE.createTask(task));
		else
			DatabaseManipulator.INSTANCE.updateTask(task);
		DatabaseManipulator.INSTANCE.close();
		notifyWidget(task);
		createAlarm(task);
		finish();
	}

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
	 * Sets values to a task object;
	 * 
	 * @param task
	 *            MyTask object
	 */
	private void populateTask(MyTask task) {
		task.setName(mTaskTitle.getText().toString());
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
	 * 
	 * @param task
	 *            The task that was managed in this activity.
	 */
	private void notifyWidget(MyTask task) {
		final Intent fillInIntent = new Intent(this, ListWidget.class);
		fillInIntent.setAction(ListWidget.ADD_ACTION);
		final Bundle extras = new Bundle();
		extras.putInt(ListWidget.EXTRA_TASK_ID, task.getId());
		fillInIntent.putExtras(extras);
		sendBroadcast(fillInIntent);
	}

	private void enableTimeAndDate(boolean isChecked) {
		mDateSelector.setEnabled(isChecked);
		if (isChecked) {
			mSelectedDateTime = Calendar.getInstance();
			mSelectedDateTime.setTimeInMillis(System.currentTimeMillis());
			mDateSelector.setText(Constants.DT_FORMATTER
					.format(mSelectedDateTime.getTime()));
		} else {
			mDateSelector.setText("");
			mSelectedDateTime = null;
		}
	}

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

	private void updateSelectedDate(DatePicker datePicker, TimePicker timePicker) {
		int year = datePicker.getYear();
		int month = datePicker.getMonth();
		int day = datePicker.getDayOfMonth();
		int hour = timePicker.getCurrentHour();
		int minute = timePicker.getCurrentMinute();
		mSelectedDateTime = Calendar.getInstance();
		mSelectedDateTime.set(year, month, day, hour, minute);
		mDateSelector.setText(Constants.DT_FORMATTER.format(mSelectedDateTime
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