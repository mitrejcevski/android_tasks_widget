package com.mitrejcevski.widget.activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.mitrejcevski.widget.notification.AlarmService;
import com.mitrejcevski.widget.notification.OnAlarmReceiver;
import com.mitrejcevski.widget.provider.ListWidget;
import com.mitrejcevski.widget.utils.AppSettings;

import java.util.Calendar;

/**
 * Activity for adding or editing tasks.
 *
 * @author jovche.mitrejchevski
 */
public class NewItemActivity extends AppCompatActivity implements OnCheckedChangeListener, OnClickListener {

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

    /**
     * Called when the activity is creating.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadExtras();
        initialize();
    }

    /**
     * Loads the extras in the incoming intent.
     */
    private void loadExtras() {
        int selectedTaskId = getIntent().getIntExtra(MY_TASK_EXTRA, -1);
        mGroupCalling = getIntent().getStringExtra(GROUP_EXTRA);
        if (selectedTaskId > -1)
            mMyTask = DBManipulator.INSTANCE.getTaskById(this, selectedTaskId);
    }

    /**
     * Initializes the UI.
     */
    private void initialize() {
        setContentView(R.layout.new_full_task_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTaskTitle = (EditText) findViewById(R.id.task_title_edit_text);
        mReminder = (ToggleButton) findViewById(R.id.checkbox_reminder_enable);
        mDateSelector = (Button) findViewById(R.id.date_spinner);
        mGroupSelector = (Spinner) findViewById(R.id.group_selector);
        setupFields();
    }

    /**
     * Triggers some setup on the activity views and adds listeners.
     */
    private void setupFields() {
        setupGroupSelector();
        if (mMyTask != null)
            setupViewsValues(mMyTask);
        else
            mDateSelector.setEnabled(false);
        mReminder.setOnCheckedChangeListener(this);
        mDateSelector.setOnClickListener(this);
    }

    /**
     * Sets up the data in the group drop down.
     */
    private void setupGroupSelector() {
        mAdapter = new ArrayAdapter<Group>(this, android.R.layout.simple_spinner_item, DBManipulator.INSTANCE.getAllGroups(this));
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGroupSelector.setAdapter(mAdapter);
        mGroupSelector.setSelection(findGroupIndex(mGroupCalling));
    }

    /**
     * If there is a task provided when opening the activity, edit it.
     *
     * @param task The task.
     */
    private void setupViewsValues(MyTask task) {
        mTaskTitle.setText(mMyTask.getName());
        if (task.hasTimeAttached()) {
            mReminder.setChecked(true);
            mDateSelector.setEnabled(mReminder.isChecked());
            Calendar calendar = task.getDateTime();
            mDateSelector.setText(AppSettings.DT_FORMATTER.format(calendar.getTime()));
        }
    }

    /**
     * Returns the position of the group in the adapter if exist. 0 otherwise.
     *
     * @param groupName The name of the group.
     * @return The position of the requested group inside the drop down adapter.
     */
    private int findGroupIndex(String groupName) {
        for (int i = 0; i < mAdapter.getCount(); i++)
            if (mAdapter.getItem(i).toString().equals(groupName))
                return i;
        return 0;
    }

    /**
     * Called when the options menu is creating. Inflates a menu from the resources.
     *
     * @param menu The menu where the resource would be attached.
     * @return True always.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_task_menu, menu);
        return true;
    }

    /**
     * Called when the user clicks on a particular menu item.
     *
     * @param item The clicked menu item.
     * @return True if clicked one of the items inside the inflated menu from resources.
     * Returns the call to super otherwise.
     */
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
        if (TextUtils.isEmpty(mTaskTitle.getText()))
            mTaskTitle.setError(getString(R.string.emptyFieldErrorMessage));
        else
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
        setResults();
    }

    /**
     * Sets RESULT_OK to the intent and destroys the activity.
     */
    private void setResults() {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Creates an alarm if the reminder for the task is enabled.
     *
     * @param task The task.
     */
    private void createAlarm(final MyTask task) {
        if (mReminder.isChecked()) {
            Intent intent = new Intent(this, OnAlarmReceiver.class);
            intent.putExtra(AlarmService.NOTIFICATION_ID, task.getId());
            intent.putExtra(AlarmService.NOTIFICATION_TITLE, task.getName());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDateTime().getTimeInMillis(), pendingIntent);
        }
    }

    /**
     * Sets values to a task object regarding on the fields.
     *
     * @param task MyTask object
     */
    private void populateTask(MyTask task) {
        task.setName(mTaskTitle.getText().toString());
        task.setGroup(mAdapter.getItem(mGroupSelector.getSelectedItemPosition()).toString());
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
     * @param isChecked Flag that shows if the reminder is enabled.
     */
    private void enableTimeAndDate(final boolean isChecked) {
        mDateSelector.setEnabled(isChecked);
        if (isChecked) {
            mSelectedDateTime = Calendar.getInstance();
            mSelectedDateTime.setTimeInMillis(System.currentTimeMillis());
            mDateSelector.setText(AppSettings.DT_FORMATTER.format(mSelectedDateTime.getTime()));
        } else {
            mDateSelector.setText(getString(R.string.labelReminderNotSet));
            mSelectedDateTime = null;
        }
    }

    /**
     * Shows a dialog for selecting date/time.
     */
    private void selectDate() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.pick_date_dialog);
        dialog.setTitle(R.string.labelSelectDate);
        Button choose = (Button) dialog.findViewById(R.id.choose_date_button_positive);
        Button cancel = (Button) dialog.findViewById(R.id.choose_date_button_negative);
        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.time_picker);
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
     * @param datePicker Date picked object.
     * @param timePicker Time picker object.
     */
    private void updateSelectedDate(DatePicker datePicker, TimePicker timePicker) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        mSelectedDateTime = Calendar.getInstance();
        mSelectedDateTime.set(year, month, day, hour, minute);
        mDateSelector.setText(AppSettings.DT_FORMATTER.format(mSelectedDateTime.getTime()));
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
