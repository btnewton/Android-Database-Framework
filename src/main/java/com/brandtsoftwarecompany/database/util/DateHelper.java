package com.brandtsoftwarecompany.database.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Brandt on 5/16/2016.
 */
public class DateHelper {

    private static final String SQLiteTimestampFormat = "yyyy-MM-d HH:mm:ss";

    public static String toTimestamp(Date date) {
        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                SQLiteTimestampFormat, Locale.US);
        return iso8601Format.format(date);
    }

    public static Date fromTimestamp(String time) {
        DateFormat format = new SimpleDateFormat(SQLiteTimestampFormat, Locale.US);
        try {
            return format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
