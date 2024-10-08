package nsoft.laundry.customer.utils.database;

import static nsoft.laundry.customer.utils.database.LocationReaderContract.LocationEntry;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "laundrycustomer.db";
    private static final String SQL_CREATE_LOCATION =
        "CREATE TABLE " + LocationEntry.LOCATION_TABLE + " (" +
            LocationEntry._ID + " INTEGER PRIMARY KEY," +
            LocationEntry.LOCATION_TITLE + " TEXT," +
            LocationEntry.LOCATION_SUB_TITLE + " TEXT," +
            LocationEntry.LOCATION_PHONE_NUMBER + " TEXT," +
            LocationEntry.LOCATION_ADDRESS + " TEXT," +
            LocationEntry.LOCATION_LONGITUDE + " TEXT," +
            LocationEntry.LOCATION_LATITUDE + " TEXT," +
            LocationEntry.LOCATION_SELECTED + " TEXT)";
    private static final String SQL_DELETE_LOCATION =
        "DROP TABLE IF EXISTS " + LocationEntry.LOCATION_TABLE;

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL(SQL_DELETE_LOCATION);
            onCreate(sqLiteDatabase);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
