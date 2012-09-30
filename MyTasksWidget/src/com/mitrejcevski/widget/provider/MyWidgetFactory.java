package com.mitrejcevski.widget.provider;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.database.DatabaseManipulator;
import com.mitrejcevski.widget.model.MyTask;
import com.mitrejcevski.widget.utilities.Constants;

/**
 * Kind of adapter for the items in the list of the widget.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class MyWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

	private Context mContext = null;
	private ArrayList<MyTask> mTasks;

	/**
	 * Constructor.
	 * 
	 * @param context
	 * @param intent
	 */
	public MyWidgetFactory(Context context, Intent intent) {
		mContext = context;
		initialize();
	}

	/**
	 * Initialize the items that should be shown in the widget.
	 */
	private void initialize() {
		DatabaseManipulator.INSTANCE.open(mContext);
		mTasks = DatabaseManipulator.INSTANCE.getAllTasks();
		DatabaseManipulator.INSTANCE.close();
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onDestroy() {

	}

	@Override
	public int getCount() {
		return (mTasks.size());
	}

	@Override
	public RemoteViews getViewAt(int position) {
		// setup a row of the list in the widget.
		MyTask task = mTasks.get(position);
		RemoteViews row = new RemoteViews(mContext.getPackageName(),
				R.layout.one_list_row_layout);
		// if the task is finished, strike the text;
		if (task.isFinished()) {
			SpannableString striked = new SpannableString(task.getName());
			striked.setSpan(new StrikethroughSpan(), 0, striked.length(),
					Spanned.SPAN_PARAGRAPH);
			row.setTextViewText(R.id.m_row_label, striked);
		} else {
			row.setTextViewText(R.id.m_row_label, task.getName());
		}
		if (task.hasTimeAttached()) {
			Date date = task.getDateTime().getTime();
			row.setTextViewText(R.id.m_row_date,
					Constants.FORMATTER.format(date));
		} else {
			row.setTextViewText(R.id.m_row_date, "");
		}
		// set an action on click on the row item
		final Intent fillInIntent = new Intent();
		final Bundle extras = new Bundle();
		extras.putInt(ListWidget.EXTRA_TASK_ID, task.getId());
		fillInIntent.putExtras(extras);
		row.setOnClickFillInIntent(R.id.m_row_label, fillInIntent);
		row.setOnClickFillInIntent(R.id.m_row_date, fillInIntent);
		return (row);
	}

	@Override
	public RemoteViews getLoadingView() {
		return (null);
	}

	@Override
	public int getViewTypeCount() {
		return (1);
	}

	@Override
	public long getItemId(int position) {
		return (position);
	}

	@Override
	public boolean hasStableIds() {
		return (true);
	}

	@Override
	public void onDataSetChanged() {
		initialize();
	}
}
