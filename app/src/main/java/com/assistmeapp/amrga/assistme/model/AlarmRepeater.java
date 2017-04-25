package com.assistmeapp.amrga.assistme.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by amrga on 9/3/2016.
 */
public class AlarmRepeater extends AbstractModel {

    public static final String TABLE_NAME = "alarmrepeater";
    public static final String COL_ID = AbstractModel.COL_ID;
    public static final String COL_ALARMID = "alarm_id";
    public static final String COL_DATETIME = "datetime";

    private long alarmId;
    private long dateTime;

    static String getSql() {
        return Util.concat("CREATE TABLE ", TABLE_NAME, " (",
                AbstractModel.getSql(),
                COL_ALARMID, " INTEGER, ",
                COL_DATETIME, " INTEGER",
                ");");
    }

    @Override
    long save(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ALARMID, alarmId);
        cv.put(COL_DATETIME, dateTime);

        return db.insert(TABLE_NAME, null, cv);
    }

    @Override
    boolean update(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        super.update(cv);
        if (alarmId > 0) {
            cv.put(COL_ALARMID, alarmId);
        }
        if (dateTime > 0) {
            cv.put(COL_DATETIME, dateTime);
        }

        int result = db.update(TABLE_NAME, cv, COL_ID+" = ?", new String[]{String.valueOf(id)});
        return result == 1 ? true : false;
    }

    public boolean load(SQLiteDatabase db) {
        Cursor c = db.query(TABLE_NAME, null, COL_ID+" = ?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (c.moveToFirst()) {
                reset();
                super.load(c);
                alarmId = c.getLong(c.getColumnIndex(COL_ALARMID));
                dateTime = c.getLong(c.getColumnIndex(COL_DATETIME));
                return true;
            }
            return false;
        } finally {
            c.close();
        }
    }

    public static Cursor list(SQLiteDatabase db, String... args) {
        String[] cols = {COL_ID, COL_ALARMID, COL_DATETIME};
        String selection = "1 = 1";
        selection += (args!=null && args.length>0 && args[0]!=null) ? " AND "+COL_ALARMID+" = "+args[0] : "";
        selection += (args!=null && args.length>1 && args[1]!=null) ? " AND "+COL_DATETIME+" >= "+args[1] : "";
        selection += (args!=null && args.length>2 && args[2]!=null) ? " AND "+COL_DATETIME+" <= "+args[2] : "";
        String orderBy = (args!=null && args.length>3) ? args[3] : COL_DATETIME+" DESC";

        return db.query(TABLE_NAME, cols, selection, null, null, null, orderBy);
    }

    public boolean delete(SQLiteDatabase db) {
        int result = db.delete(TABLE_NAME, COL_ID+" = ?", new String[]{String.valueOf(id)});
        return result == 1 ? true : false;
    }

    public void reset() {
        super.reset();
        alarmId = 0;
        dateTime = 0;
    }

    public long getAlarmId() {
        return alarmId;
    }
    public void setAlarmId(long alarmId) {
        this.alarmId = alarmId;
    }
    public long getDateTime() {
        return dateTime;
    }
    public void setDateTime(long datetime) {
        this.dateTime = datetime;
    }

    public AlarmRepeater() {}

    public AlarmRepeater(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((obj == null) || (obj.getClass() != this.getClass()))
            return false;

        return id == ((AlarmRepeater)obj).id;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
