package com.assistmeapp.amrga.assistme.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by amrga on 9/2/2016.
 */
public abstract class AbstractModel {

    static final String COL_ID = "_id";
    protected  long id;

    abstract long save(SQLiteDatabase db);
    abstract  boolean update(SQLiteDatabase db);

    static String getSql() {
        return Util.concat(COL_ID, " INTEGER PRIMARY KEY AUTOINCREMENT, ");
    }

    void update(ContentValues cv) {
        cv.put(COL_ID, id);
    }

    void load(Cursor c) {
        id = c.getLong(c.getColumnIndex(COL_ID));
    }

    protected void reset() {
        this.id = 0;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long persist(SQLiteDatabase db) {
        if (this.id > 0) {
            return update(db) ? id : 0;
        }
        else {
            return save(db);
        }
    }
}
