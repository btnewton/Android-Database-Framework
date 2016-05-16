package com.brandtsoftwarecompany.database;

import android.content.Context;

/**
 * Extend this class to create classes that map to database tables.
 * Created by brandt on 4/19/16.
 */
public abstract class DataObject implements CrudObject {

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

    @SuppressWarnings("unchecked")
    public boolean save(Context context) {
        Table table = getTable(context);

        if (isSaved()) {
            Query query = new Query();
            query.andWhere(table.getPrimaryKeyColumn() + "=?", getId());
            return DBHelper.update(table, table.getContentValues(this), query) == 1;
        } else {
            int id = (int) DBHelper.insert(table, table.getContentValues(this));
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
