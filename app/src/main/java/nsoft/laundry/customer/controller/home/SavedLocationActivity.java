package nsoft.laundry.customer.controller.home;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.OnMultiClickListener;
import nsoft.laundry.customer.controller.base.BaseActivity;
import nsoft.laundry.customer.controller.model.location.SavedLocationAdapter;
import nsoft.laundry.customer.controller.model.location.SavedLocationView;
import nsoft.laundry.customer.utils.database.DataBaseHelper;
import nsoft.laundry.customer.utils.database.LocationReaderContract;

public class SavedLocationActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private ListView lstSavedLocation;
    private LinearLayout layClose;
    private DataBaseHelper dbHelper;
    private ArrayList<SavedLocationView> savedLocationViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_location);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_saved_location);

        dbHelper = new DataBaseHelper(thisContext);
        initUI();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    private void initUI() {

        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });

        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(getString(R.string.title_saved_locations));
        lstSavedLocation = findViewById(R.id.lst_saved_location);
        layClose = findViewById(R.id.lay_close);
        layClose.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
                insertLocation();
            }
        });

        showSavedLocations();
    }

    private void insertLocation() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocationReaderContract.LocationEntry.LOCATION_TITLE, "Home");
        values.put(LocationReaderContract.LocationEntry.LOCATION_SUB_TITLE, "Premium");
        values.put(LocationReaderContract.LocationEntry.LOCATION_PHONE_NUMBER, "94563789");
        values.put(LocationReaderContract.LocationEntry.LOCATION_ADDRESS, "RM 301 Padlilla Delos Reyes Building");
        values.put(LocationReaderContract.LocationEntry.LOCATION_LONGITUDE, "14.7321");
        values.put(LocationReaderContract.LocationEntry.LOCATION_LATITUDE, "121.1648");
        values.put(LocationReaderContract.LocationEntry.LOCATION_SELECTED, "false");

        long newRowId = db.insert(LocationReaderContract.LocationEntry.LOCATION_TABLE, null, values);
        db.close();
        showToast("" + newRowId);
    }

    private void deleteLocation(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(LocationReaderContract.LocationEntry.LOCATION_TABLE, LocationReaderContract.LocationEntry._ID + "=?", new String[]{id});
        db.close();
    }

    private void showSavedLocations() {
        savedLocationViews = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
            BaseColumns._ID,
            LocationReaderContract.LocationEntry.LOCATION_TITLE,
            LocationReaderContract.LocationEntry.LOCATION_SUB_TITLE,
            LocationReaderContract.LocationEntry.LOCATION_PHONE_NUMBER,
            LocationReaderContract.LocationEntry.LOCATION_ADDRESS,
            LocationReaderContract.LocationEntry.LOCATION_SELECTED
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = LocationReaderContract.LocationEntry.LOCATION_TITLE + " = ?";
        String[] selectionArgs = { "My Title" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
            LocationReaderContract.LocationEntry.LOCATION_SUB_TITLE + " DESC";

        Cursor cursor = db.query(
            LocationReaderContract.LocationEntry.LOCATION_TABLE,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,                   // The columns for the WHERE clause
            null,                   // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        );
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(LocationReaderContract.LocationEntry._ID));
            String itemTitle = cursor.getString(cursor.getColumnIndexOrThrow(LocationReaderContract.LocationEntry.LOCATION_TITLE));
            String itemAddress = cursor.getString(cursor.getColumnIndexOrThrow(LocationReaderContract.LocationEntry.LOCATION_ADDRESS));
            String itemNumber = cursor.getString(cursor.getColumnIndexOrThrow(LocationReaderContract.LocationEntry.LOCATION_PHONE_NUMBER));
            String itemSelected = cursor.getString(cursor.getColumnIndexOrThrow(LocationReaderContract.LocationEntry.LOCATION_SELECTED));
            savedLocationViews.add(new SavedLocationView(itemId, itemTitle, itemAddress, itemNumber, itemSelected));
        }
        cursor.close();

        SavedLocationAdapter savedLocationAdapter = new SavedLocationAdapter(thisContext,
                R.layout.item_saved_location, savedLocationViews, mListener);
        lstSavedLocation.setAdapter(savedLocationAdapter);

    }

    private SavedLocationAdapter.MyClickListener mListener = new SavedLocationAdapter.MyClickListener() {
        @Override
        public void myBtnOnClick(int position, View v) {
            SavedLocationView savedLocationView = savedLocationViews.get(position);
            String idString = savedLocationView.id + "";
            showToast(idString);
//            deleteLocation(idString);
        }
    };
}