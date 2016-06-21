package com.ksddo.task2;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.ksddo.mylibrary.ByteArray;
import com.ksddo.mylibrary.CalendarHandler;
import com.ksddo.mylibrary.TaskEventAdapter;
import com.ksddo.mylibrary.TaskEvent;
import com.ksddo.mylibrary.WearConstants;


public class MainActivity extends ActionBarActivity implements ResultCallback<DataApi.DataItemResult> {

    private ListView listView_activeTasks;
    private ListView listView_incompleteScheduledTasks;
    private ListView listView_completeTasks;

    String DATA_API_PATH = "/data/feed/api/";
    String DATA_API_EXTRA_KEY = "/data/feed/api/";

    TaskEventAdapter adapter_activeTasks;
    TaskEventAdapter adapter_incompleteScheduledTasks;
    TaskEventAdapter adapter_completeTasks;
    TextView textView_activeTasks;
    GoogleApiClient mGoogleApiClient;

    CalendarHandler calendarHandler = new CalendarHandler();

    private void assignViews() {
        listView_activeTasks = (ListView) findViewById(R.id.listView_activeTasks);
        listView_incompleteScheduledTasks = (ListView) findViewById(R.id.listView_incompleteScheduledTasks);
        listView_completeTasks = (ListView) findViewById(R.id.listView_completeTasks);
        textView_activeTasks = (TextView) findViewById(R.id.textView_activeTasks);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();

        adapter_activeTasks = new TaskEventAdapter(calendarHandler.dataList_activeTasks, this);
        adapter_incompleteScheduledTasks = new TaskEventAdapter(calendarHandler.dataList_incompleteScheduledTasks, this);
        adapter_completeTasks = new TaskEventAdapter(calendarHandler.dataList_completeTasks, this);

        listView_activeTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EndTaskEvent(position);
            }
        });

        listView_incompleteScheduledTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StartTaskEvent((ListView) parent, position, true);
            }
        });

        listView_completeTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StartTaskEvent((ListView) parent, position, false);
            }
        });

        listView_activeTasks.setOnTouchListener(new OnSwipeTouchListener() {

            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom() {
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });

        if(mGoogleApiClient == null) {
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


        ((Button) findViewById(R.id.button)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
/*                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);*/

                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                        .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                            @Override
                            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                                for (Node node : nodes.getNodes()) {
                                    //ノードに対する処理を記述する
                                    //データの送信処理で、node.getId();が必要になる。
                                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), WearConstants.WEAR_ACTION_SEND_CALENDER_DATA, new byte[0]);
                                }
                            }
                        });


//                syncData(WearConstants.WEAR_ACTION_SEND_CALENDER_DATA,                 WearConstants.WEAR_ACTION_PARAM_KEY, "test");

                /*
                Asset asset = Asset.createFromBytes("Android".getBytes());

                final PutDataRequest req = PutDataRequest.create(DATA_API_PATH);
                req.putAsset(DATA_API_EXTRA_KEY,asset);
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for (Node node : nodes.getNodes()) {
                    DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleApiClient,req).await();

                }
                */
                return true;
            }
        });




        /*
        String[] members2 = { "dataList_incompleteScheduledTasks", "rongon_xp", "kacchi0516", "kobashinG",
                "seit", "kei_i_t", "furusin_oriver", "mhidaka", "rongon_xp", "kacchi0516", "kobashinG",
                "seit", "kei_i_t", "furusin_oriver" };
        for (int i = 0; i < members2.length; i++) {
            dataList_incompleteScheduledTasks.add(new TaskEvent(members2[i], "", ""));
        }*/
        /*
        String[] members3 = { "dataList_completeTasks", "rongon_xp", "kacchi0516", "kobashinG",
                "seit", "kei_i_t", "furusin_oriver", "mhidaka", "rongon_xp", "kacchi0516", "kobashinG",
                "seit", "kei_i_t", "furusin_oriver" };
        for (int i = 0; i < members3.length; i++) {
            dataList_completeTasks.add(new TaskEvent(members3[i], "", ""));
        }*/

        calendarHandler.readCalenderEvent(this);

        listView_activeTasks.setAdapter(adapter_activeTasks);
        listView_incompleteScheduledTasks.setAdapter(adapter_incompleteScheduledTasks);
        listView_completeTasks.setAdapter(adapter_completeTasks);

        textView_activeTasks.setText(calendarHandler.CalculateEndTime());
    }


    /**

     * Wearにデータを送信

     *

     * @param action アクション名

     * @param key キー

     * @param value バリュー

     */

    public void syncData(String action, String key, String value) {

        PutDataMapRequest dataMap = PutDataMapRequest.create(action);

        dataMap.getDataMap().putString(key, value + new Random().nextInt());
        PutDataRequest request = dataMap.asPutDataRequest();

        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi

                .putDataItem(mGoogleApiClient, request);

        pendingResult.setResultCallback(this);
    }


    private void SendCalenderDataToWear()
    {
        try {
            PutDataMapRequest dataMap = PutDataMapRequest.create(WearConstants.WEAR_ACTION_SEND_CALENDER_DATA);
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

    private void StartTaskEvent(ListView listView, int position, boolean removeEventFromList) {
        TaskEvent taskEvent = (TaskEvent) listView.getItemAtPosition(position);

        calendarHandler.startCalenderEvent(this, taskEvent, removeEventFromList);

        TaskEventAdapter adapter = (TaskEventAdapter) listView.getAdapter();
        if(removeEventFromList) {
            adapter.notifyDataSetChanged();
        }

        adapter_activeTasks.notifyDataSetChanged();

        AppWidget.CallUpdade(this);
        SendCalenderDataToWear();
    }

    private void EndTaskEvent(int index) {
        TaskEventAdapter taskEventAdapter =(TaskEventAdapter)listView_activeTasks.getAdapter();
        TaskEvent t = taskEventAdapter.dataList.get(index);

        calendarHandler.endCalenderEvent(this, t);

        taskEventAdapter.notifyDataSetChanged();
        adapter_completeTasks.notifyDataSetChanged();

        AppWidget.CallUpdade(this);
        SendCalenderDataToWear();
    }

    @Override
    public void onPause(){
        super.onPause();

        calendarHandler.SaveData(this);
    }

    @Override
    public void onResume(){
        super.onResume();

        calendarHandler.LoadData(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(DataApi.DataItemResult dataItemResult) {

    }
}
