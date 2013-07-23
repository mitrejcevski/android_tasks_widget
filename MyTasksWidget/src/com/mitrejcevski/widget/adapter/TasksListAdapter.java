package com.mitrejcevski.widget.adapter;

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

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.activity.MainActivity;
import com.mitrejcevski.widget.database.DBManipulator;
import com.mitrejcevski.widget.model.MyTask;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter for the tasks list.
 *
 * @author jovche.mitrejchevski
 */
public class TasksListAdapter extends BaseAdapter {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<MyTask> mTasks = new ArrayList<MyTask>();

    /**
     * Constructor.
     *
     * @param context Context of the activity.
     */
    public TasksListAdapter(final Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * Used to add tasks in the list.
     *
     * @param tasks An array list of tasks.
     */
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
            convertView = mLayoutInflater.inflate(R.layout.task_list_row_layout, null);
            holder = new Holder();
            initializeHolder(convertView, holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        setupHolder(holder, task);
        return convertView;
    }

    /**
     * Initializes the holder of one row item.
     *
     * @param convertView The view representing one row item.
     * @param holder      Model class that keeps instances of the views needed in one
     *                    particular row item.
     */
    private void initializeHolder(final View convertView, final Holder holder) {
        holder.mLabel = (TextView) convertView.findViewById(R.id.list_row_label);
        holder.mDate = (TextView) convertView.findViewById(R.id.list_row_date_label);
        holder.mCompleteButton = (Button) convertView.findViewById(R.id.button_task_completed);
        holder.mEditButton = (Button) convertView.findViewById(R.id.button_task_edit);
        convertView.setTag(holder);
    }

    /**
     * Puts data in the particular row.
     *
     * @param holder The holder that is representing the row.
     * @param task   The task that should be presented in this row.
     */
    private void setupHolder(final Holder holder, final MyTask task) {
        holder.mLabel.setText(task.isFinished() ? makeStroke(task.getName()) : task.getName());
        String dateTime = task.getDateTimeString();
        holder.mDate.setText(dateTime.equals("") ? mContext.getString(R.string.reminder_not_set) : String.format(mContext.getString(R.string.reminder_set), dateTime));
        setEditAction(holder, task);
        setCompleteAction(holder, task);
    }

    /**
     * Sets action to the edit command.
     *
     * @param holder The holder that is representing the row.
     * @param task   The task that should be presented in this row.
     */
    private void setEditAction(final Holder holder, final MyTask task) {
        holder.mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).editTask(task);
            }
        });
    }

    /**
     * Sets action to the complete command.
     *
     * @param holder The holder that is representing the row.
     * @param task   The task that should be presented in this row.
     */
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

    /**
     * Makes stroke through the incoming text.
     *
     * @param title The title that has to be stroked.
     * @return The incoming argument with a stroke in the middle.
     */
    private SpannableString makeStroke(final String title) {
        SpannableString stroke = new SpannableString(title);
        stroke.setSpan(new StrikethroughSpan(), 0, stroke.length(), Spanned.SPAN_PARAGRAPH);
        return stroke;
    }

    /**
     * Model class that keeps instances of the views needed in one particular
     * row of the list view.
     *
     * @author jovche.mitrejchevski
     */
    private static class Holder {
        private TextView mLabel;
        private TextView mDate;
        private Button mEditButton;
        private Button mCompleteButton;
    }
}
