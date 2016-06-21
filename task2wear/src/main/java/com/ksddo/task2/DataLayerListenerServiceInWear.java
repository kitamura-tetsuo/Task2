package com.ksddo.task2;

/**
 * Created by k_tetsuo on 2015/08/04.
 */
import android.content.Intent;
import android.util.Log;

import android.widget.Toast;


import com.google.android.gms.wearable.DataEvent;

import com.google.android.gms.wearable.DataEventBuffer;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.ksddo.mylibrary.CalendarHandler;
import com.ksddo.mylibrary.WearConstants;


/**

 * Listens to DataItems and Messages from the local node.

 */

public class DataLayerListenerServiceInWear extends WearableListenerService{


    /** ロゴのTag. */

    private static final String TAG = "WEAR";

    public static final CalendarHandler CALENDAR_HANDLER = new CalendarHandler();



    @Override
    public void onCreate() {

        super.onCreate();

    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
    }

    @Override

    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent event : dataEvents) {

            if (event.getType() == DataEvent.TYPE_DELETED) {

                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());

            } else if (event.getType() == DataEvent.TYPE_CHANGED) {

                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());



                // Action名が同じ場合は、値を取得

                if(WearConstants.WEAR_ACTION_SEND_CALENDER_DATA.equals(event.getDataItem().getUri().getPath())){
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
//                    Toast.makeText(this, "data:",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, MainWearActivity.class);
                    intent.setAction("test");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);//新規起動の記述
                    intent.putExtra(WearConstants.WEAR_ACTION_PARAM_KEY_ACTIVE_TASKS, dataMapItem.getDataMap().getByteArray(WearConstants.WEAR_ACTION_PARAM_KEY_ACTIVE_TASKS));
                    intent.putExtra(WearConstants.WEAR_ACTION_PARAM_KEY_COMPLETE_TASKS, dataMapItem.getDataMap().getByteArray(WearConstants.WEAR_ACTION_PARAM_KEY_COMPLETE_TASKS));
                    intent.putExtra(WearConstants.WEAR_ACTION_PARAM_KEY_INCOMPLETE_SCHEDULED_TASKS, dataMapItem.getDataMap().getByteArray(WearConstants.WEAR_ACTION_PARAM_KEY_INCOMPLETE_SCHEDULED_TASKS));
                    MainWearActivity.startMainWearActivity(getApplicationContext(), intent, true);
                }

            }

        }

    }

}