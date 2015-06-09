package com.example.sec.mymap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DBNAME = "dejavu.db";
    private static final int DBversion = 1;
    public static final String TABLE_DEJAVU = "dejavu";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LON = "lon";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_DATE = "date";

    private static final String CREATE_TABLE_SQL =
            "create table " + TABLE_DEJAVU + " "
            + "(" + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_LAT + " real not null,"
            + COLUMN_LON + " real not null,"
            + COLUMN_ADDRESS + " text null,"
            + COLUMN_DATE + " text not null)";

    DatabaseHelper(Context context){
        super(context,DBNAME,null,DBversion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
