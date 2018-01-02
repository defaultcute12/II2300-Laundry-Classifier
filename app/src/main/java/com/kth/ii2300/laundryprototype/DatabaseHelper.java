package com.kth.ii2300.laundryprototype;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Elvar on 27.12.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Laundry.db";

    private static final String SQL_CREATE_GARMENT_TABLE =
            "CREATE TABLE Garment ( " +
                    "Garment_Id INTEGER INTEGER PRIMARY KEY, " +
                    "Fabric TEXT, " +
                    "Name TEXT, " +
                    "Weight INTEGER, " +
                    "MaxWashTemp INTEGER, " +
                    "SuggestedWashTemp INTEGER, " +
                    "SpinningLimit INTEGER, " +
                    "YarnTwist INTEGER, " +
                    "IsColorBleedSensitive);";

    private static final String SQL_CREATE_GARMENTIMAGE_TABLE =
            "CREATE TABLE GarmentImage ( " +
                    "Name Text, " +
                    "ImageUri Text," +
                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP);";

    private static final String insertStringHeader =
            "INSERT INTO Garment ( " +
                    "Fabric, " +
                    "Name, " +
                    "Weight, " +
                    "MaxWashTemp, " +
                    "SuggestedWashTemp, " +
                    "SpinningLimit, " +
                    "YarnTwist, " +
                    "IsColorBleedSensitive) ";

    private static final String SQL_INSERT_COTTON =
            insertStringHeader +
                    "VALUES ( " +
                    "'Cotton'," +
                    "'Cotton'," +
                    "0," +
                    "60," +
                    "60," +
                    "1800," +
                    "333," +
                    "0)";

    private static final String SQL_INSERT_DENIM =
            insertStringHeader +
                    "VALUES ( " +
                    "'Denim'," +
                    "'Denim'," +
                    "0," +
                    "40," +
                    "40," +
                    "900," +
                    "99," +
                    "0)";

    private static final String SQL_INSERT_NYLON =
            insertStringHeader +
                    "VALUES ( " +
                    "'Nylon'," +
                    "'Nylon'," +
                    "0," +
                    "30," +
                    "30," +
                    "1200," +
                    "12," +
                    "1)";

    private static final String SQL_INSERT_SILK =
            insertStringHeader +
                    "VALUES ( " +
                    "'Silk'," +
                    "'Silk'," +
                    "0," +
                    "15," +
                    "15," +
                    "300," +
                    "134," +
                    "1)";

    private static final String SQL_DELETE_GARMENT_TABLE =
            "DROP TABLE IF EXISTS Garment";

    private static final String SQL_DELETE_GARMENTIMAGE_TABLE =
            "DROP TABLE IF EXISTS GarmentImage";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_GARMENT_TABLE);
        db.execSQL(SQL_CREATE_GARMENTIMAGE_TABLE);
        db.execSQL(SQL_INSERT_COTTON);
        db.execSQL(SQL_INSERT_DENIM);
        db.execSQL(SQL_INSERT_NYLON);
        db.execSQL(SQL_INSERT_SILK);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_GARMENTIMAGE_TABLE);
        db.execSQL(SQL_DELETE_GARMENT_TABLE);
        db.execSQL(SQL_CREATE_GARMENT_TABLE);
        db.execSQL(SQL_CREATE_GARMENTIMAGE_TABLE);
        db.execSQL(SQL_INSERT_COTTON);
        db.execSQL(SQL_INSERT_DENIM);
        db.execSQL(SQL_INSERT_NYLON);
        db.execSQL(SQL_INSERT_SILK);
    }
}
