package com.ksddo.task2;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by k_tetsuo on 2015/07/12.
 */
public class TaskApplication extends Application {
    private final String TAG = "DEBUG-APPLICATION";
    private Bitmap obj;

    @Override
    public void onCreate() {
        /** Called when the Application-class is first created. */
        Log.v(TAG,"--- onCreate() in ---");
    }

    @Override
    public void onTerminate() {
        /** This Method Called when this Application finished. */
        Log.v(TAG,"--- onTerminate() in ---");
    }

    public void setObj(Bitmap bmp){
        obj = bmp;
    }

    public Bitmap getObj(){
        return obj;
    }
}