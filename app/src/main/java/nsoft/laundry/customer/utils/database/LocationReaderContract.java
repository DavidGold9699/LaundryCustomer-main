package nsoft.laundry.customer.utils.database;

import android.provider.BaseColumns;

public final class LocationReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private LocationReaderContract() {}

    /* Inner class that defines the table contents */
    public static class LocationEntry implements BaseColumns {
        public static final String LOCATION_TABLE = "location";
        public static final String LOCATION_TITLE = "title";
        public static final String LOCATION_SUB_TITLE = "sub_title";
        public static final String LOCATION_PHONE_NUMBER = "phone_number";
        public static final String LOCATION_ADDRESS = "address";
        public static final String LOCATION_LONGITUDE = "longitude";
        public static final String LOCATION_LATITUDE = "latitude";
        public static final String LOCATION_SELECTED = "selected";
    }
}
