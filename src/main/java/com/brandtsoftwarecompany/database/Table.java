package com.brandtsoftwarecompany.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by brandt on 4/19/16.
 */
public abstract class Table<T extends DataObject> {

    public static final String UPDATED_AT = "updatedAt";
    public static final String CREATED_AT = "createdAt";

    private Context context;

    public Table(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public abstract String getName();
    public abstract String getPrimaryKeyColumn();
    public abstract ContentValues getContentValues(T object);
    public abstract String[] getColumns();
    public abstract T fromCursor(Cursor cursor);

    public void truncate() {
        DBHelper.truncate(this);
    }
}
