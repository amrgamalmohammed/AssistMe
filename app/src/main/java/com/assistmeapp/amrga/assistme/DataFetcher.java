package com.assistmeapp.amrga.assistme;

import android.database.Cursor;

import com.assistmeapp.amrga.assistme.model.DbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by amrga on 9/7/2016.
 */
public class DataFetcher {

    private Cursor cursor;
    private List<AlarmItem> list;

    public DataFetcher () {
    }

    public List<AlarmItem> getData (String callType) {
        String time = prepareTime(callType);
        cursor = ContainerActivity.dbHelper.list(ContainerActivity.db, time);
        list = new ArrayList<>();
        while (cursor.moveToNext()) {
            AlarmItem item = new AlarmItem();
            item.setId(cursor.getLong(cursor.getColumnIndex("_id")));
            item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            item.setAt(cursor.getString(cursor.getColumnIndex("at")));
            item.setMessage(cursor.getString(cursor.getColumnIndex("message")));
            item.setLocation(cursor.getString(cursor.getColumnIndex("location")));
            item.setAction(cursor.getString(cursor.getColumnIndex("action")));
            item.setDate(cursor.getLong(cursor.getColumnIndex("datetime")));
            item.setThumbnail(cursor.getInt(cursor.getColumnIndex("tag")));
            list.add(item);
        }
        return list;
    }

    private String prepareTime (String callType) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        switch (callType) {
            case "today" :
                break;
            case "week" :
                cal.set(Calendar.DAY_OF_WEEK, 7);
                break;
            case "month" :
                cal.set(Calendar.DAY_OF_WEEK, 7);
                cal.set(Calendar.WEEK_OF_MONTH, 5);
                break;
        }
        return String.valueOf(cal.getTimeInMillis());
    }

    public void edit( long id) {

    }
}
