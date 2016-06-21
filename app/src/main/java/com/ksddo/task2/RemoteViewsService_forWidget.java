package com.ksddo.task2;

import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ksddo.mylibrary.TaskEvent;

import java.util.ArrayList;

/**
 * Created by k_tetsuo on 2015/07/24.
 */
public abstract class RemoteViewsService_forWidget extends RemoteViewsService {
    ArrayList<TaskEvent> arrayList_tasks;
    public static final String EXTRA_POSITION = "extra_position";


    public class RemoteViewsFactory_forWidget implements RemoteViewsFactory {

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }


        @Override
        public int getCount() {
            return arrayList_tasks.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if(arrayList_tasks.size() <= position)
                return null;

            TaskEvent task = arrayList_tasks.get(position);

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.task_list_view);
            remoteViews.setTextViewText(R.id.textView_title, task.title);
            remoteViews.setTextViewText(R.id.textView_time, task.time);
            remoteViews.setTextViewText(R.id.textView_duration, task.duration);
            remoteViews.setInt(R.id.frameLayout_calenderColor, "setBackgroundColor", task.calenderColor);


            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(EXTRA_POSITION, position);
//            fillInIntent.setData(browserUri);

            remoteViews.setOnClickFillInIntent(R.id.textView_title, fillInIntent);

            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
