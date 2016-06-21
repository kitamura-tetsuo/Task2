package com.ksddo.task2;

import android.content.Intent;

/**
 * Created by k_tetsuo on 2015/07/24.
 */
public class RemoteViewsService_forWidget_activeTasks extends RemoteViewsService_forWidget {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        arrayList_tasks = AppWidget.arrayList_activeTasks;
        return new RemoteViewsFactory_forWidget();
    }
}
