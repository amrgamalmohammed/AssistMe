package com.assistmeapp.amrga.assistme.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by amrga on 9/3/2016.
 */
public class AlarmInfo extends AbstractModel{

    public static final String TABLE_NAME = "alarminfo";
    public static final String COL_ID = AbstractModel.COL_ID;
    public static final String COL_ALARMID = "alarm_id";
    public static final String COL_AT = "at";
    public static final String COL_ACTION = "action";
    public static final String COL_MESSAGE = "message";
    public static final String COL_LOCATION = "location";
    public static final String COL_TAG = "tag";

    private long alarmId;
    private String at;
    private String action;
    private String message;
    private String location;
    private int tag;

    static String getSql() {
        return Util.concat("CREATE TABLE ", TABLE_NAME, " (",
                AbstractModel.getSql(),
                COL_ALARMID, " INTEGER, ",
                COL_AT, " INTEGER, ",
                COL_ACTION, " TEXT, ",
                COL_MESSAGE, " TEXT, ",
                COL_LOCATION, " TEXT, ",
                COL_TAG, " INTEGER",
                ");");
    }

    @Override
    long save(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ALARMID, alarmId);
        cv.put(COL_AT, at);
        cv.put(COL_ACTION, action);
        cv.put(COL_MESSAGE, message);
        cv.put(COL_LOCATION, location);
        cv.put(COL_TAG, tag);

        return db.insert(TABLE_NAME, null, cv);
    }

    @Override
    boolean update(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        super.update(cv);
        if (alarmId > 0) {
            cv.put(COL_ALARMID, alarmId);
        }
        if (at != null) {
            cv.put(COL_AT, at);
        }
        if (action != null) {
            cv.put(COL_ACTION, action);
        }
        if (message != null) {
            cv.put(COL_MESSAGE, message);
        }
        if (location != null) {
            cv.put(COL_LOCATION, location);
        }
        if (tag != -1) {
            cv.put(COL_TAG, tag);
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
                at = c.getString(c.getColumnIndex(COL_AT));
                action = c.getString(c.getColumnIndex(COL_ACTION));
                message = c.getString(c.getColumnIndex(COL_MESSAGE));
                location = c.getString(c.getColumnIndex(COL_LOCATION));
                tag = c.getInt(c.getColumnIndex(COL_TAG));
                return true;
            }
            return false;
        } finally {
            c.close();
        }
    }

    public static Cursor list(SQLiteDatabase db, String... args) {
        String[] cols = {COL_ID, COL_AT, COL_ACTION, COL_MESSAGE, COL_LOCATION, COL_TAG};
        String selection = "1 = 1";
        if (args!=null && args.length>0 && args[0]!=null) {
            selection += " AND "+COL_ALARMID+" = "+args[0];
        }
        String orderBy = (args!=null && args.length>1) ? args[1] : COL_AT+" DESC";

        return db.query(TABLE_NAME, cols, selection, null, null, null, orderBy);
    }

    public boolean delete(SQLiteDatabase db) {
        int result = db.delete(TABLE_NAME, COL_ID+" = ?", new String[]{String.valueOf(id)});
        return result == 1 ? true : false;
    }

    public void reset() {
        super.reset();
        alarmId = 0;
        at = null;
        action = null;
        message = null;
        location = null;
        tag = -1;
    }

    public long getAlarmId() {
        return alarmId;
    }
    public void setAlarmId(long alarmId) {
        this.alarmId = alarmId;
    }
    public String getAt() {
        return at;
    }
    public void setAt(String at) {
        this.at = at;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getAction() {
        return action;
    }
    public String getMessage() {
        return  message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public int getTag() { return tag; }
    public void setTag(int tag) { this.tag = tag; }

    public AlarmInfo() {}

    public AlarmInfo(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((obj == null) || (obj.getClass() != this.getClass()))
            return false;

        return id == ((AlarmInfo)obj).id;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
