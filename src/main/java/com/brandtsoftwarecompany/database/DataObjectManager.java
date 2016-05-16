package com.brandtsoftwarecompany.database;

import android.content.Context;
import android.database.Cursor;

import java.lang.reflect.Array;

/**
 * Created by brandt on 4/19/16.
 */
public class DataObjectManager <K extends DataObject> {

    private final Table table;
    private final Class<K> type;

    protected DataObjectManager(Class<K> type, Table table) {
        this.type = type;
        this.table = table;
    }

    protected Context getContext() {
        return table.getContext();
    }

    public K[] all() {
        return all(new Query());
    }
//    public K[] all(Query query) {
//        return (K[]) DBHelper.selectMultiple(table, query);
//    }
//    public K[] all(Query query) {
//        return (K[]) DBHelper.selectMultiple(table, query);
//    }

    @SuppressWarnings("unchecked")
    public K[] all(Query query) {
        Cursor cursor = DBHelper.select(table, query);

        K[] dataObjects = (K[]) Array.newInstance(type, cursor.getCount());

        cursor.moveToFirst();

        for (int i = 0; i < dataObjects.length; i++) {
            dataObjects[i] = (K) table.fromCursor(cursor);
            cursor.moveToNext();
        }

        return dataObjects;
    }

    @SuppressWarnings("unchecked")
    public K select(Query query) {
        query.setLimit(1);

        Cursor cursor = null;
        K dataObject = null;

        try {
            cursor = DBHelper.select(table, query);
            if (cursor.moveToFirst()) {
                dataObject = (K) table.fromCursor(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return dataObject;

    }
//    public static DataObject[] selectMultiple(Table table, Query query) {
//        Cursor cursor = selectToCursor(table, query);
//
//        DataObject[] dataObjects = new DataObject[cursor.getCount()];
//
//        cursor.moveToFirst();
//
//        for (int i = 0; i < dataObjects.length; i++) {
//            dataObjects[i] = table.fromCursor(cursor);
//            cursor.moveToNext();
//        }
//
//        return dataObjects;
//    }

    public boolean deleteAll(Query query) {
        return DBHelper.delete(table, query) > 0;
    }

    @SuppressWarnings("unchecked")
    public K findById(int id) {
        Query query = new Query();
        query.addAndWhereClause(table.getPrimaryKeyColumn() + "=?", id);
        return (K) select(query);
    }
}
