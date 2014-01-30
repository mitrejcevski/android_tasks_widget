package com.mitrejcevski.widget.provider;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Service for binding the remote views.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class MyWidgetService extends RemoteViewsService {
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new MyWidgetFactory(this.getApplicationContext(), intent);
	}
}
