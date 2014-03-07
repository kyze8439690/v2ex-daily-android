package com.yugy.v2ex.daily.utils.database;

/**
 * Created by yugy on 13-11-29.
 */
public class Column {

    public String getColumnName() {
        return mColumnName;
    }

    public Constraint getConstraint() {
        return mConstraint;
    }

    public DataType getDataType() {
        return mDataType;
    }

    public static enum Constraint {
        UNIQUE("UNIQUE"),
        NOT("NOT"),
        NULL("NULL"),
        CHECK("CHECK"),
        FOREIGN_KEY("FOREIGN_KEY"),
        PRIMARY_KEY("PRIMARY_KEY"),
        PRIMARY_KEY_AUTOINCREMENT("PRIMARY_KEY_AUTOINCREMENT");

        private String mValue;

        private Constraint(String value){
            mValue = value;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }

    public static enum DataType{
        NULL, INTEGER, REAL, TEXT, BLOB
    }

    private String mColumnName;
    private Constraint mConstraint;
    private DataType mDataType;

    public Column(String columnName, Constraint constraint, DataType dataType){
        mColumnName = columnName;
        mConstraint = constraint;
        mDataType = dataType;
    }


}
