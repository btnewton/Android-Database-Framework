package com.brandtsoftwarecompany.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by brandt on 2/10/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = "DBHelper";

    // Increase to getCurrent app call onUpgrade
    private static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME = "database.db";
    private static final String UPGRADE_SCRIPTS_PATH = "upgrade_scripts";

    // Singleton DBHandler instance
    private static DBHelper mInstance = null;
    private static DBHelper mTestInstance = null;

    private Context context;

    private void executeSQLScript(SQLiteDatabase database, String scriptAsset) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;

        try {
            InputStream inputStream = context.getAssets().open(scriptAsset);
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            String[] createScript = outputStream.toString().split(";");
            for (String aCreateScript : createScript) {
                String sqlStatement = aCreateScript.trim();
                if (sqlStatement.length() > 0) {
                    database.execSQL(sqlStatement + ";");
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to show script: '" + scriptAsset + "'\n " + Log.getStackTraceString(e));
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Failed to execute script: '" + scriptAsset + "'\n " + Log.getStackTraceString(e));
        }
    }

    private DBHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
        this.context = context;
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context.getApplicationContext(), DATABASE_NAME);
        }

        return mInstance;
    }
    public static synchronized DBHelper getTestInstance(Context context) {
        if (mTestInstance == null) {
            mTestInstance = new DBHelper(context.getApplicationContext(), "test." + DATABASE_NAME);
        }
        return mTestInstance;
    }

    public static SQLiteDatabase getWritable(Context context) {
        return DBHelper.getInstance(context).getWritableDatabase();
    }

    public static int delete(Table table, Query query) {
        return getWritable(table.getContext()).delete(table.getName(), query.getWhereClause(), query.getWhereArgs());
    }
    public static long insert(Table table, ContentValues contentValues) {
        return getWritable(table.getContext()).insert(table.getName(), null, contentValues);
    }
    public static int update(Table table, ContentValues contentValues, Query query) {
        return getWritable(table.getContext()).update(table.getName(), contentValues, query.getWhereClause(), query.getWhereArgs());
    }
    public static Cursor select(Table table, Query query) {
        return getReadable(table.getContext()).query(query.isDistinct(), table.getName(), table.getColumns(), query.getWhereClause(), query.getWhereArgs(), query.getGroupBy(), query.getHaving(), query.getOrderBy(), query.getLimit());
    }

    public static void truncate(Table table) {
        getWritable(table.getContext()).execSQL("DELETE FROM " + table.getName());
    }

    public static SQLiteDatabase getReadable(Context context) {
        return DBHelper.getInstance(context).getReadableDatabase();
    }

    public static synchronized DBHelper getTestInstance(Context context, String DatabaseName) {
        if (mTestInstance == null) {
            // Use application context to prevent leak in activity's context
            mTestInstance = new DBHelper(context.getApplicationContext(), DatabaseName);
        }

        return mTestInstance;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        runScriptsFrom(db, 0);
    }

    private void runScriptsFrom(SQLiteDatabase database, int startingScriptNumber) {
        try {
            String[] scripts = context.getAssets().list(UPGRADE_SCRIPTS_PATH);
            for (String script : scripts) {
                int scriptNumber = Integer.valueOf(script.substring(0, 3));
                if (startingScriptNumber <= scriptNumber) {
                    executeSQLScript(database, UPGRADE_SCRIPTS_PATH + "/" + script);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        runScriptsFrom(db, oldVersion + 1);
        Log.i("DBHandler", "Database updated! (" + oldVersion + " -> " + newVersion + ")");
    }
}
