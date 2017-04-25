package com.assistmeapp.amrga.assistme.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by amrga on 9/2/2016.
 */
public class Alarm extends AbstractModel {

    public static final String TABLE_NAME = "alarm";
    public static final String COL_ID = AbstractModel.COL_ID;
    public static final String COL_CREATEDTIME = "created_time";
    public static final String COL_MODIFIEDTIME = "modified_time";
    public static final String COL_TITLE = "title";
    public static final String COL_FROMDATE = "from_date";
    public static final String COL_TODATE = "to_date";
    public static final String COL_RULE = "rule";
    public static final String COL_INTERVAL = "interval";

    private long createdTime;
    private long modifiedTime;
    private String title;
    private String fromDate;
    private String toDate;
    private String rule;
    private String interval;
    private List<AlarmInfo> occurrences;

    static String getSql() {
        return Util.concat("CREATE TABLE ", TABLE_NAME, " (",
                AbstractModel.getSql(),
                COL_CREATEDTIME, " INTEGER, ",
                COL_MODIFIEDTIME, " INTEGER, ",
                COL_TITLE, " TEXT, ",
                COL_FROMDATE, " DATE, ",
                COL_TODATE, " DATE, ",
                COL_RULE, " TEXT, ",
                COL_INTERVAL, " TEXT",
                ");");
    }

    @Override
    long save(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        long now = System.currentTimeMillis();
        cv.put(COL_CREATEDTIME, now);
        cv.put(COL_MODIFIEDTIME, now);
        cv.put(COL_TITLE, title==null ? "" : title);
        cv.put(COL_FROMDATE, fromDate);
        cv.put(COL_TODATE, toDate);
        cv.put(COL_RULE, rule);
        cv.put(COL_INTERVAL, interval);

        return db.insert(TABLE_NAME, null, cv);
    }

    @Override
    boolean update(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        super.update(cv);
        cv.put(COL_MODIFIEDTIME, System.currentTimeMillis());
        if (title != null)
            cv.put(COL_TITLE, title);
        if (fromDate != null)
            cv.put(COL_FROMDATE, fromDate);
        if (toDate != null)
            cv.put(COL_TODATE, toDate);
        if (rule != null)
            cv.put(COL_RULE, rule);
        if (interval != null)
            cv.put(COL_INTERVAL, interval);

        int result = db.update(TABLE_NAME, cv, COL_ID+" = ?", new String[]{String.valueOf(id)});
        return result == 1 ? true : false;
    }

    public boolean load(SQLiteDatabase db) {
        Cursor c = db.query(TABLE_NAME, null, COL_ID+" = ?", new String[]{String.valueOf(id)}, null, null, null);
        try {
           if (c.moveToFirst()) {
               reset();
               super.load(c);
               createdTime = c.getLong(c.getColumnIndex(COL_CREATEDTIME));
               modifiedTime = c.getLong(c.getColumnIndex(COL_MODIFIEDTIME));
               title = c.getString(c.getColumnIndex(COL_TITLE));
               fromDate = c.getString(c.getColumnIndex(COL_FROMDATE));
               toDate = c.getString(c.getColumnIndex(COL_TODATE));
               rule = c.getString(c.getColumnIndex(COL_RULE));
               interval = c.getString(c.getColumnIndex(COL_INTERVAL));

               return true;
           }
            return false;
        }
        finally {
            c.close();
        }
    }

    public static Cursor list(SQLiteDatabase db) {
        String[] cols = {COL_ID, COL_TITLE};

        return db.query(TABLE_NAME, cols, null, null, null, null, COL_CREATEDTIME+" DESC");
    }

    public boolean delete(SQLiteDatabase db) {
        boolean status = false;
        db.beginTransaction();
        try {
            int result = db.delete(TABLE_NAME, COL_ID+" = ?", new String[]{String.valueOf(id)});
            status = result == 1 ? true : false;
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }

        return status;
    }

    public void reset() {
        super.reset();
        createdTime = 0;
        modifiedTime = 0;
        title = null;
        fromDate = null;
        toDate = null;
        rule = null;
        interval = null;
        occurrences = null;
    }

    public long getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
    public long getModifiedTime() {
        return modifiedTime;
    }
    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getFromDate() {
        return fromDate;
    }
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }
    public String getToDate() {
        return toDate;
    }
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
    public String getRule() {
        return rule;
    }
    public void setRule(String rule) {
        this.rule = rule;
    }
    public String getInterval() {
        return interval;
    }
    public void setInterval(String interval) {
        this.interval = interval;
    }
    public List<AlarmInfo> getOccurrences() {
        return occurrences;
    }
    public void setOccurrences(List<AlarmInfo> occurrences) {
        this.occurrences = occurrences;
    }

    public Alarm() {}

    public Alarm(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((obj == null) || (obj.getClass() != this.getClass()))
            return false;

        return id == ((Alarm)obj).id;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
