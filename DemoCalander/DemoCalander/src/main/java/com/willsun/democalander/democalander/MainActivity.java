package com.willsun.democalander.democalander;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;


public class MainActivity extends Activity {

    private static final String DEBUG_TAG = "MyActivity";

    /*
        Projection array. Creating indices for this array instead of doing dynamic lookups improves
        performance.
     */
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };

    // The indices for the projection array above
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    private Button showCalendar, showEvents, insertEvent, getEventId;
    private TextView eventId;

    private static final String prefix = "Event ID: \n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyOnClickListener listener = new MyOnClickListener();
        showCalendar = (Button) findViewById(R.id.btn_show_calendar);
        showCalendar.setOnClickListener(listener);
        showEvents = (Button) findViewById(R.id.btn_show_event);
        showEvents.setOnClickListener(listener);
        insertEvent = (Button) findViewById(R.id.btn_insert_event);
        insertEvent.setOnClickListener(listener);
        getEventId = (Button) findViewById(R.id.btn_get_event_id);
        getEventId.setOnClickListener(listener);

        eventId = (TextView) findViewById(R.id.tv_event_id);
        eventId.setText(prefix);

//        Cursor cursor = null;
//        ContentResolver contentResolver = getContentResolver();
//        Uri uri = CalendarContract.Calendars.CONTENT_URI;
//        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
//                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
//                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
////        String[] selectionArgs = new String[]{
////                "willsun_liu@sina.com", "com.google", "willsun_liu@sina.com"
////        };
//        cursor = contentResolver.query(uri, EVENT_PROJECTION, null, null, null);
//        for (int i = 0; i < cursor.getColumnCount(); i++) {
//            Log.i(DEBUG_TAG, cursor.getColumnName(i));
//        }
//
//        while (cursor.moveToNext()) {
//            long callID = 0;
//            String displayName = null;
//            String accountName = null;
//            String ownerName = null;
//
//            // Get the field values
//            callID = cursor.getLong(PROJECTION_ID_INDEX);
//            displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
//            accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
//            ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
//
////            Log.i(DEBUG_TAG, callID + "");
////            Log.i(DEBUG_TAG, displayName);
////            Log.i(DEBUG_TAG, accountName);
////            Log.i(DEBUG_TAG, ownerName);
//        }
    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_show_calendar:
                    showCalendar();
                    break;
                case R.id.btn_show_event:
                    showEvent();
                    break;
                case R.id.btn_insert_event:
                    insertEvent();
                    break;
                case R.id.btn_get_event_id:
                    getEventId();
                    break;
            }
        }
    }

    // Using intent to view calendar data
    private void showCalendar() {
        // Showing how to open the Calendar to a particular date
        long startMillis;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2014, Calendar.DECEMBER, 20, 17, 0);  // month is from 0 ~ 11
        startMillis = beginTime.getTimeInMillis();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        startActivity(intent);
    }

    private void showEvent() {
        // Showing how to open an event for viewing:
        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, 208);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(uri);
        startActivity(intent);
    }

    // Using intent to insert an event
    private void insertEvent() {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2014, Calendar.MAY, 21, 12, 40);
        Calendar endTime = Calendar.getInstance();
        endTime.set(2014, Calendar.MAY, 21, 13, 0);
        Intent intent = new Intent(Intent.ACTION_EDIT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, "Yoga")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        startActivity(intent);
    }

    /**
     * Since Intents for Calendar do not support for getting Event ID, we have to use
     * CalendarContract provider to get it.
     */
    private void getEventId() {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2014, Calendar.MAY, 21, 12, 0);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2014, Calendar.MAY, 21, 13, 0);
        long endMillis = endTime.getTimeInMillis();

        Cursor cur = null;
        ContentResolver cr = getContentResolver();

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        cur = cr.query(builder.build(), new String[]{CalendarContract.Instances.TITLE, CalendarContract.Instances.EVENT_ID}, null, null, null);
        if (cur != null) {
            while (cur.moveToNext()) {
                Log.i(DEBUG_TAG, cur.getString(cur.getColumnIndex(CalendarContract.Instances.TITLE)));
                String text = eventId.getText().toString();
                text += cur.getString(cur.getColumnIndex(CalendarContract.Instances.EVENT_ID));
                text += "; ";
                eventId.setText(text);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
