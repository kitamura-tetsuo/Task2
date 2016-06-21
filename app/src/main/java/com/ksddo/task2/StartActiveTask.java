package com.ksddo.task2;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ksddo.mylibrary.CalendarHandler;
import com.ksddo.mylibrary.TaskEvent;


import org.apache.commons.lang.math.NumberUtils;

public class StartActiveTask extends ActionBarActivity {
    boolean isCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_active_task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_active_task, menu);

        Button button_add = (Button) findViewById(R.id.button_add);
        Button button_cancel = (Button) findViewById(R.id.button_cancel);

        button_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!isCalled) {
                    isCalled = true;
                    CalendarHandler calendarHandler = new CalendarHandler();
                    TaskEvent taskEvent = new TaskEvent();
                    taskEvent.title = ((EditText) findViewById(R.id.editText_title)).getText().toString();
                    taskEvent.durationMinute = NumberUtils.toInt(((EditText) findViewById(R.id.editText_title)).getText().toString(), 0);
                    calendarHandler.startCalenderEvent(getApplicationContext(), taskEvent, true);
                    AppWidget.notifyDataChange(getApplicationContext());
                    finish();
                }
                return true;
            }
        });

        button_cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return true;
            }
        });

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
}
