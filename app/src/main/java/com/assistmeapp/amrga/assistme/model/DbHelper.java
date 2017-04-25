package com.assistmeapp.amrga.assistme.model;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by amrga on 9/3/2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "assistme.db";
    public static final int DB_VERSION = 1;

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Alarm.getSql());
        db.execSQL(AlarmInfo.getSql());
        db.execSQL(AlarmRepeater.getSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Alarm.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlarmInfo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlarmRepeater.TABLE_NAME);

        onCreate(db);
    }

    private final String populateSQL = Util.concat("SELECT ",
                                                        "a."+Alarm.COL_FROMDATE+", ",
                                                        "a."+Alarm.COL_TODATE+", ",
                                                        "a."+Alarm.COL_RULE+", ",
                                                        "a."+Alarm.COL_INTERVAL+", ",
                                                        "ai."+AlarmInfo.COL_AT,
                                                    " FROM "+Alarm.TABLE_NAME+" AS a",
                                                    " JOIN "+AlarmInfo.TABLE_NAME+" AS ai",
                                                    " ON a."+Alarm.COL_ID+" = ai."+AlarmInfo.COL_ALARMID,
                                                    " WHERE a."+Alarm.COL_ID+" = ?");

    public void populate(long alarmId, SQLiteDatabase db) {
        String[] selection = {String.valueOf(alarmId)};
        Cursor c = db.rawQuery(populateSQL, selection);

        if (c.moveToFirst()) {
            Calendar cal = Calendar.getInstance();
            AlarmRepeater alarmRpt = new AlarmRepeater();
            long now = System.currentTimeMillis();
            db.beginTransaction();
            try {
                do {
                    Date fromDate = sdf.parse(c.getString(0));
                    cal.setTime(fromDate);

                    String[] tokens = c.getString(4).split(":");
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tokens[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(tokens[1]));
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);

                    String rule = c.getString(2);
                    String interval = c.getString(3);

                    if (rule == null && interval == null) {
                        alarmRpt.reset();
                        alarmRpt.setAlarmId(alarmId);
                        alarmRpt.setDateTime(cal.getTimeInMillis());
                        if (alarmRpt.getDateTime() < now-Util.MIN) {
                            nextDate(cal, interval);
                            continue;
                        }
                        alarmRpt.save(db);
                    }
                    else {
                        if (rule != null) {
                            tokens = rule.split(" ");
                            interval = "0 0 1 0 0";

                            if (!"0".equals(tokens[1])) {
                                cal.set(Calendar.DAY_OF_WEEK, Integer.parseInt(tokens[1]));
                                interval = "0 0 7 0 0";
                            }

                            if (!"0".equals(tokens[0]) && "0".equals(tokens[1])) {
                                cal.set(Calendar.DATE, Integer.parseInt(tokens[0]));
                                interval = "0 0 0 1 0";
                            }

                            if (!"0".equals(tokens[2])) {
                                cal.set(Calendar.MONTH, Integer.parseInt(tokens[2])-1);
                                interval = "0 0 0 0 1";
                            }

                            while(cal.getTime().before(fromDate)) {
                                nextDate(cal, interval);
                            }
                        }

                        Date toDate = sdf.parse(c.getString(1));
                        toDate.setHours(0);
                        toDate.setMinutes(0);
                        toDate.setSeconds(0);
                        toDate.setDate(toDate.getDate()+1);
                        while(cal.getTime().before(toDate)) {
                            alarmRpt.reset();
                            alarmRpt.setAlarmId(alarmId);
                            alarmRpt.setDateTime(cal.getTimeInMillis());
                            if (alarmRpt.getDateTime() < now-Util.MIN) {
                                nextDate(cal, interval);
                                continue;
                            }
                            alarmRpt.save(db);
                            nextDate(cal, interval);
                        }
                    }
                } while(c.moveToNext());
                db.setTransactionSuccessful();
            } catch (Exception e) {

            } finally {
                db.endTransaction();
            }
        }
        c.close();
    }

    private void nextDate(Calendar cal, String interval) {
        String[] tokens = interval.split(" ");
        cal.add(Calendar.MINUTE, Integer.parseInt(tokens[0]));
        cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(tokens[1]));
        cal.add(Calendar.DATE, Integer.parseInt(tokens[2]));
        cal.add(Calendar.MONTH, Integer.parseInt(tokens[3]));
        cal.add(Calendar.YEAR, Integer.parseInt(tokens[4]));
    }

    public static Cursor listForExecute(SQLiteDatabase db, Long id) {
        if (id != -1) {
            String query = "SELECT * FROM "+AlarmRepeater.TABLE_NAME+" WHERE "
                    +AlarmRepeater.COL_ALARMID+ " = ?";
            return db.rawQuery(query, new String[]{String.valueOf(id)});
        }
        else {
            String query = "SELECT * FROM "+AlarmRepeater.TABLE_NAME;
            return db.rawQuery(query, null);
        }
    }

    private static String listSQL = "SELECT "+"a."+Alarm.COL_ID+", a."+Alarm.COL_TITLE+
                                ", ai."+AlarmInfo.COL_AT+", ai."+AlarmInfo.COL_ACTION+
                                ", ai."+AlarmInfo.COL_LOCATION+", ai."+AlarmInfo.COL_MESSAGE+
                                ", ai."+AlarmInfo.COL_TAG+", ar."+AlarmRepeater.COL_DATETIME+
                                " FROM "+Alarm.TABLE_NAME+" AS a"+" JOIN "+
                                AlarmInfo.TABLE_NAME+" AS ai, "+AlarmRepeater.TABLE_NAME+" AS ar"+
                                " ON a."+Alarm.COL_ID+" = "+"ai."+AlarmInfo.COL_ALARMID+" AND a."+
                                Alarm.COL_ID+" = ar."+AlarmRepeater.COL_ALARMID+
                                " WHERE ar."+AlarmRepeater.COL_DATETIME+" <= ? ORDER BY ar."+AlarmRepeater.COL_DATETIME+" ASC";

    public static Cursor list(SQLiteDatabase db, String dateTime) {
        Cursor c  = db.rawQuery(listSQL, new String[]{dateTime});
        return c;
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        try {
            db.delete(Alarm.TABLE_NAME, Alarm.COL_ID + " = ?", new String[]{String.valueOf(id)});
            db.delete(AlarmInfo.TABLE_NAME, AlarmInfo.COL_ALARMID + " = ?", new String[]{String.valueOf(id)});
            db.delete(AlarmRepeater.TABLE_NAME, AlarmRepeater.COL_ALARMID + " = ?", new String[]{String.valueOf(id)});
            return true;
        }
        catch (Exception e) {

        }
        return false;
    }

    public static boolean removeRepeating(SQLiteDatabase db, long id) {
        try {
            String tempId = String.valueOf(id);
            db.delete(AlarmRepeater.TABLE_NAME,
                            AlarmRepeater.COL_ALARMID+" = ? AND "+
                            AlarmRepeater.COL_ID+" NOT IN ( SELECT "+
                            AlarmRepeater.COL_ID+" FROM "+AlarmRepeater.TABLE_NAME+
                            " WHERE "+AlarmRepeater.COL_ALARMID+" = ? LIMIT 1 )",
                            new String[]{tempId, tempId});
            return true;
        }
        catch (Exception e) {

        }
        return false;
    }

    public static boolean removeReceived(SQLiteDatabase db, long id) {
        try {
            String tempid = String.valueOf(id);
            db.delete(AlarmRepeater.TABLE_NAME,
                            AlarmRepeater.COL_ALARMID+" = ? AND "+
                            AlarmRepeater.COL_ID+" IN ( SELECT "+
                            AlarmRepeater.COL_ID+" FROM "+AlarmRepeater.TABLE_NAME+
                            " WHERE "+AlarmRepeater.COL_ALARMID+" = ? LIMIT 1 )",
                            new String[]{tempid, tempid});
            Cursor c = db.rawQuery("SELECT * FROM "+AlarmRepeater.TABLE_NAME+
                                    " WHERE "+AlarmRepeater.COL_ALARMID+" = ?",
                                    new String[]{String.valueOf(id)});
            if (!c.moveToNext()) {
                remove(db, id);
            }
            return true;
        }
        catch (Exception e) {

        }
        return false;
    }

    public static void removeExpired(SQLiteDatabase db, Long now) {
        Cursor c = db.rawQuery("SELECT "+AlarmRepeater.COL_ALARMID+" FROM "+AlarmRepeater.TABLE_NAME+" WHERE "+
                            AlarmRepeater.COL_DATETIME+" < ?", new String[]{String.valueOf(now)});
        if (c.moveToFirst()) {
            do {
                removeReceived(db, c.getLong(c.getColumnIndex(AlarmRepeater.COL_ALARMID)));
            } while (c.moveToNext());
        }
    }

    private static String notificationSQL = "SELECT a."+Alarm.COL_TITLE+
            ", ai."+AlarmInfo.COL_AT+", ai."+AlarmInfo.COL_ACTION+
            ", ai."+AlarmInfo.COL_LOCATION+", ai."+AlarmInfo.COL_MESSAGE+
            ", ai."+AlarmInfo.COL_TAG+", ar."+AlarmRepeater.COL_DATETIME+
            " FROM "+Alarm.TABLE_NAME+" AS a"+" JOIN "+
            AlarmInfo.TABLE_NAME+" AS ai, "+AlarmRepeater.TABLE_NAME+" AS ar"+
            " ON a."+Alarm.COL_ID+" = "+"ai."+AlarmInfo.COL_ALARMID+" AND a."+
            Alarm.COL_ID+" = ar."+AlarmRepeater.COL_ALARMID+
            " WHERE ar."+AlarmRepeater.COL_ID+" = ?";

    public static Cursor ListNotification(SQLiteDatabase db, Long id) {
        return db.rawQuery(notificationSQL, new String[]{String.valueOf(id)});
    }

    public static final String getDateStr(int year, int month, int date) {
        return Util.concat(year, "-", month, "-", date);
    }

    public static final String getTimeStr(int hour, int minute) {
        return Util.concat(hour, ":", minute>9 ? "":"0", minute);
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
