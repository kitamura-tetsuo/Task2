package com.ksddo.task2;

/**
 * Created by k_tetsuo on 2015/08/04.
 */
import android.os.Bundle;
import android.util.Log;

import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;

import com.google.android.gms.wearable.DataEventBuffer;

import com.google.android.gms.wearable.DataMapItem;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.ksddo.mylibrary.ByteArray;
import com.ksddo.mylibrary.CalendarHandler;
import com.ksddo.mylibrary.TaskEvent;
import com.ksddo.mylibrary.WearConstants;

import org.apache.commons.lang.math.NumberUtils;

import java.util.Calendar;
import java.util.Random;


/**

 * Listens to DataItems and Messages from the local node.

 */

public class DataLayerListenerServiceInHost extends WearableListenerService implements ResultCallback<DataApi.DataItemResult> {


    /** ロゴのTag. */

    private static final String TAG = "WEAR";
    public static final CalendarHandler CALENDAR_HANDLER = new CalendarHandler();
    GoogleApiClient mGoogleApiClient;



    @Override

    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        //接続が完了した時に呼び出される
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        //一時的に切断された時の処理を記述する

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                }).build();

        mGoogleApiClient.connect();
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        if(!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        if (messageEvent.getPath().equals(WearConstants.WEAR_ACTION_SEND_CALENDER_DATA_REQUEST_FROM_WEAR))
        {
            SendCalenderDataToWear(true);
        }
        else if (messageEvent.getPath().equals(WearConstants.WEAR_ACTION_END_CALENDER_FROM_WEAR))
        {
            try
            {
                CALENDAR_HANDLER.endCalenderEvent(getApplicationContext(), (TaskEvent) ByteArray.toObject(messageEvent.getData()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if (messageEvent.getPath().equals(WearConstants.WEAR_ACTION_START_CALENDER_FROM_WEAR))
        {
            try
            {
                CALENDAR_HANDLER.startCalenderEvent(getApplicationContext(), (TaskEvent) ByteArray.toObject(messageEvent.getData()), true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            SendCalenderDataToWear(false);
        }
        else if (messageEvent.getPath().equals(WearConstants.WEAR_ACTION_ADD_NEW_ACTIVE_TASK_EVENT_FROM_WEAR))
        {
            try
            {
                TaskEvent taskEvent = new TaskEvent();
                taskEvent.title = (String) ByteArray.toObject(messageEvent.getData());
                CALENDAR_HANDLER.startCalenderEvent(getApplicationContext(), taskEvent, true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            SendCalenderDataToWear(true);
        }
    }

    private void SendCalenderDataToWear(Boolean force)
    {
        try {
            PutDataMapRequest dataMap = PutDataMapRequest.create(WearConstants.WEAR_ACTION_SEND_CALENDER_DATA);
            if(force)
            {
                dataMap.getDataMap().putLong("froce", Calendar.getInstance().getTimeInMillis());
                CALENDAR_HANDLER.readCalenderEvent(getApplicationContext());
            }
            dataMap.getDataMap().putByteArray(WearConstants.WEAR_ACTION_PARAM_KEY_ACTIVE_TASKS, ByteArray.fromObject(CalendarHandler.dataList_activeTasks));
            dataMap.getDataMap().putByteArray(WearConstants.WEAR_ACTION_PARAM_KEY_COMPLETE_TASKS, ByteArray.fromObject(CalendarHandler.dataList_completeTasks));
            dataMap.getDataMap().putByteArray(WearConstants.WEAR_ACTION_PARAM_KEY_INCOMPLETE_SCHEDULED_TASKS, ByteArray.fromObject(CalendarHandler.dataList_incompleteScheduledTasks));
//                dataMap.getDataMap().putString(WearConstants.WEAR_ACTION_PARAM_KEY_ACTIVE_TASKS + "R", "1" + new Random().nextInt());

            syncData(dataMap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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

                    String data = dataMapItem.getDataMap().getString(WearConstants.WEAR_ACTION_PARAM_KEY);



                    //Toast.makeText(this, "data:" + data,Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void syncData(PutDataMapRequest dataMap) {
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, dataMap.asPutDataRequest());
        pendingResult.setResultCallback(this);
/*
        PutDataMapRequest mapReq = PutDataMapRequest.create("/testapp");
        mapReq.getDataMap().putString("name", "shokai");
        mapReq.getDataMap().putString("url", "http://shokai.org");
        Wearable.DataApi.putDataItem(mGoogleApiClient, mapReq.asPutDataRequest());
        */
    }

    public void syncData(String action, String key, String value) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(action);
        dataMap.getDataMap().putString(key, value + new Random().nextInt());
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(mGoogleApiClient, request);
        pendingResult.setResultCallback(this);
    }

    @Override
    public void onResult(DataApi.DataItemResult dataItemResult) {

    }
}