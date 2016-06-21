package com.ksddo.task2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ksddo.mylibrary.CalendarHandler;
import com.ksddo.mylibrary.TaskEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link AppWidgetConfigureActivity AppWidgetConfigureActivity}
 */
public class AppWidget extends AppWidgetProvider {

    protected static ArrayList<TaskEvent> arrayList_activeTasks = new ArrayList<TaskEvent>();
    protected static ArrayList<TaskEvent> arrayList_incompleteScheduledTasks = new ArrayList<TaskEvent>();
    protected static ArrayList<TaskEvent> arrayList_incompleteUnscheduledTasks = new ArrayList<TaskEvent>();
    protected static ArrayList<TaskEvent> arrayList_completeTasks = new ArrayList<TaskEvent>();

    static CalendarHandler calendarHandler = new CalendarHandler();

    private static final String ACTION_ITEM_CLICK_ACTIVE_TASKS = "com.ksddo.task2.ACTION_ITEM_CLICK_ACTIVE_TASKS";
    private static final String ACTION_ITEM_CLICK_INCOMPLETE_SCHEDULED_TASKS = "com.ksddo.task2.ACTION_ITEM_CLICK_INCOMPLETE_SCHEDULED_TASKS";
    private static final String ACTION_ITEM_CLICK_INCOMPLETE_UNSCHEDULED_TASKS = "com.ksddo.task2.ACTION_ITEM_CLICK_INCOMPLETE_UNSCHEDULED_TASKS";
    private static final String ACTION_ITEM_CLICK_COMPLETE_TASKS = "com.ksddo.task2.ACTION_ITEM_CLICK_COMPLETE_TASKS";
    private static final String ACTION_BUTTON_ADD_CLICK = "com.ksddo.task2.ACTION_BUTTON_ADD_CLICK";
    private static final String ACTION_BUTTON_RELOAD_CLICK = "com.ksddo.task2.ACTION_BUTTON_RELOAD_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            AppWidgetConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        if (ACTION_ITEM_CLICK_ACTIVE_TASKS.equals(action)) {
            EndTaskEvent(context, intent.getIntExtra(RemoteViewsService_forWidget.EXTRA_POSITION, -1));
        } else if (ACTION_ITEM_CLICK_INCOMPLETE_SCHEDULED_TASKS.equals(action)) {
            StartTaskEvent(context, arrayList_incompleteScheduledTasks, intent.getIntExtra(RemoteViewsService_forWidget.EXTRA_POSITION, -1));
        } else if (ACTION_ITEM_CLICK_INCOMPLETE_UNSCHEDULED_TASKS.equals(action)) {
            StartTaskEvent(context, arrayList_incompleteUnscheduledTasks, intent.getIntExtra(RemoteViewsService_forWidget.EXTRA_POSITION, -1));
        } else if (ACTION_ITEM_CLICK_COMPLETE_TASKS.equals(action)) {
            StartTaskEvent(context, arrayList_completeTasks, intent.getIntExtra(RemoteViewsService_forWidget.EXTRA_POSITION, -1));
        } else if (action.equals(ACTION_BUTTON_ADD_CLICK)) {
            Intent i = new Intent();
            i.setClassName(context.getPackageName(), StartActiveTask.class.getName());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            context.startActivity(i);
        } else if (action.equals(ACTION_BUTTON_RELOAD_CLICK) || action.equals("android.appwidget.action.APPWIDGET_UPDATE_OPTIONS")) {
            callUpdate(context);
        }
    }

    private void callUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AppWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int i : appWidgetIds)
            updateAppWidget(context, appWidgetManager, i);
    }

    private void StartTaskEvent(Context context, ArrayList<TaskEvent> list, int position) {
        TaskEvent taskEvent = list.get(position);
        calendarHandler.startCalenderEvent(context, taskEvent, true);

        notifyDataChange(context);
    }

    private void EndTaskEvent(Context context, int index) {
        if (arrayList_activeTasks.size() <= index)
            return;

        TaskEvent t = arrayList_activeTasks.get(index);

        calendarHandler.endCalenderEvent(context, t);

        notifyDataChange(context);
    }

    public static void notifyDataChange(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AppWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView_activeTasks);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView_completeTasks);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView_incompleteScheduledTasks);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView_incompleteUnscheduledTasks);
        CalculateEndTime(context, appWidgetManager);
    }

    private static void CalculateEndTime(Context context, AppWidgetManager appWidgetManager) {
        Calendar calendar = Calendar.getInstance();
        for (TaskEvent event : arrayList_incompleteScheduledTasks) {
            calendar.add(Calendar.MINUTE, event.durationMinute);
        }
        for (TaskEvent event : arrayList_activeTasks) {
            calendar.add(Calendar.MINUTE, event.durationMinute);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.widgetTextView_activeTasks, "end with: " + new SimpleDateFormat("HH:mm").format(calendar.getTime()));
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AppWidget.class.getName());
        appWidgetManager.updateAppWidget(thisAppWidget, views);
    }


    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        CharSequence widgetText = AppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object

        calendarHandler.LoadData(context);

        arrayList_activeTasks.clear();
        arrayList_incompleteScheduledTasks.clear();
        arrayList_completeTasks.clear();

        calendarHandler.readCalenderEvent(context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setRemoteAdapter(R.id.widgetListView_activeTasks, createIntentForListView(context, "arrayList_activeTasks", RemoteViewsService_forWidget_activeTasks.class));
        views.setRemoteAdapter(R.id.widgetListView_completeTasks, createIntentForListView(context, "arrayList_completeTasks", RemoteViewsService_forWidget_complete.class));
        views.setRemoteAdapter(R.id.widgetListView_incompleteScheduledTasks, createIntentForListView(context, "arrayList_completeTasks", RemoteViewsService_forWidget_incomplete.class));
/*
        Intent remoteViewsFactoryIntent = new Intent(context, RemoteViewsService_forWidget_incomplete.class);
        remoteViewsFactoryIntent.putExtra("ArrayList<TaskEvent>_Name", "arrayList_incompleteScheduledTasks");
        views.setRemoteAdapter(R.id.widgetListView_incompleteScheduledTasks, remoteViewsFactoryIntent);
*/
        setOnItemSelectedPendingIntent(context, views, ACTION_ITEM_CLICK_ACTIVE_TASKS, R.id.widgetListView_activeTasks);
        setOnItemSelectedPendingIntent(context, views, ACTION_ITEM_CLICK_INCOMPLETE_SCHEDULED_TASKS, R.id.widgetListView_incompleteScheduledTasks);
        setOnItemSelectedPendingIntent(context, views, ACTION_ITEM_CLICK_INCOMPLETE_UNSCHEDULED_TASKS, R.id.widgetListView_incompleteUnscheduledTasks);
        setOnItemSelectedPendingIntent(context, views, ACTION_ITEM_CLICK_COMPLETE_TASKS, R.id.widgetListView_completeTasks);
        setOnButtonClickPendingIntent(context, views, appWidgetId, ACTION_BUTTON_RELOAD_CLICK, R.id.imageButton_reload);
        setOnButtonClickPendingIntent(context, views, appWidgetId, ACTION_BUTTON_ADD_CLICK, R.id.imageButton_add);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetListView_activeTasks);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetListView_completeTasks);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetListView_incompleteScheduledTasks);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetListView_incompleteUnscheduledTasks);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        CalculateEndTime(context, appWidgetManager);
    }

    private static Intent createIntentForListView(Context context, String arrayListName, Class cls) {
        Intent remoteViewsFactoryIntent = new Intent(context, cls);
        remoteViewsFactoryIntent.putExtra("ArrayList<TaskEvent>_Name", arrayListName);
        return remoteViewsFactoryIntent;
    }

    private void setOnItemSelectedPendingIntent(Context ctx, RemoteViews rv, String action, int id) {
        Intent itemClickIntent = new Intent(ctx, AppWidget.class);
        itemClickIntent.setAction(action);

        PendingIntent itemClickPendingIntent = PendingIntent.getBroadcast(
                ctx,
                0,
                itemClickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        rv.setPendingIntentTemplate(id, itemClickPendingIntent);
    }

    private void setOnButtonClickPendingIntent(Context ctx, RemoteViews rv, int appWidgetId, String action, int buttonID) {
        Intent btnClickIntent = new Intent(action);
        btnClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent btnClickPendingIntent = PendingIntent.getBroadcast(
                ctx,
                0,
                btnClickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        rv.setOnClickPendingIntent(buttonID, btnClickPendingIntent);
    }

    public static void CallUpdade(Context context)
    {
        calendarHandler.LoadData(context);

        arrayList_activeTasks.clear();
        arrayList_incompleteScheduledTasks.clear();
        arrayList_completeTasks.clear();

        calendarHandler.readCalenderEvent(context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AppWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView_activeTasks);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView_completeTasks);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView_incompleteScheduledTasks);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView_incompleteUnscheduledTasks);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetIds, views);
        /*
        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE_OPTIONS");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AppWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int appWidgetId : appWidgetIds) {
            PendingIntent operation = PendingIntent.getBroadcast(
                    context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        */
    }
}

