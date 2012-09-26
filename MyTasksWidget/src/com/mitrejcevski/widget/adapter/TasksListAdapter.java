package com.mitrejcevski.widget.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.model.MyTask;

/**
 * An adapter for the tasks list.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class TasksListAdapter extends BaseAdapter {

	private ArrayList<MyTask> mTasks = new ArrayList<MyTask>();
	private LayoutInflater mLayoutInflater;
	private boolean mIsInEditMode = false;

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
	public void addTasks(ArrayList<MyTask> tasks) {
		mTasks = tasks;
		notifyDataSetChanged();
	}

	/**
	 * Sets the list edit mode. In edit mode a check box is shown for each list
	 * item and the user can choose several items for instance to delete them.
	 * 
	 * @param editMode
	 */
	public void setEditMode(boolean editMode) {
		mIsInEditMode = editMode;
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
		holder.mCheck = (CheckBox) convertView.findViewById(R.id.delete_check);
	}

	/**
	 * Puts data in the particular row.
	 * 
	 * @param holder
	 *            The holder that is representing the row.
	 * @param task
	 *            The taks that should be presented in this row.
	 */
	private void setupHolder(Holder holder, final MyTask task) {
		holder.mCheck.setVisibility(mIsInEditMode ? View.VISIBLE : View.GONE);
		holder.mCheck.setChecked(task.shouldDelete());
		holder.mLabel.setText(task.getName());
		holder.mCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				task.setShouldDelete(isChecked);
			}
		});
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
		private CheckBox mCheck;
	}
}
