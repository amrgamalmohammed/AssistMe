package com.assistmeapp.amrga.assistme;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import com.assistmeapp.amrga.assistme.model.AlarmInfo;
import com.assistmeapp.amrga.assistme.model.AlarmRepeater;
import com.assistmeapp.amrga.assistme.model.DbHelper;

import java.util.Calendar;

/**
 * Created by amrga on 9/12/2016.
 */
public class TaskService extends IntentService {

    private static final String TAG = "TaskService";

    public static final String POPULATE = "POPULATE";
    public static final String CREATE = "CREATE";
    public static final String CANCEL_ALL = "CANCEL_ALL";
    public static final String CANCEL_REPEATING = "CANCEL_REPEATING";

    private static DbHelper dbHelper;
    private static SQLiteDatabase db;

    private String intentAction;

    private IntentFilter filter;

    public TaskService() {
        super(TAG);
        filter = new IntentFilter();
        filter.addAction(POPULATE);
        filter.addAction(CREATE);
        filter.addAction(CANCEL_ALL);
        filter.addAction(CANCEL_REPEATING);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        intentAction = intent.getAction();
        String alarmId = intent.getStringExtra(AlarmInfo.COL_ALARMID);
        String caller = intent.getStringExtra("FRAGMENT");

        if (filter.matchAction(intentAction)) {
            switch (intentAction) {
                case POPULATE:
                    dbHelper.populate(Long.parseLong(alarmId), db);
                    execute(alarmId, POPULATE);
                    commitUpdateView(caller);
                    break;
                case CREATE:
                    dbHelper.removeExpired(db, Calendar.getInstance().getTimeInMillis());
                    execute(alarmId, POPULATE);
                    break;
                case CANCEL_ALL:
                    execute(alarmId, CANCEL_ALL);
                    dbHelper.remove(db, Long.parseLong(alarmId));
                    commitUpdateView(caller);
                    break;
                case CANCEL_REPEATING:
                    execute(alarmId, CANCEL_REPEATING);
                    dbHelper.removeRepeating(db, Long.parseLong(alarmId));
                    commitUpdateView(caller);
                    break;
            }
        }
    }

    private void execute(String alarmId, String flag) {
        Intent intent;
        PendingIntent pendingIntent;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Cursor c = dbHelper.listForExecute(db, Long.parseLong(alarmId));

        if (c.moveToFirst()) {
            boolean valid = true;
            if (flag.equals(CANCEL_REPEATING)) {
                valid = c.moveToNext();
            }
            if (valid) {
                do {
                    intent = new Intent(this, TaskReceiver.class);
                    intent.putExtra(AlarmRepeater.COL_ID, c.getLong(c.getColumnIndex(AlarmRepeater.COL_ID)));
                    intent.putExtra(AlarmRepeater.COL_ALARMID, c.getLong(c.getColumnIndex(AlarmRepeater.COL_ALARMID)));
                    long id = c.getLong(c.getColumnIndex(AlarmRepeater.COL_ID));

                    pendingIntent = PendingIntent.getBroadcast(this, (int)id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    if (flag.equals(POPULATE)) {
                        long time = c.getLong(c.getColumnIndex(AlarmRepeater.COL_DATETIME));
                        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                    }
                    else if (flag.equals(CANCEL_ALL) || flag.equals(CANCEL_REPEATING)) {
                        alarmManager.cancel(pendingIntent);
                    }
                } while (c.moveToNext());
            }
        }
        c.close();
    }

    private void commitUpdateView(final String caller) {
        try {
            Handler handler = new Handler(getApplicationContext().getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    UpdateSubject.getInstance().notifyUpdate(caller);
                }
            };
            handler.post(runnable);
        } catch (Exception e) {
        }

    }

}
