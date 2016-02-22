package com.widget.activity;

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

import com.widget.R;
import com.widget.database.DBManipulator;
import com.widget.model.Group;
import com.widget.model.MyTask;
import com.widget.notification.AlarmService;
import com.widget.notification.OnAlarmReceiver;
import com.widget.provider.ListWidget;
import com.widget.utils.AppSettings;

import java.util.Calendar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadExtras();
        initialize();
    }

    private void loadExtras() {
        int selectedTaskId = getIntent().getIntExtra(MY_TASK_EXTRA, -1);
        mGroupCalling = getIntent().getStringExtra(GROUP_EXTRA);
        if (selectedTaskId > -1)
            mMyTask = DBManipulator.INSTANCE.getTaskById(this, selectedTaskId);
    }

    private void initialize() {
        setContentView(R.layout.new_full_task_layout);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mTaskTitle = (EditText) findViewById(R.id.task_title_edit_text);
        mReminder = (ToggleButton) findViewById(R.id.checkbox_reminder_enable);
        mDateSelector = (Button) findViewById(R.id.date_spinner);
        mGroupSelector = (Spinner) findViewById(R.id.group_selector);
        setupFields();
    }

    private void setupFields() {
        setupGroupSelector();
        if (mMyTask != null)
            setupViewsValues(mMyTask);
        else
            mDateSelector.setEnabled(false);
        mReminder.setOnCheckedChangeListener(this);
        mDateSelector.setOnClickListener(this);
    }

    private void setupGroupSelector() {
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DBManipulator.INSTANCE.getAllGroups(this));
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGroupSelector.setAdapter(mAdapter);
        mGroupSelector.setSelection(findGroupIndex(mGroupCalling));
    }

    private void setupViewsValues(MyTask task) {
        mTaskTitle.setText(mMyTask.getName());
        if (task.hasTimeAttached()) {
            mReminder.setChecked(true);
            mDateSelector.setEnabled(mReminder.isChecked());
            Calendar calendar = task.getDateTime();
            mDateSelector.setText(AppSettings.DT_FORMATTER.format(calendar.getTime()));
        }
    }

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

    private void prepareItem() {
        if (TextUtils.isEmpty(mTaskTitle.getText()))
            mTaskTitle.setError(getString(R.string.emptyFieldErrorMessage));
        else
            saveItem();
    }

    private void saveItem() {
        MyTask task = mMyTask == null ? new MyTask() : mMyTask;
        populateTask(task);
        DBManipulator.INSTANCE.createUpdateTask(this, task);
        notifyWidget();
        createAlarm(task);
        setResults();
    }

    private void setResults() {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

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

    private void notifyWidget() {
        final Intent fillInIntent = new Intent(this, ListWidget.class);
        fillInIntent.setAction(ListWidget.ADD_ACTION);
        sendBroadcast(fillInIntent);
    }

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
