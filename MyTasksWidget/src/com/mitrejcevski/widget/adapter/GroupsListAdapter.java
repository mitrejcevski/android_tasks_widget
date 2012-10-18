package com.mitrejcevski.widget.adapter;

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
import com.mitrejcevski.widget.model.Group;

import java.util.ArrayList;

/**
 * Adapter for the groups list.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class GroupsListAdapter extends BaseAdapter {

	private ArrayList<Group> mGroups = new ArrayList<Group>();
	private final LayoutInflater mLayoutInflater;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context of the activity.
	 */
	public GroupsListAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
	}

	/**
	 * Used to add tasks in the list.
	 * 
	 * @param tasks
	 *            An array list of tasks.
	 */
	public void addGroups(ArrayList<Group> tasks) {
		mGroups = tasks;
		notifyDataSetChanged();
	}

	/**
	 * Returns all the groups marked for deleting.
	 * 
	 * @return
	 */
	public ArrayList<Group> getMarkedGroups() {
		ArrayList<Group> groups = new ArrayList<Group>();
		for (Group group : mGroups) {
			if (group.shouldDelete())
				groups.add(group);
		}
		return groups;
	}

	@Override
	public int getCount() {
		return mGroups.size();
	}

	@Override
	public Group getItem(int position) {
		return mGroups.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mGroups.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Group task = getItem(position);
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
	}

	/**
	 * Puts data in the particular row.
	 * 
	 * @param holder
	 *            The holder that is representing the row.
	 * @param group
	 *            The group that should be presented in this row.
	 */
	private void setupHolder(Holder holder, Group group) {
		if (group.shouldDelete()) {
			SpannableString striked = new SpannableString(group.getGroupTitle());
			striked.setSpan(new StrikethroughSpan(), 0, striked.length(),
					Spanned.SPAN_PARAGRAPH);
			holder.mLabel.setText(striked);
		} else {
			holder.mLabel.setText(group.getGroupTitle());
		}
	}

	/**
	 * Model class that keeps instances of the views needed in one particular
	 * row of the list view.
	 * 
	 * @author jovche.mitrejchevski
	 */
	class Holder {
		private TextView mLabel;
	}
}
