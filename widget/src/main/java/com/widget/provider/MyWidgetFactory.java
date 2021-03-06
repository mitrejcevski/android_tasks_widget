package com.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.widget.R;
import com.widget.database.DBManipulator;
import com.widget.model.MyTask;
import com.widget.utils.AppSettings;

import java.util.ArrayList;
import java.util.Date;

public class MyWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext = null;

    private ArrayList<MyTask> mTasks;

    public MyWidgetFactory(Context context, Intent intent) {
        mContext = context;
        initialize();
    }

    private void initialize() {
        mTasks = DBManipulator.INSTANCE.getAllTasks(mContext);
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
            row.setTextViewText(R.id.rowLabelTextView, striked);
        } else {
            row.setTextViewText(R.id.rowLabelTextView, task.getName());
        }
        if (task.hasTimeAttached()) {
            Date date = task.getDateTime().getTime();
            row.setTextViewText(R.id.rowDateTextView,
                    AppSettings.FORMATTER.format(date));
        } else {
            row.setTextViewText(R.id.rowDateTextView, "");
        }
        // set an action on click on the row item
        final Intent fillInIntent = new Intent();
        final Bundle extras = new Bundle();
        extras.putInt(ListWidget.EXTRA_TASK_ID, task.getId());
        fillInIntent.putExtras(extras);
        row.setOnClickFillInIntent(R.id.rowLabelTextView, fillInIntent);
        row.setOnClickFillInIntent(R.id.rowDateTextView, fillInIntent);
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
