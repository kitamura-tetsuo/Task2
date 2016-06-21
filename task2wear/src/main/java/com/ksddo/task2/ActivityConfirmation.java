package com.ksddo.task2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

public class ActivityConfirmation extends Activity implements DelayedConfirmationView.DelayedConfirmationListener
{

    private TextView mTextView;
    public static final String INTENT_EXTRA_TITEL_NAME = "INTENT_EXTRA_TITEL_NAME";
    public static final String INTENT_EXTRA_ACTION = "INTENT_EXTRA_ACTION";
    public static final String INTENT_EXTRA_ACTION_RETRY = "INTENT_EXTRA_ACTION_RETRY";
    public static final String INTENT_EXTRA_ACTION_CANCEL = "INTENT_EXTRA_ACTION_CANCEL";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_confirmation);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener()
        {
            @Override
            public void onLayoutInflated(WatchViewStub stub)
            {
                mTextView = (TextView) stub.findViewById(R.id.textView_title);
                mTextView.setText(getIntent().getStringExtra(getIntent().getStringExtra(INTENT_EXTRA_TITEL_NAME)));

                DelayedConfirmationView delayedConfirmationView = (DelayedConfirmationView) findViewById(R.id.delayed_confirmation_view_cancel);
                delayedConfirmationView.setTotalTimeMs(2 * 1000);
                delayedConfirmationView.setListener(ActivityConfirmation.this);
                delayedConfirmationView.start();

                ((DelayedConfirmationView) findViewById(R.id.delayed_confirmation_view_retry)).setListener(ActivityConfirmation.this);
            }
        });
    }

    @Override
    public void onTimerFinished(View view)
    {
        view.toString();
        if (!isFinishing() && !isDestroyed())
        {
            // 返すデータ(Intent&Bundle)の作成
/*
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("key.StringData", "送り返す文字列");
        bundle.putInt("key.intData", 123456789);
        data.putExtras(bundle);
*/
            // setResult() で bundle を載せた
            // 送るIntent dataをセットする

            // 第一引数は…Activity.RESULT_OK,
            // Activity.RESULT_CANCELED など
            Intent data = new Intent();
            data.putExtra(getIntent().getStringExtra(INTENT_EXTRA_TITEL_NAME), getIntent().getStringExtra(getIntent().getStringExtra(INTENT_EXTRA_TITEL_NAME)));
            setResult(RESULT_OK, data);

            // finish() で終わらせて
            // Intent data を送る
            finish();
        }
    }

    @Override
    public void onTimerSelected(View view)
    {
        Intent data = new Intent();
        if(view.getId() == R.id.delayed_confirmation_view_retry)
        {
            data.putExtra(INTENT_EXTRA_ACTION, INTENT_EXTRA_ACTION_RETRY);
        }
        if(view.getId() == R.id.delayed_confirmation_view_cancel)
        {
            data.putExtra(INTENT_EXTRA_ACTION, INTENT_EXTRA_ACTION_CANCEL);
        }
        setResult(RESULT_CANCELED, data);
        finish();
    }
}
