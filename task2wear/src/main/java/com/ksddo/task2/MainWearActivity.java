package com.ksddo.task2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v13.app.FragmentPagerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.DataEventBuffer;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataItem;
import com.ksddo.mylibrary.ByteArray;
import com.ksddo.mylibrary.CalendarHandler;
import com.ksddo.mylibrary.TaskEvent;
import com.ksddo.mylibrary.TaskEventAdapter;
import com.ksddo.mylibrary.WearConstants;

public class MainWearActivity extends Activity
{

    private TextView mTextView;

    private ViewPager viewPager;
    private ListView listView_task;

    GoogleApiClient mGoogleApiClient;
    DataListener mDataListener;

    final ArrayList<TaskEvent> dataList_activeTasks = new ArrayList<TaskEvent>();
    final ArrayList<TaskEvent> dataList_incompleteScheduledTasks = new ArrayList<TaskEvent>();
    final ArrayList<TaskEvent> dataList_completeTasks = new ArrayList<TaskEvent>();

    final TaskListViewFragmentForActive fragment_activeTasks = new TaskListViewFragmentForActive();
    final TaskListViewFragment fragment_incompleteScheduledTasks = new TaskListViewFragment();
    final TaskListViewFragment fragment_completeTasks = new TaskListViewFragment();

    final String TITLE_ACTIVE = "active";
    final String TITLE_INCOMPLETE = "incomplete";
    final String TITLE_COMPLETE = "complete";

    final static String ACTION_START_PAGE = "ACTION_START_PAGE";

    String DATA_API_PATH = "/data/feed/api/";

