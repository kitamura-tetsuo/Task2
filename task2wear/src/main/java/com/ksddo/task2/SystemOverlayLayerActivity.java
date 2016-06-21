package com.ksddo.task2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by k_tetsuo on 2015/08/10.
 */
public class SystemOverlayLayerActivity extends Activity
{
    Button start_button;
    Button stop_button;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        start_button = (Button) findViewById(R.id.start_button);
        stop_button = (Button) findViewById(R.id.stop_button);

        final View.OnClickListener onStartButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(SystemOverlayLayerActivity.this, LayerService.class));
            }
        };
        start_button.setOnClickListener(onStartButton);

        final View.OnClickListener onStopButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(SystemOverlayLayerActivity.this, LayerService.class));
            }
        };
        stop_button.setOnClickListener(onStopButton);
    }
}