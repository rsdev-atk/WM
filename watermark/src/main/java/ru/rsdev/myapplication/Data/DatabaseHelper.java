package ru.rsdev.myapplication.Data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String DATABASE_NAME = "watermarkdatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE_OBJECTS = "objects";
    public static final String COLUMN_OBJECTS_NAME = "name";


    public static final String DATABASE_TABLE_IMAGES = "images";
    public static final String COLUMN_IMAGES_ID_IMAGES = "id_images";
    public static final String COLUMN_IMAGES_URL = "url";

    private static final String DATABASE_CREATE_TABLE_OBJECTS = "create table "
            + DATABASE_TABLE_OBJECTS + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + COLUMN_OBJECTS_NAME
            + " text not null)";

    private static final String DATABASE_CREATE_TABLE_IMAGES = "create table "
            + DATABASE_TABLE_IMAGES + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + COLUMN_IMAGES_ID_IMAGES
            + " integer, " + COLUMN_IMAGES_URL
            + " text)";




    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_TABLE_OBJECTS);
        sqLiteDatabase.execSQL(DATABASE_CREATE_TABLE_IMAGES);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
