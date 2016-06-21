package com.ksddo.mylibrary;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.ExtendedProperties;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class CalendarHandler
{
    public static final ArrayList<TaskEvent> dataList_activeTasks = new ArrayList<TaskEvent>();
    public static final ArrayList<TaskEvent> dataList_incompleteScheduledTasks = new ArrayList<TaskEvent>();
    public static final ArrayList<TaskEvent> dataList_completeTasks = new ArrayList<TaskEvent>();

    private static final HashSet<Long> hashSet_activatedTaskIDs = new HashSet<>();

    final String CALENDER_ACCOUNT_NAME = "CalenderAccountName";
    final String CALENDER_NAME = "CalenderName";
    final String EVENT_TITLE = "EventTitle";
    final String EXTENDED_PROPERTY_NAME = "ExtendedPropertyName";
    final String EXTENDED_PROPERTY_VALUE = "ExtendedPropertyValue";
    String extendedPropertyName = "com.ksddo.task.running";
    String extendedPropertyValue = "true";
    String calenderName_completeTasks = "completeTasks";
    String calenderName_activeTasks = "activeTasks";

    @SuppressLint("SimpleDateFormat")

    static int random = new java.util.Random().nextInt(100);

    public void startCalenderEvent(Context context, final TaskEvent taskEvent, boolean removeEventFromList) {
        while (dataList_activeTasks.size() > 0) {
            endCalenderEvent(context, dataList_activeTasks.get(0));
        }

        String calenderAccountName = "kitamura.tetsuo@gmail.com";
        String calenderAccountType = "com.google"; // google
        String calenderEventsTitle = taskEvent.title;
        String calenderId = getCalenderID(context, calenderAccountName, calenderAccountType, calenderName_activeTasks);

        if (calenderId == null)
            return;

        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MINUTE, taskEvent.durationMinute);

        ContentValues cv = new ContentValues();
        cv.put(Events.CALENDAR_ID, calenderId);
        cv.put(Events.TITLE, calenderEventsTitle);
        cv.put(Events.DTSTART, beginTime.getTimeInMillis());
        cv.put(Events.DTEND, endTime.getTimeInMillis());
        cv.put(Events.STATUS, 1);
        cv.put(Events.HAS_EXTENDED_PROPERTIES, 0);
        cv.put(Events.ALL_DAY, 0);
        taskEvent.uri_forEvent = context.getContentResolver().insert(CalenderUtil.asSyncAdapter(Events.CONTENT_URI, calenderAccountName, calenderAccountType), cv).toString();
        taskEvent.update(cv);

        if(removeEventFromList)
        {
            removeByInstanceID(dataList_incompleteScheduledTasks, taskEvent);
        }

        dataList_activeTasks.add(taskEvent);

        hashSet_activatedTaskIDs.add(taskEvent.instanceID);
        SaveData(context);
        /*
        {
            ContentValues extendedValues = new ContentValues();
            extendedValues.put(ExtendedProperties.EVENT_ID, taskEvent.uri_forEvent.getLastPathSegment());
            extendedValues.put(ExtendedProperties.NAME, extendedPropertyName);
            extendedValues.put(ExtendedProperties.VALUE, extendedPropertyValue);
            context.getContentResolver().insert(CalenderUtil.asSyncAdapter(ExtendedProperties.CONTENT_URI, calenderAccountName, calenderAccountType), extendedValues);

        }
        */
        /*
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();


//        beginTime.add(Calendar.HOUR, -11);
        ContentUris.appendId(eventsUriBuilder, beginTime.getTimeInMillis());
        ContentUris.appendId(eventsUriBuilder, beginTime.getTimeInMillis());
        Uri eventsUri = eventsUriBuilder.build();
        Cursor cursor = null;
        c = context.getContentResolver().query(eventsUri, null, null, null, CalendarContract.Instances.DTSTART + " ASC");
        if (c.moveToFirst()) {
            do {
/*                System.out.println(c.getColumnIndex(Events.TITLE));
                if (c.getInt(c.getColumnIndex(Events.ALL_DAY)) == 1)
                {
                    if(c.getLong(c.getColumnIndex(Events.DTSTART)) < beginTime.getTimeInMillis() && c.getLong(c.getColumnIndex(Events.DTEND)) < endTime.getTimeInMillis())
                        continue;
                }
                Uri updateUri = null;
                // The new title for the event
                Uri myUri = ContentUris.withAppendedId(Events.CONTENT_URI, c.getLong(c.getColumnIndex(Events._ID)));
                TaskEvent te = new TaskEvent(c, myUri);
                Cursor evc = context.getContentResolver().query(ExtendedProperties.CONTENT_URI, null, ExtendedProperties.EVENT_ID + "=? AND " + ExtendedProperties.NAME + "=?",
                        new String[]{c.getString(c.getColumnIndex(Events._ID)), extendedPropertyName}, null);
*///                Cursor evc = context.getContentResolver().query(ExtendedProperties.CONTENT_URI, null, ExtendedProperties.EVENT_ID + "=? AND " + ExtendedProperties.NAME + "=? AND " + ExtendedProperties.VALUE + "=?",
//                        new String[]{c.getString(c.getColumnIndex(Events._ID)), extendedPropertyName, extendedPropertyValue}, null);
//                c.getInt(c.getColumnIndex(Events.HAS_EXTENDED_PROPERTIES))
/*
                if((c.getString(c.getColumnIndex(Events.TITLE)).equals(calenderEventsTitle)) &&
                        (c.getString(c.getColumnIndex(Events.CALENDAR_ID)).equals(calenderId)) &&
                        (c.getLong(c.getColumnIndex(Events.DTSTART)) == beginTime.getTimeInMillis()) &&
                        (c.getLong(c.getColumnIndex(Events.DTEND)) == beginTime.getTimeInMillis()) &&
                        (c.getLong(c.getColumnIndex(Events.STATUS)) == 1) &&
                        (c.getLong(c.getColumnIndex(Events.HAS_EXTENDED_PROPERTIES)) == 1) &&
                        (c.getLong(c.getColumnIndex(Events.ALL_DAY)) == 0)) {
                    ContentValues extendedValues = new ContentValues();
                    extendedValues.put(ExtendedProperties.EVENT_ID, c.getString(c.getColumnIndex(Events._ID)));
                    extendedValues.put(ExtendedProperties.NAME, extendedPropertyName);
                    extendedValues.put(ExtendedProperties.VALUE, extendedPropertyValue);
                    context.getContentResolver().insert(CalenderUtil.asSyncAdapter(ExtendedProperties.CONTENT_URI, calenderAccountName, calenderAccountType), extendedValues);
                    taskEvent.id_forExtendedProperties = c.getInt(c.getColumnIndex(Events._ID));
                    break;
                }
            } while (c.moveToNext());
        }
*/
        return;
    }

    final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    public static String CalculateEndTime(ArrayList<TaskEvent> activeTasks, ArrayList<TaskEvent> incompleteScheduledTasks) {
        if(incompleteScheduledTasks == null)
            return "";

        Calendar calendar = Calendar.getInstance();
        long max = 0;
        for (TaskEvent event: activeTasks)
        {
            max = Math.max(max, event.endTimeInMillis);
        }
        if(max != 0)
            calendar.setTimeInMillis(max);
        for (TaskEvent event: incompleteScheduledTasks)
        {
            calendar.add(Calendar.MINUTE, event.durationMinute);
        }

        return simpleDateFormat.format(calendar.getTime());
    }

    public String CalculateEndTime() {
        return CalculateEndTime(dataList_activeTasks, dataList_incompleteScheduledTasks);
    }

    private void removeByInstanceID(ArrayList<TaskEvent> list, final TaskEvent taskEvent)
    {
        list.removeAll(ImmutableList.copyOf(Iterables.filter(list, new Predicate<TaskEvent>()
        {
            @Override
            public boolean apply(TaskEvent input)
            {
                return input.instanceID == taskEvent.instanceID;
            }
        })));
    }

    public void endCalenderEvent(Context context, TaskEvent taskEvent) {
        String calenderAccountName = "kitamura.tetsuo@gmail.com";
        String calenderAccountType = "com.google"; // google
        String calenderId = getCalenderID(context, calenderAccountName, calenderAccountType, calenderName_completeTasks);

        removeByInstanceID(dataList_activeTasks, taskEvent);
        dataList_completeTasks.add(taskEvent);


        if(taskEvent.uri_forEvent == null)
            return;

        /*
        Cursor c = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, null,
                Calendars.NAME + " = ? AND " + Calendars.ACCOUNT_NAME + " = ? AND " + Calendars.ACCOUNT_TYPE + " = ?",
                new String[]{calenderName_completeTasks, calenderAccountName, calenderAccountType}, null);

        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(Calendars.NAME)).equals(calenderName_completeTasks)) {
                    calenderId_completeTasks = c.getString(c.getColumnIndex(Calendars._ID));
                }
            } while (c.moveToNext());
        }
        */

        Calendar endTime = Calendar.getInstance();
//        endTime.add(Calendar.MINUTE, 60 );
        long endMillis = endTime.getTimeInMillis();

        ContentValues cv = new ContentValues();
        cv.put(Events.CALENDAR_ID, calenderId);
        cv.put(Events.DTEND, endMillis);
        cv.put(Events.HAS_EXTENDED_PROPERTIES, 0);
//        cv.put(Events.CALENDAR_ID, calenderId_completeTasks);
        int rows = context.getContentResolver().update(Uri.parse(taskEvent.uri_forEvent), cv, null, null);

        /*
        int row = context.getContentResolver().delete(CalenderUtil.asSyncAdapter(ExtendedProperties.CONTENT_URI, calenderAccountName, calenderAccountType),
                ExtendedProperties.EVENT_ID + "=? AND " + ExtendedProperties.NAME + "=?",
                new String[]{ String.valueOf(taskEvent.id_forExtendedProperties), extendedPropertyName});
*/
        return;
    }

    private String getCalenderID(Context context, String calenderAccountName, String calenderAccountType, String calenderName) {
        Cursor c = context.getContentResolver().query(Calendars.CONTENT_URI, null,
                Calendars.NAME + " = ? AND " + Calendars.ACCOUNT_NAME + " = ? AND " + Calendars.ACCOUNT_TYPE + " = ?",
                new String[]{calenderName, calenderAccountName, calenderAccountType}, null);

        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(Calendars.NAME)).equals(calenderName)) {
                    return c.getString(c.getColumnIndex(Calendars._ID));
                }
            } while (c.moveToNext());
        }
        return null;
    }

    public void readCalenderEvent(Context context) {
        dataList_activeTasks.clear();
        dataList_completeTasks.clear();
        dataList_incompleteScheduledTasks.clear();

        String calenderAccountName = "kitamura.tetsuo@gmail.com";
        String calenderAccountType = "com.google"; // google
        String calenderEventsTitle = "Title";

        Calendar calenderUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar beginTime = Calendar.getInstance();
        calenderUTC.set(Calendar.YEAR, beginTime.get(Calendar.YEAR));
        calenderUTC.set(Calendar.DAY_OF_YEAR, beginTime.get(Calendar.DAY_OF_YEAR));
        calenderUTC.set(Calendar.HOUR_OF_DAY, 0);
        calenderUTC.set(Calendar.MINUTE, 0);
        calenderUTC.set(Calendar.SECOND, 0);
        calenderUTC.set(Calendar.MILLISECOND, 0);

        beginTime.set(Calendar.HOUR_OF_DAY, 0);
        beginTime.set(Calendar.MINUTE, 0);
        beginTime.set(Calendar.SECOND, 0);
        beginTime.set(Calendar.MILLISECOND, 0);

        long startMillis = calenderUTC.getTimeInMillis();
        startMillis = beginTime.getTimeInMillis();

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();


//        beginTime.add(Calendar.HOUR, -11);
        ContentUris.appendId(eventsUriBuilder, beginTime.getTimeInMillis());
        Calendar endTime = (Calendar) beginTime.clone();
        endTime.add(Calendar.DAY_OF_YEAR, 1);
        ContentUris.appendId(eventsUriBuilder, endTime.getTimeInMillis());
        Uri eventsUri = eventsUriBuilder.build();
        Cursor cursor = null;
        Cursor c = context.getContentResolver().query(eventsUri, null, null, null, CalendarContract.Instances.BEGIN + " ASC");

//        c = context.getContentResolver().query(Events.CONTENT_URI, null, Events.DTSTART + " >= ? AND " + Events.DTSTART + " <= ?" ,
//                new String[]{String.valueOf(startMillis - 1000 * 60 * 60 * 8), String.valueOf(startMillis + 1000 * 60 * 60 * 48)}, null);

/*
        boolean flgHasIconEvent = false;
        ArrayList<String> runningEventIDs = new ArrayList<>();
        {
            Cursor evc = context.getContentResolver().query(ExtendedProperties.CONTENT_URI, null, ExtendedProperties.NAME + "=?",
                    new String[]{extendedPropertyName}, null);
//                    Cursor evc = context.getContentResolver().query(ExtendedProperties.CONTENT_URI, null, ExtendedProperties.NAME + "=? AND " + ExtendedProperties.VALUE + "=?",
//                    new String[]{extendedPropertyName, extendedPropertyValue}, null);
            if (evc.moveToFirst()) {
                flgHasIconEvent = true;
                runningEventIDs.add(evc.getString(evc.getColumnIndex(ExtendedProperties.EVENT_ID)));
                runningEventIDs.add(evc.getString(evc.getColumnIndex(ExtendedProperties._ID)));
//                runningEventIDs.add(evc.getString(evc.getColumnIndex(ExtendedProperties.ORIGINAL_ID)));
//                runningEventIDs.add(evc.getString(evc.getColumnIndex(ExtendedProperties.UID_2445)));
            }
        }
*/

        if (c.moveToFirst()) {
            do {
                System.out.println(c.getColumnIndex(Events.TITLE));
                if (c.getInt(c.getColumnIndex(Events.ALL_DAY)) == 1)
                {
                    if(c.getLong(c.getColumnIndex(Events.DTSTART)) < beginTime.getTimeInMillis() && c.getLong(c.getColumnIndex(Events.DTEND)) < endTime.getTimeInMillis())
                        continue;
                }

                Uri updateUri = null;
                Long instanceID = c.getLong(c.getColumnIndex(CalendarContract.Instances._ID));
                // The new title for the event

                Uri myUri = ContentUris.withAppendedId(Events.CONTENT_URI, c.getLong(c.getColumnIndex(CalendarContract.Instances.EVENT_ID)));
                TaskEvent te = new TaskEvent(c, myUri);

                if (c.getString(c.getColumnIndex(Events.CALENDAR_DISPLAY_NAME)).equals(calenderName_activeTasks)) {
                    dataList_activeTasks.add(te);
                }
                else if (c.getString(c.getColumnIndex(Events.CALENDAR_DISPLAY_NAME)).equals(calenderName_completeTasks)) {
//                    Cursor evc = context.getContentResolver().query(ExtendedProperties.CONTENT_URI, null, ExtendedProperties.EVENT_ID + "=? AND " + ExtendedProperties.NAME + "=? AND " + ExtendedProperties.VALUE + "=?",
//                            new String[]{c.getString(c.getColumnIndex(CalendarContract.Instances.EVENT_ID)), extendedPropertyName, extendedPropertyValue}, null);
//                        if (evc.moveToFirst())
                    dataList_completeTasks.add(te);
                } else {
                    if(!context.getString(R.string.incompleteTaskCalenders).contains(c.getString(c.getColumnIndex(Events.CALENDAR_DISPLAY_NAME))))
                        continue;
                    if (!hashSet_activatedTaskIDs.contains(instanceID)) {
                        dataList_incompleteScheduledTasks.add(te);
                    }
                }
            } while (c.moveToNext());
        }

        return;
/*
                Cursor evc = context.getContentResolver().query(ExtendedProperties.CONTENT_URI, null, ExtendedProperties.EVENT_ID + "=? AND " + ExtendedProperties.NAME + "=? AND " + ExtendedProperties.VALUE + "=?",
                        new String[]{c.getString(c.getColumnIndex(Events._ID)), extendedPropertyName, extendedPropertyValue}, null);
                if (evc.moveToFirst()) {
                    flgHasIconEvent = true;
                    break;
                }

        if (!flgHasIconEvent) {
            ContentValues cv = new ContentValues();
            cv.put(Events.CALENDAR_ID, calenderId);
            cv.put(Events.TITLE, calenderEventsTitle);
            cv.put(Events.DTSTART, startMillis);
            //	cv.put(Events.DTEND, endMillis);
            cv.put(Events.STATUS, 1);
            cv.put(Events.HAS_EXTENDED_PROPERTIES, 1);
            cv.put(Events.ALL_DAY, 1);
            Uri uri_forEvent = context.getContentResolver().insert(CalenderUtil.asSyncAdapter(Events.CONTENT_URI, calenderAccountName, calenderAccountType), cv);

            c = context.getContentResolver().query(uri_forEvent, null, null, null, null);
            if (c.moveToFirst()) {
                do {

                    ContentValues extendedValues = new ContentValues();
                    extendedValues.put(ExtendedProperties.EVENT_ID, c.getString(c.getColumnIndex(Events._ID)));
                    extendedValues.put(ExtendedProperties.NAME, extendedPropertyName);
                    extendedValues.put(ExtendedProperties.VALUE, extendedPropertyValue);
                    context.getContentResolver().insert(CalenderUtil.asSyncAdapter(ExtendedProperties.CONTENT_URI, calenderAccountName, calenderAccountType), extendedValues);
                } while (c.moveToNext());
            }
        }
        */
    }


    public void addCalenderEvent(Activity activity) {
        Bundle extras = activity.getIntent().getExtras();
        if (extras == null || !extras.containsKey(CALENDER_ACCOUNT_NAME))
            return;

        String calenderId = null;
        String calenderName = extras.getString(CALENDER_NAME);
        String calenderAccountName = extras.getString(CALENDER_ACCOUNT_NAME);
        String calenderAccountType = "com.google"; // google
        String calenderEventsTitle = extras.getString(EVENT_TITLE);
        String extendedPropertyName = extras.getString(EXTENDED_PROPERTY_NAME);
        String extendedPropertyValue = extras.getString(EXTENDED_PROPERTY_VALUE);

        Cursor c = activity.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, null,
                Calendars.NAME + " = ? AND " + Calendars.ACCOUNT_NAME + " = ? AND " + Calendars.ACCOUNT_TYPE + " = ?",
                new String[]{calenderName, calenderAccountName, calenderAccountType}, null);

        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(Calendars.NAME)).equals(calenderName)) {
                    calenderId = c.getString(c.getColumnIndex(Calendars._ID));
                }
            } while (c.moveToNext());
        }

        if (calenderId == null)
            return;

        Calendar calenderUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar beginTime = Calendar.getInstance();
        calenderUTC.set(Calendar.YEAR, beginTime.get(Calendar.YEAR));
        calenderUTC.set(Calendar.DAY_OF_YEAR, beginTime.get(Calendar.DAY_OF_YEAR));
        calenderUTC.set(Calendar.HOUR, 0);
        calenderUTC.set(Calendar.MINUTE, 0);
        calenderUTC.set(Calendar.SECOND, 0);
        calenderUTC.set(Calendar.MILLISECOND, 0);
        long startMillis = calenderUTC.getTimeInMillis();

        c = activity.getContentResolver().query(Events.CONTENT_URI, null, Events.TITLE + "=? AND " + Events.HAS_EXTENDED_PROPERTIES + "=? AND " + Events.DTSTART + "=?",
                new String[]{"", "1", String.valueOf(startMillis - 1000 * 60 * 60 * 12)}, null);

        boolean flgHasIconEvent = false;
        if (c.moveToFirst()) {
            do {
                Cursor evc = activity.getContentResolver().query(ExtendedProperties.CONTENT_URI, null, ExtendedProperties.EVENT_ID + "=? AND " + ExtendedProperties.NAME + "=? AND " + ExtendedProperties.VALUE + "=?",
                        new String[]{c.getString(c.getColumnIndex(Events._ID)), extendedPropertyName, extendedPropertyValue}, null);
                if (evc.moveToFirst()) {
                    flgHasIconEvent = true;
                    break;
                }
            } while (c.moveToNext());
        }

        if (!flgHasIconEvent) {
            ContentValues cv = new ContentValues();
            cv.put(Events.CALENDAR_ID, calenderId);
            cv.put(Events.TITLE, calenderEventsTitle);
            cv.put(Events.DTSTART, startMillis);
            //	cv.put(Events.DTEND, endMillis);
            cv.put(Events.STATUS, 1);
            cv.put(Events.HAS_EXTENDED_PROPERTIES, 1);
            cv.put(Events.ALL_DAY, 1);
            Uri uri = activity.getContentResolver().insert(CalenderUtil.asSyncAdapter(Events.CONTENT_URI, calenderAccountName, calenderAccountType), cv);

            c = activity.getContentResolver().query(uri, null, null, null, null);
            if (c.moveToFirst()) {
                do {

                    ContentValues extendedValues = new ContentValues();
                    extendedValues.put(ExtendedProperties.EVENT_ID, c.getString(c.getColumnIndex(Events._ID)));
                    extendedValues.put(ExtendedProperties.NAME, extendedPropertyName);
                    extendedValues.put(ExtendedProperties.VALUE, extendedPropertyValue);
                    activity.getContentResolver().insert(CalenderUtil.asSyncAdapter(ExtendedProperties.CONTENT_URI, calenderAccountName, calenderAccountType), extendedValues);
                } while (c.moveToNext());
            }
        }
    }

    public void addShortCut(Activity activity, String ShortcutName, String CalenderAccountName, String CalenderName, String EventTitle, String ExtendedPropertyName, String ExtendedPropertyValue)
    {
        Intent shortcutIntent = new Intent(Intent.ACTION_VIEW);
//        shortcutIntent.setClassName(activity.getApplicationContext(), MainActivity.class.getName());
        shortcutIntent.putExtra(CALENDER_ACCOUNT_NAME, CalenderAccountName);
        shortcutIntent.putExtra(CALENDER_NAME, CalenderName);
        shortcutIntent.putExtra(EVENT_TITLE, EventTitle);
        shortcutIntent.putExtra(EXTENDED_PROPERTY_NAME, ExtendedPropertyName);
        shortcutIntent.putExtra(EXTENDED_PROPERTY_VALUE, ExtendedPropertyValue);

        //shortcutIntent
        Intent intent = new Intent();

        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, ShortcutName);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        /*
        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(), R.mipmap.ic_launcher);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        if(activity.getIntent().getComponent().getClassName().endsWith(".MainActivityShortcut")){
            activity.setResult(activity.RESULT_OK, intent);
        }
        else{
            activity.sendBroadcast(intent);
        }
        */
        activity.finish();
    }


    public void LoadData(Context context){
        try (FileInputStream fis = context.openFileInput("SaveData.dat"))
        {
            ObjectInputStream ois = new ObjectInputStream(fis);
            hashSet_activatedTaskIDs.addAll((HashSet<Long>) ois.readObject());
            Calendar calenderLastWrite = (Calendar)  ois.readObject();
            if(Calendar.getInstance().get(Calendar.DAY_OF_YEAR) != calenderLastWrite.get(Calendar.DAY_OF_YEAR))
                hashSet_activatedTaskIDs.clear();
            /*
            adapter_activeTasks.dataList = (ArrayList<TaskEvent>) ois.readObject();
            adapter_completeTasks.dataList = (ArrayList<TaskEvent>) ois.readObject();
            adapter_incompleteScheduledTasks.dataList = (ArrayList<TaskEvent>) ois.readObject();
            adapter_activeTasks.notifyDataSetChanged();
            adapter_completeTasks.notifyDataSetChanged();
            adapter_incompleteScheduledTasks.notifyDataSetChanged();
            */
        }
        catch (Exception e) {
        }
    }

    public void SaveData(Context context){
        try (FileOutputStream fos = context.openFileOutput("SaveData.dat", context.MODE_PRIVATE))
        {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(hashSet_activatedTaskIDs);
            oos.writeObject(Calendar.getInstance());
            /*
            oos.writeObject(adapter_activeTasks.dataList);
            oos.writeObject(adapter_completeTasks.dataList);
            oos.writeObject(adapter_incompleteScheduledTasks.dataList);
            */
        } catch (Exception e) {
        }
        /**/
    }
}
