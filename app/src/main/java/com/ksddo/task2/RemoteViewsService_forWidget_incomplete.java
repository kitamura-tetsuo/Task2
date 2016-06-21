package com.ksddo.task2;

import android.content.Intent;

/**
 * Created by k_tetsuo on 2015/07/22.
 */
public class RemoteViewsService_forWidget_incomplete extends RemoteViewsService_forWidget {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        arrayList_tasks = AppWidget.arrayList_incompleteScheduledTasks;
        return new RemoteViewsFactory_forWidget();
    }
}
