package com.brandtsoftwarecompany.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandt on 2/10/16.
 */
public class Query {

    private final static int AND_OP = 0;
    private final static int OR_OP = 1;
    public final static int DEFAULT_RESULT_LIMIT = 500;

    public final static int COLLATION_BINARY = 0;
    public final static int COLLATION_NOCASE = 1;
    public final static int COLLATION_RTRIM = 2;

    private int limit;
    private StringBuilder whereClause = new StringBuilder();
    private String orderBy;
    private List<String> whereArgs = new ArrayList<>();
    private boolean distinct;

    public Query() {
        limit = DEFAULT_RESULT_LIMIT;
    }

    public Query setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public Query setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public Query addAndWhereClause(String whereClause) {
        return addAndWhereClause(whereClause, null);
    }
    public Query addAndWhereClause(String whereClause, String whereArg) {
        return addWhereClause(AND_OP, whereClause, whereArg);
    }
    public Query addAndWhereClause(String whereClause, int whereArg) {
        return addAndWhereClause(whereClause, Integer.toString(whereArg));
    }
    public Query addAndWhereClause(String whereClause, double whereArg) {
        return addAndWhereClause(whereClause, Double.toString(whereArg));
    }
    public Query addOrWhereClause(String whereClause) {
        return addOrWhereClause(whereClause, null);
    }
    public Query addOrWhereClause(String whereClause, String whereArg) {
        return addWhereClause(OR_OP, whereClause, whereArg);
    }

    private Query addWhereClause(int operator, String whereClause, String whereArg) {
        if (this.whereClause.length() > 0) {
            this.whereClause.append(getOperator(operator));
        }
        this.whereClause.append(whereClause);

        if (whereArg != null) {
            whereArgs.add(whereArg);
        }
        return this;
    }

    public Query overrideDefaultResultLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public static String wildCardWrapper(String value) {
        return "%" + value + "%";
    }

    public String getWhereClause() {
        return  whereClause.toString();
    }

    public String[] getWhereArgs() {
        String[] whereArgs = new String[this.whereArgs.size()];
        for (int i = 0; i < whereArgs.length; i++) {
            whereArgs[i] = this.whereArgs.get(i);
        }
        return whereArgs;
    }

    public Query setOrderBy(String orderBy, boolean orderAsc) {
        return setOrderBy(orderBy, orderAsc, -1);
    }
    public Query setOrderBy(String orderBy, boolean orderAsc, int collation) {
        String collationString;

        switch (collation) {
            case COLLATION_BINARY:
                collationString = "COLLATE BINARY";
                break;
            case COLLATION_NOCASE:
                collationString = "COLLATE NOCASE";
                break;
            case COLLATION_RTRIM:
                collationString = "COLLATE RTRIM";
                break;
            default:
                collationString = "";
        }

        if (orderBy == null)
            this.orderBy = null;
        else
            this.orderBy = orderBy + " " + collationString + " " + ((orderAsc)? "ASC" : "DESC") + " ";

        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public String getLimit() {
        return Integer.toString(limit);
    }

    private String getOperator(int operatorCode) {
        if (operatorCode == AND_OP) {
            return " AND ";
        } else {
            return " OR ";
        }
    }

    public String logWhereClause() {
        try {
            StringBuilder query = new StringBuilder();
            int argCounter = 0;
            for (int i = 0; i < whereClause.length(); i++) {
                if (whereClause.charAt(i) == '?') {
                    String arg = whereArgs.get(argCounter);

                    if (!arg.matches("[-+]?\\d*\\.?\\d+"))
                        arg = "'" + arg + "'";

                    query.append(arg);
                    argCounter++;
                } else {
                    query.append(whereClause.charAt(i));
                }
            }

            return "WHERE " + query.toString() + " ORDER BY " + getOrderBy();
        } catch (Exception exception) {
            return "An exception occurred when debugging searchString: " + exception.getMessage();
        }
    }
}