    static Timer mTimer = null;
    Handler mHandler = new Handler();
    private Vibrator myVib;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        setContentView(com.ksddo.task2.R.layout.activity_main_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(com.ksddo.task2.R.id.watch_view_stub);

        LoadData(getApplicationContext());

        ReadCalenderTasksFromIntent(getIntent());


        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener()
        {
            @Override
            public void onLayoutInflated(WatchViewStub stub)
            {
                mTextView = (TextView) stub.findViewById(com.ksddo.task2.R.id.text);

                viewPager = (ViewPager) stub.findViewById(com.ksddo.task2.R.id.pager);
                PagerAdapter adapter = new PagerAdapter(getFragmentManager());
                {
                    Bundle args = new Bundle();
                    args.putString(TaskListViewFragment.PAGE_TITLE, TITLE_ACTIVE);
                    fragment_activeTasks.setArguments(args);
                    adapter.addFragment(fragment_activeTasks);
                }
                {
                    Bundle args = new Bundle();
                    args.putString(TaskListViewFragment.PAGE_TITLE, TITLE_INCOMPLETE);
                    fragment_incompleteScheduledTasks.setArguments(args);
                    adapter.addFragment(fragment_incompleteScheduledTasks);
                }
                {
                    Bundle args = new Bundle();
                    args.putString(TaskListViewFragment.PAGE_TITLE, TITLE_COMPLETE);
                    fragment_completeTasks.setArguments(args);
                    adapter.addFragment(fragment_completeTasks);
                }
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(1, false);
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(Wearable.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
        {
            @Override
            public void onConnected(Bundle bundle)
            {
                //接続が完了した時に呼び出される
            }

            @Override
            public void onConnectionSuspended(int cause)
            {
                //一時的に切断された時の処理を記述する

            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener()
        {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult)
            {

            }
        }).build();

        mDataListener = new DataListener()
        {
            @Override
            public void onDataChanged(DataEventBuffer dataEvents)
            {
                Toast.makeText(getApplicationContext(), "data:", Toast.LENGTH_LONG).show();

                for (DataEvent event : dataEvents)
                {
                    if (!TextUtils.equals(DATA_API_PATH, event.getDataItem().getUri().getPath()))
                    {
                        continue;
                    }

                    switch (event.getType())
                    {
                        case DataEvent.TYPE_CHANGED:
                            DataItem item = event.getDataItem();
                            byte[] bytes = item.getData();
                            //追加、変更
                            break;
                        case DataEvent.TYPE_DELETED:
                            //削除
                            break;
                    }
                }

            }
        };

        if (!mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.connect();
//            Wearable.DataApi.addListener(mGoogleApiClient, mDataListener);
//            Wearable.DataApi.addListener(mGoogleApiClient, new  DataLayerListenerServiceInWear());

        }
        sendMessage_WhenErrorRepeat(WearConstants.WEAR_ACTION_SEND_CALENDER_DATA_REQUEST_FROM_WEAR, new byte[0]);
        stopLayerService();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        if(intent.getIntExtra(ACTION_START_PAGE, -1) != -1)
        {
            viewPager.setCurrentItem(intent.getIntExtra(ACTION_START_PAGE, -1), false);
            intent.removeExtra(ACTION_START_PAGE);
        }

        ReadCalenderTasksFromIntent(intent);
        stopLayerService();
    }

    private void ReadCalenderTasksFromIntent(Intent intent)
    {
        try
        {
            ArrayList<TaskEvent> temp_dataList_activeTasks = (ArrayList<TaskEvent>) ByteArray.toObject(intent.getByteArrayExtra(WearConstants.WEAR_ACTION_PARAM_KEY_ACTIVE_TASKS));
            ArrayList<TaskEvent> temp_dataList_incompleteScheduledTasks = (ArrayList<TaskEvent>) ByteArray.toObject(intent.getByteArrayExtra(WearConstants.WEAR_ACTION_PARAM_KEY_INCOMPLETE_SCHEDULED_TASKS));
            ArrayList<TaskEvent> temp_dataList_completeTasks = (ArrayList<TaskEvent>) ByteArray.toObject(intent.getByteArrayExtra(WearConstants.WEAR_ACTION_PARAM_KEY_COMPLETE_TASKS));

            dataList_activeTasks.clear();
            dataList_incompleteScheduledTasks.clear();
            dataList_completeTasks.clear();

            dataList_activeTasks.addAll(temp_dataList_activeTasks);
            dataList_incompleteScheduledTasks.addAll(temp_dataList_incompleteScheduledTasks);
            dataList_completeTasks.addAll(temp_dataList_completeTasks);

            fragment_activeTasks.notifyDataSetChanged();
            fragment_incompleteScheduledTasks.notifyDataSetChanged();
            fragment_completeTasks.notifyDataSetChanged();

            lastCacheMin = -1;
        }
        catch (Exception e)
        {
        }
    }

    private void startTimer()
    {
        stopTimer();

        mTimer = new Timer(true);
        mTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                final TextView tv = ((TextView) findViewById(R.id.textView_header));
                if (tv != null)
                {
                    final String s = CalculateEndTime();
                    // mHandlerを通じてUI Threadへ処理をキューイング
                    mHandler.post(new Runnable()
                    {
                        public void run()
                        {
                            tv.setText(s);
                        }
                    });
                }
            }
        }, 100, 1 * 1000);
    }

    private void stopTimer()
    {
        if (mTimer != null)
        {
            mTimer.cancel();
            mTimer = null;
        }
    }

    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d  HH:mm:ss");
    String endTimeCash = "";
    int lastCacheMin = -1;

    private String CalculateEndTime()
    {
        Calendar calendar = Calendar.getInstance();
        if (lastCacheMin != calendar.get(Calendar.MINUTE))
        {
            endTimeCash = CalendarHandler.CalculateEndTime(dataList_activeTasks, dataList_incompleteScheduledTasks);
        }
        lastCacheMin = calendar.get(Calendar.MINUTE);
        return simpleDateFormat.format(calendar.getTime()) + "    - " + endTimeCash;
    }

    public static void startMainWearActivity(Context context, Intent intent, Boolean forUpdate)
    {
        mIsUpdatingByIntent = forUpdate;
        context.startActivity(intent);
    }

