package com.assistmeapp.amrga.assistme;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.assistmeapp.amrga.assistme.model.Alarm;
import com.assistmeapp.amrga.assistme.model.AlarmInfo;
import com.assistmeapp.amrga.assistme.model.AlarmRepeater;
import com.assistmeapp.amrga.assistme.model.DbHelper;

/**
 * Created by amrga on 9/13/2016.
 */
public class TaskReceiver extends BroadcastReceiver {

    SQLiteDatabase db;
    DbHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {

        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();

        long id = intent.getLongExtra(AlarmRepeater.COL_ID, -1);
        long alarmId = intent.getLongExtra(AlarmRepeater.COL_ALARMID, -1);

        Cursor c = dbHelper.ListNotification(db, id);

        Intent i = new Intent(context, TaskNotification.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (c.moveToFirst()) {
            do {
                i.putExtra(Alarm.COL_TITLE, c.getString(c.getColumnIndex(Alarm.COL_TITLE)));
                i.putExtra(AlarmInfo.COL_MESSAGE, c.getString(c.getColumnIndex(AlarmInfo.COL_MESSAGE)));
                i.putExtra(AlarmInfo.COL_AT, c.getString(c.getColumnIndex(AlarmInfo.COL_AT)));
                i.putExtra(AlarmInfo.COL_ACTION, c.getString(c.getColumnIndex(AlarmInfo.COL_ACTION)));
                i.putExtra(AlarmInfo.COL_LOCATION, c.getString(c.getColumnIndex(AlarmInfo.COL_LOCATION)));
                i.putExtra(AlarmInfo.COL_TAG, c.getString(c.getColumnIndex(AlarmInfo.COL_TAG)));
                i.putExtra(AlarmRepeater.COL_DATETIME, c.getString(c.getColumnIndex(AlarmRepeater.COL_DATETIME)));
            } while (c.moveToNext());
        }

        dbHelper.removeReceived(db, alarmId);

        UpdateSubject.getInstance().notifyUpdate("");

        context.startActivity(i);

    }
}
