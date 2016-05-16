package com.brandtsoftwarecompany.database;

import android.content.ContentValues;
import android.content.Context;

import com.brandtsoftwarecompany.database.util.DateHelper;

import java.util.Date;
/**
 * Extend this class to create classes that map to database tables.
 * Created by brandt on 4/19/16.
 */
public abstract class DataObject implements CrudObject {

    private Date updatedAt;
    private Date createdAt;

    public abstract Table getTable(Context context);

    public boolean isSaved() {
        return getId() != null;
    }

    public abstract Integer getId();
    protected abstract void setId(int id);

    public boolean delete(Context context) {
        Table table = getTable(context);

        if (isSaved()) {
            Query query = new Query();
            query.andWhere(table.getPrimaryKeyColumn() + "=?", getId());
            return DBHelper.delete(table, query) == 1;
        } else {
            return true;
        }
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    protected void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    protected void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @SuppressWarnings("unchecked")
    public boolean save(Context context) {
        Table table = getTable(context);

        ContentValues contentValues = table.getContentValues(this);

        if (isSaved()) {
            Query query = new Query();
            query.andWhere(table.getPrimaryKeyColumn() + "=?", getId());

            updatedAt = new Date();
            contentValues.put(Table.UPDATED_AT, DateHelper.toTimestamp(updatedAt));
            return DBHelper.update(table, contentValues, query) == 1;
        } else {
            createdAt = new Date();
            contentValues.put(Table.CREATED_AT, DateHelper.toTimestamp(createdAt));

            int id = (int) DBHelper.insert(table, contentValues);

            setId(id);
            return id != -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        } else if (o instanceof DataObject) {
            DataObject dataObject = (DataObject) o;
            return dataObject.getId().equals(getId());
        }

        return false;
    }
}
