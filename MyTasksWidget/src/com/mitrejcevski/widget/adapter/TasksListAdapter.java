package com.mitrejcevski.widget.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.model.MyTask;
import com.mitrejcevski.widget.utilities.Constants;

/**
 * An adapter for the tasks list.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class TasksListAdapter extends BaseAdapter {

	private ArrayList<MyTask> mTasks = new ArrayList<MyTask>();
	private LayoutInflater mLayoutInflater;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context of the activity.
	 */
	public TasksListAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
	}

	/**
	 * Used to add tasks in the list.
	 * 
	 * @param tasks
	 *            An array list of tasks.
	 */
	public void setTasks(ArrayList<MyTask> tasks) {
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
			convertView = mLayoutInflater.inflate(R.layout.list_row_layout,
					null);
			holder = new Holder();
			initializeHolder(convertView, holder);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		setupHolder(holder, task);
		return convertView;
	}

	/**
	 * Initializes the holder of one row item.
	 * 
	 * @param convertView
	 *            The view representing one row item.
	 * @param holder
	 *            Model class that keeps instances of the views needed in one
	 *            particular row item.
	 */
	private void initializeHolder(View convertView, Holder holder) {
		holder.mLabel = (TextView) convertView
				.findViewById(R.id.list_row_label);
		holder.mDate = (TextView) convertView
				.findViewById(R.id.list_row_date_label);
	}

	/**
	 * Puts data in the particular row.
	 * 
	 * @param holder
	 *            The holder that is representing the row.
	 * @param task
	 *            The task that should be presented in this row.
	 */
	private void setupHolder(Holder holder, MyTask task) {
		if (task.isFinished()) {
			SpannableString striked = new SpannableString(task.getName());
			striked.setSpan(new StrikethroughSpan(), 0, striked.length(),
					Spanned.SPAN_PARAGRAPH);
			holder.mLabel.setText(striked);
		} else {
			holder.mLabel.setText(task.getName());
		}
		if (task.getDateTime() != null)
			holder.mDate.setText(Constants.FORMATTER.format(task.getDateTime()
					.getTime()));
	}

	/**
	 * Model class that keeps instances of the views needed in one particular
	 * row of the list view.
	 * 
	 * @author jovche.mitrejchevski
	 * 
	 */
	class Holder {
		private TextView mLabel;
		private TextView mDate;
	}
}
