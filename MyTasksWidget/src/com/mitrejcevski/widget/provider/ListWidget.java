package com.mitrejcevski.widget.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.activity.ListActivity;
import com.mitrejcevski.widget.activity.QuickTaskAdder;
import com.mitrejcevski.widget.database.DatabaseManipulator;

/**
 * The application widget.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class ListWidget extends AppWidgetProvider {

	public static String CLICK_ACTION = "com.widget.provider.CLICK";
	public static String ADD_ACTION = "com.widget.provider.ADD";
	public static String EXTRA_TASK_ID = "com.widget.provider.task";

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		// determine click on a task in the widget.
		if (action.equals(CLICK_ACTION)) {
			final int task = intent.getIntExtra(EXTRA_TASK_ID, -1);
			deleteTask(context, task);
		}
		// determine added new task.
		if (action.equals(ADD_ACTION)) {
			Toast.makeText(context, R.string.new_task_added_label,
					Toast.LENGTH_SHORT).show();
		}
		// notify the widget that an action has appeared.
		final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		final ComponentName cn = new ComponentName(context, ListWidget.class);
		mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
				R.id.widget_list);
		super.onReceive(context, intent);
	}

	/**
	 * Delete a task from the database.
	 * 
	 * @param context
	 * @param taskId
	 */
	private void deleteTask(Context context, int taskId) {
		DatabaseManipulator.INSTANCE.open(context);
		DatabaseManipulator.INSTANCE.deleteTask(taskId);
		DatabaseManipulator.INSTANCE.close();
	}

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int count = appWidgetIds.length;
		for (int i = 0; i < count; i++) {
			Intent intent = new Intent(context, MyWidgetService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			// setup remote views
			final RemoteViews widget = new RemoteViews(
					context.getPackageName(),
					R.layout.list_widget_provider_layout);
			widget.setRemoteAdapter(R.id.widget_list, intent);
			// set an empty view when the list in the widget is empty
			widget.setEmptyView(R.id.widget_list, R.id.empty_view);
			// an intent for add new task button on the widget.
			final Intent addIntent = new Intent(context, QuickTaskAdder.class);
			PendingIntent addNewClick = PendingIntent.getActivity(context, 0,
					addIntent, 0);
			widget.setOnClickPendingIntent(R.id.action_icon_add, addNewClick);
			// an intent for the widget header - open the app on click.
			final Intent appIntent = new Intent(context, ListActivity.class);
			PendingIntent headerClick = PendingIntent.getActivity(context, 0,
					appIntent, 0);
			widget.setOnClickPendingIntent(R.id.widget_header, headerClick);
			// setup on item click action.
			final Intent onClickIntent = new Intent(context, ListWidget.class);
			onClickIntent.setAction(ListWidget.CLICK_ACTION);
			onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			final PendingIntent onClickPendingIntent = PendingIntent
					.getBroadcast(context, 0, onClickIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			widget.setPendingIntentTemplate(R.id.widget_list,
					onClickPendingIntent);

			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
