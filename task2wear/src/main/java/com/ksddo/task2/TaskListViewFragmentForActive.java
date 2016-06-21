package com.ksddo.task2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.ksddo.mylibrary.TaskEventAdapter;

/**
 * Created by k_tetsuo on 2015/08/16.
 */
public class TaskListViewFragmentForActive extends TaskListViewFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ImageButton b = (ImageButton) view.findViewById(R.id.imageButton_add);
        b.setOnClickListener(this);

        return view;
    }

    @Override
    public View inflateView(LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.fragment_task_list_view_for_active, container, false);
    }

}
