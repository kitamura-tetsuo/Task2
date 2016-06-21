package com.ksddo.task2;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class ServiceForWidget extends Service {
    public ServiceForWidget() {
    }

    private final String BUTTON_CLICK_ACTION = "BUTTON_CLICK_ACTION";

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
