package com.widget.provider;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.widget.R;
import com.widget.database.DatabaseManipulator;
import com.widget.model.MyTask;

public class MyWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

	private Context mContext = null;
	private ArrayList<MyTask> mTasks;

	public MyWidgetFactory(Context context, Intent intent) {
		mContext = context;
		initialize();
	}

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
		MyTask model = mTasks.get(position);
		RemoteViews row = new RemoteViews(mContext.getPackageName(),
				R.layout.one_list_row_layout);
		row.setTextViewText(R.id.m_row_label, model.toString());

		final Intent fillInIntent = new Intent();
		final Bundle extras = new Bundle();
		extras.putInt(ListWidget.EXTRA_TASK_ID, model.getId());
		fillInIntent.putExtras(extras);
		row.setOnClickFillInIntent(R.id.m_row_label, fillInIntent);

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
