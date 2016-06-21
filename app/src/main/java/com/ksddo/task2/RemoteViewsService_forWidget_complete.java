package com.ksddo.task2;

import android.content.Intent;

/**
 * Created by k_tetsuo on 2015/07/24.
 */
public class RemoteViewsService_forWidget_complete extends RemoteViewsService_forWidget {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        arrayList_tasks = AppWidget.arrayList_completeTasks;
        return new RemoteViewsFactory_forWidget();
    }
}
