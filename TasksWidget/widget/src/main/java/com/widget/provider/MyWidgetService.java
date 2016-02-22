package com.widget.provider;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class MyWidgetService extends RemoteViewsService {

    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetFactory(this.getApplicationContext(), intent);
    }
}
