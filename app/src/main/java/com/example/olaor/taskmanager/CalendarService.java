package com.example.olaor.taskmanager;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.olaor.taskmanager.TaskManager.Data.Task;

public class CalendarService {

    private static final String ACCOUNT_NAME = "taskManager@manager.com";
    private static final String CALENDAR_NAME = "TaskManager Calendar";

    public static void createCalendar(Context context) {
        if (getCalendarId(context) != -1) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(
                CalendarContract.Calendars.ACCOUNT_NAME,
                ACCOUNT_NAME);
        values.put(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(
                CalendarContract.Calendars.NAME,
                CALENDAR_NAME);
        values.put(
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CALENDAR_NAME);
        values.put(
                CalendarContract.Calendars.CALENDAR_COLOR,
                0xffff0000);
        values.put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(
                CalendarContract.Calendars.OWNER_ACCOUNT,
                ACCOUNT_NAME);
        values.put(
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                "Europe/Warsaw");
        values.put(
                CalendarContract.Calendars.SYNC_EVENTS,
                1);
        Uri.Builder builder =
                CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_NAME,
                ACCOUNT_NAME);
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
                CalendarContract.CALLER_IS_SYNCADAPTER,
                "true");
        Uri uri =
                context.getContentResolver().insert(builder.build(), values);
    }

    private static long getCalendarId(Context context) {
        String[] projection = new String[]{CalendarContract.Calendars._ID};
        String selection =
                CalendarContract.Calendars.ACCOUNT_NAME +
                        " = ? AND " +
                        CalendarContract.Calendars.ACCOUNT_TYPE +
                        " = ? ";
        String[] selArgs =
                new String[]{
                        ACCOUNT_NAME,
                        CalendarContract.ACCOUNT_TYPE_LOCAL};
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.i("error", "In ShowCalendarActivity we should have all needed permissions.");
            return -1;
        }
        Cursor cursor =
                context.getContentResolver().
                        query(
                                CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                selection,
                                selArgs,
                                null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return -1;
    }

    public static void insertTasksToCalendar(Task task, Context context) {
        long calId = getCalendarId(context);
        if (calId == -1) {
            return;
        }
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, task.getStartDate());
        values.put(CalendarContract.Events.DTEND, task.getEndDate());
        values.put(CalendarContract.Events.TITLE, task.getProjectName());
        values.put(CalendarContract.Events.CALENDAR_ID, calId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Warsaw");
        values.put(CalendarContract.Events.DESCRIPTION, "Planed task");
        values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        values.put(CalendarContract.Events.SELF_ATTENDEE_STATUS,
                CalendarContract.Events.STATUS_CONFIRMED);
        values.put(CalendarContract.Events.ALL_DAY, 0);
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 1);
        values.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 1);
        values.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.i("error", "In ShowCalendarActivity we should have all needed permissions.");
            return;
        }
        Uri uri =
                context.getContentResolver().
                        insert(CalendarContract.Events.CONTENT_URI, values);
        task.setIdInCalendar(new Long(uri.getLastPathSegment()));
    }

    public static int deleteTaskFromCalendar(long eventId, Context context) {
        Log.i("info", "removing task from calendar, event if = " + eventId);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.i("error", "In ShowCalendarActivity we should have all needed permissions.");
            return 0;
        }
        String[] selArgs =
                new String[]{Long.toString(eventId)};
        int deleted = context.getContentResolver().delete(
                CalendarContract.Events.CONTENT_URI,
                CalendarContract.Events._ID + " =? ",
                selArgs);
        return deleted;
    }
}
