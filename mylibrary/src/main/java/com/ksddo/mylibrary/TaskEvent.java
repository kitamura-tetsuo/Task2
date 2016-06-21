package com.ksddo.mylibrary;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ksddo.mylibrary.Duration;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.format.DateFormat;

/**
 * Created by k_tetsuo on 2015/07/13.
 */
public class TaskEvent implements Serializable {
    public String title;
    public String time;
    public String duration;
    public int durationMinute;
    public String uri_forEvent;
    public int id_forExtendedProperties;
    public long instanceID;
    public long startTimeInMillis;
    public long endTimeInMillis;
    public int calenderColor;

    public TaskEvent() {

    }

    public TaskEvent(Cursor c, Uri uri_forEvent) {
        this.title = c.getString(c.getColumnIndex(Events.TITLE));
        instanceID = c.getLong(c.getColumnIndex(CalendarContract.Instances._ID));

        calenderColor = c.getInt(c.getColumnIndex(CalendarContract.Instances.CALENDAR_COLOR));

        Calendar calendarBeginTime = Calendar.getInstance();
        calendarBeginTime.setTimeInMillis(c.getLong(c.getColumnIndex(CalendarContract.Instances.BEGIN)));

        Calendar calendarEndTime = Calendar.getInstance();
        calendarEndTime.setTimeInMillis(c.getLong(c.getColumnIndex(CalendarContract.Instances.END)));

        updateTime(calendarBeginTime, calendarEndTime);
        this.uri_forEvent = uri_forEvent.toString();
    }

    private void updateTime(Calendar calendarBeginTime, Calendar calendarEndTime) {
        startTimeInMillis = calendarBeginTime.getTimeInMillis();
        endTimeInMillis = calendarEndTime.getTimeInMillis();

        Calendar calendarNowTime = Calendar.getInstance();

        if(calendarNowTime.get(Calendar.YEAR) == calendarBeginTime.get(Calendar.YEAR) && calendarNowTime.get(Calendar.DAY_OF_YEAR) == calendarBeginTime.get(Calendar.DAY_OF_YEAR))
            time = DateFormat.format("kk:mm ", calendarBeginTime).toString();
        else
            time = DateFormat.format("MM/dd kk:mm ", calendarBeginTime).toString();
        time += "-";
        if(calendarNowTime.get(Calendar.YEAR) == calendarEndTime.get(Calendar.YEAR) && calendarNowTime.get(Calendar.DAY_OF_YEAR) == calendarEndTime.get(Calendar.DAY_OF_YEAR))
            time += DateFormat.format(" kk:mm", calendarEndTime).toString();
        else
            time += DateFormat.format(" MM/dd kk:mm", calendarEndTime).toString();

        this.durationMinute = (int) ((calendarEndTime.getTimeInMillis() - calendarBeginTime.getTimeInMillis())  / 1000 / 60);

        calendarNowTime.set(0, 0, 0, 0, 0, 0);
        calendarNowTime.add(Calendar.MINUTE, durationMinute);

        int hour = calendarNowTime.get(Calendar.HOUR_OF_DAY);
        int min = calendarNowTime.get(Calendar.MINUTE);
        if(hour == 0)
            duration = String.format("%5d", min);
        else
            duration = String.format("%2d:%02d", hour, min);

//        duration = DateFormat.format("k:mm", durationMinute).toString();


    }

    public void update(ContentValues cv)
    {
        Calendar calendarBeginTime = Calendar.getInstance();
        calendarBeginTime.setTimeInMillis(cv.getAsLong(Events.DTSTART));
        Calendar calendarEndTime = Calendar.getInstance();
        calendarEndTime.setTimeInMillis(cv.getAsLong(Events.DTEND));

        updateTime(calendarBeginTime, calendarEndTime);
    }

    public String getTitle(){ return title; }
    public String getISBN(){ return time; }
    public String getDuration(){ return duration; }
    public String toString() {
        return title + " - " + duration;
    }
}

