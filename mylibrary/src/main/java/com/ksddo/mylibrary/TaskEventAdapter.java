package com.ksddo.mylibrary;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by k_tetsuo on 2015/07/13.
 */
public class TaskEventAdapter extends BaseAdapter {
    public List<TaskEvent> dataList;
    public Context context;

    public TaskEventAdapter(List<TaskEvent> list, Context context) {
        dataList = list;
        this.context = context;
    }

    public void setList(List<TaskEvent> list) {
        dataList = list;
    }

    public void setContext(Activity a) {
        context = a;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent) {
        TextView textView_title;
        TextView textView_time;
        TextView textView_duration;
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.task_list_view, null);
        }
        TaskEvent task = (TaskEvent) getItem(position);
        if (task != null) {
            textView_title = (TextView) v.findViewById(R.id.textView_title);
            textView_title.setText("title");

            textView_title = (TextView) v.findViewById(R.id.textView_title);
            textView_time = (TextView) v.findViewById(R.id.textView_time);
            textView_duration = (TextView) v.findViewById(R.id.textView_duration);
            textView_title.setText(task.title);
            textView_time.setText(task.time);
            textView_duration.setText(task.duration);
            v.findViewById(R.id.frameLayout_calenderColor).setBackgroundColor(task.calenderColor);
        }
        return v;
    }
}
