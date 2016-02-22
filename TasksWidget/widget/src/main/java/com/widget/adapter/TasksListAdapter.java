package com.widget.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.widget.R;
import com.widget.activity.MainActivity;
import com.widget.database.DBManipulator;
import com.widget.model.MyTask;

import java.util.ArrayList;
import java.util.List;

public class TasksListAdapter extends BaseAdapter {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<MyTask> mTasks = new ArrayList<>();

    public TasksListAdapter(final Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setTasks(final List<MyTask> tasks) {
        mTasks = tasks;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public MyTask getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mTasks.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyTask task = getItem(position);
        Holder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.task_list_item_layout, null);
            holder = new Holder();
            initializeHolder(convertView, holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        setupHolder(holder, task);
        return convertView;
    }

    private void initializeHolder(final View convertView, final Holder holder) {
        holder.mLabel = (TextView) convertView.findViewById(R.id.list_row_label);
        holder.mDate = (TextView) convertView.findViewById(R.id.list_row_date_label);
        holder.mCompleteButton = (Button) convertView.findViewById(R.id.button_task_completed);
        holder.mEditButton = (Button) convertView.findViewById(R.id.button_task_edit);
        convertView.setTag(holder);
    }

    private void setupHolder(final Holder holder, final MyTask task) {
        holder.mLabel.setText(task.isFinished() ? makeStroke(task.getName()) : task.getName());
        String dateTime = task.getDateTimeString();
        holder.mDate.setText(dateTime.equals("") ? mContext.getString(R.string.labelReminderNotSet) : String.format(mContext.getString(R.string.labelReminderSet), dateTime));
        setEditAction(holder, task);
        setCompleteAction(holder, task);
    }

    private void setEditAction(final Holder holder, final MyTask task) {
        holder.mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).editTask(task);
            }
        });
    }

    private void setCompleteAction(final Holder holder, final MyTask task) {
        holder.mCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.setFinished(!task.isFinished());
                DBManipulator.INSTANCE.createUpdateTask(mContext, task);
                notifyDataSetChanged();
                ((MainActivity) mContext).notifyWidget();
            }
        });
    }

    private SpannableString makeStroke(final String title) {
        SpannableString stroke = new SpannableString(title);
        stroke.setSpan(new StrikethroughSpan(), 0, stroke.length(), Spanned.SPAN_PARAGRAPH);
        return stroke;
    }

    private static class Holder {
        private TextView mLabel;
        private TextView mDate;
        private Button mEditButton;
        private Button mCompleteButton;
    }
}
