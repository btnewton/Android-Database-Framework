package com.brandtsoftwarecompany.database;

import android.content.Context;

public interface CrudObject {
    boolean delete(Context context);
    boolean save(Context context);
}