    static boolean mIsUpdatingByIntent = false;

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!mIsUpdatingByIntent)
        {
            startTimer();
            stopLayerService();
        }
        mIsUpdatingByIntent = false;
    }

    static boolean mIsRunnningLayerService = false;

    public static void startLayerService(Context context)
    {
        if (!mIsRunnningLayerService)
        {
            mIsRunnningLayerService = true;
            context.startService(new Intent(context, LayerService.class));
        }
    }

    private void stopLayerService()
    {
        mIsRunnningLayerService = false;
        stopService(new Intent(getApplicationContext(), LayerService.class));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (!mIsUpdatingByIntent)
        {
            stopTimer();
            startLayerService(getApplicationContext());
            SaveData(getApplicationContext());
        }
    }

    public ArrayList<TaskEvent> getArrayList(String title)
    {
        if (title == TITLE_ACTIVE)
            return dataList_activeTasks;
        if (title == TITLE_COMPLETE)
            return dataList_completeTasks;
        if (title == TITLE_INCOMPLETE)
            return dataList_incompleteScheduledTasks;
        return null;
    }

    public void onItemClick(TaskListViewFragment fragment, AdapterView<?> parent, View view, final int position, long id)
    {
        myVib.vibrate(50);
        if (fragment == fragment_activeTasks)
        {
            EndTaskEvent(position);
        }
        else if (fragment == fragment_incompleteScheduledTasks)
        {
            StartTaskEvent(fragment_incompleteScheduledTasks.mTaskEventAdapter, position, true);
        }
        else if (fragment == fragment_completeTasks)
        {
            StartTaskEvent(fragment_completeTasks.mTaskEventAdapter, position, false);
        }
    }

    private void EndTaskEvent(final int position)
    {
        final TaskEvent taskEvent = dataList_activeTasks.remove(position);
        if (fragment_activeTasks.mTaskEventAdapter != null)
            fragment_activeTasks.mTaskEventAdapter.notifyDataSetChanged();

        dataList_completeTasks.add(taskEvent);
        if (fragment_completeTasks.mTaskEventAdapter != null)
            fragment_completeTasks.mTaskEventAdapter.notifyDataSetChanged();

        sendMessage_WhenErrorRepeat(WearConstants.WEAR_ACTION_END_CALENDER_FROM_WEAR, taskEvent);
    }

    private void StartTaskEvent(final TaskEventAdapter taskEventAdapter, final int position, boolean removeEventFromList)
    {
        viewPager.setCurrentItem(0, true);

        final TaskEvent taskEvent = (TaskEvent) taskEventAdapter.getItem(position);
        if (removeEventFromList)
        {
            taskEventAdapter.dataList.remove(taskEvent);
            taskEventAdapter.notifyDataSetChanged();
        }
        dataList_activeTasks.add(taskEvent);
        if (fragment_activeTasks.mTaskEventAdapter != null)
            fragment_activeTasks.mTaskEventAdapter.notifyDataSetChanged();

        sendMessage_WhenErrorRepeat(WearConstants.WEAR_ACTION_START_CALENDER_FROM_WEAR, taskEvent);
    }

    private void sendMessage_WhenErrorRepeat(final String message, final Object o)
    {
        try
        {
            sendMessage_WhenErrorRepeat(message, ByteArray.fromObject(o));
        }
        catch (Exception e)
        {
        }
    }


    private void sendMessage_WhenErrorRepeat(final String message, final byte[] byteArray)
    {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                boolean isSend = false;
                for (Node node : Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await().getNodes())
                {
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), message, byteArray);
                        isSend = true;
                }
                if(!isSend)
                {
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            sendMessage_WhenErrorRepeat(message, byteArray);
                        }
                    }, 60000);
                }

                return null; // ここでreturnしたオブジェクトがonPostExecute()に渡される
            }
        };
        task.execute(); // パラメータを渡す
        lastCacheMin = -1;
    }

    public void LoadData(Context context)
    {
        try (FileInputStream fis = context.openFileInput("SaveData.dat"))
        {
            ObjectInputStream ois = new ObjectInputStream(fis);

/*            hashSet_activatedTaskIDs.addAll((HashSet<Long>) ois.readObject());
            Calendar calenderLastWrite = (Calendar)  ois.readObject();
            if(Calendar.getInstance().get(Calendar.DAY_OF_YEAR) != calenderLastWrite.get(Calendar.DAY_OF_YEAR))
                hashSet_activatedTaskIDs.clear();
                */

            dataList_activeTasks.clear();
            dataList_incompleteScheduledTasks.clear();
            dataList_completeTasks.clear();
            dataList_activeTasks.addAll((ArrayList<TaskEvent>) ois.readObject());
            dataList_incompleteScheduledTasks.addAll((ArrayList<TaskEvent>) ois.readObject());
            dataList_completeTasks.addAll((ArrayList<TaskEvent>) ois.readObject());
        }
        catch (Exception e)
        {
        }
    }

    public void SaveData(Context context)
    {
        try (FileOutputStream fos = context.openFileOutput("SaveData.dat", context.MODE_PRIVATE))
        {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            /*
            oos.writeObject(hashSet_activatedTaskIDs);
            oos.writeObject(Calendar.getInstance());
            */
            oos.writeObject(dataList_activeTasks);
            oos.writeObject(dataList_incompleteScheduledTasks);
            oos.writeObject(dataList_completeTasks);
        }
        catch (Exception e)
        {
        }
        /**/
    }

    private class PagerAdapter extends FragmentPagerAdapter
    {
        List<Fragment> fragmentList = null;

        public PagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
            fragmentList = new ArrayList<Fragment>();
        }

        @Override
        public Fragment getItem(int position)
        {
            return fragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment)
        {
            fragmentList.add(fragment);
            notifyDataSetChanged();
        }
    }


    private static final int SPEECH_REQUEST_CODE = 0;

    // 音声入力activityを起動できるActivityを作成する
    public void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// テキストを入力させるActivityを開始する。
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    public static final int REQUEST_CODE_ADD_NEW_ACTIVE_TASK_EVENT = 1;
    public static final String INTENT_EXTRA_ADD_NEW_ACTIVE_TASK_EVENT_TITLE = "INTENT_EXTRA_ADD_NEW_ACTIVE_TASK_EVENT_TITLE";

    // 音声入力から戻ってきたらこのコールバックが発行されます。
// ここでIntentとテキストの入力内容が入ったIntentのextractを処理します。
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            final String spokenText = results.get(0);

            Intent intent = new Intent(this, ActivityConfirmation.class);
            intent.putExtra(ActivityConfirmation.INTENT_EXTRA_TITEL_NAME, INTENT_EXTRA_ADD_NEW_ACTIVE_TASK_EVENT_TITLE);
            intent.putExtra(INTENT_EXTRA_ADD_NEW_ACTIVE_TASK_EVENT_TITLE, spokenText);
            startActivityForResult(intent, REQUEST_CODE_ADD_NEW_ACTIVE_TASK_EVENT);
        }
        else if (requestCode == REQUEST_CODE_ADD_NEW_ACTIVE_TASK_EVENT && resultCode == RESULT_CANCELED)
        {
            if(data.getStringExtra(ActivityConfirmation.INTENT_EXTRA_ACTION).equals(ActivityConfirmation.INTENT_EXTRA_ACTION_RETRY))
                displaySpeechRecognizer();
        }
        else if (requestCode == REQUEST_CODE_ADD_NEW_ACTIVE_TASK_EVENT && resultCode == RESULT_OK)
        {
            final String spokenText = data.getStringExtra(INTENT_EXTRA_ADD_NEW_ACTIVE_TASK_EVENT_TITLE);
            sendMessage_WhenErrorRepeat(WearConstants.WEAR_ACTION_ADD_NEW_ACTIVE_TASK_EVENT_FROM_WEAR, spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
