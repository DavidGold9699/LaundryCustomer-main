package nsoft.laundry.customer.controller.home;

import static nsoft.laundry.customer.common.Global._isTest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;

import java.util.ArrayList;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.OnMultiClickListener;
import nsoft.laundry.customer.controller.base.BaseActivity;
import nsoft.laundry.customer.utils.location.GpsTracker;

public class PickupLocationActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private LinearLayout laySet;
    private MapView viewMap;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private GpsTracker gpsTracker;
    private Double userLatitude = 0.0;
    private Double userLongitude = 0.0;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_location);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_pickup_location);

        String[] permissions =  {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissionsIfNecessary(permissions);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }

        getCustomerLocation();

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if (viewMap != null) {
            viewMap.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Configuration.getInstance().save(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if (viewMap != null) {
            viewMap.onPause();
        }
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
        txtTopTitle.setText(getString(R.string.pickup_point));
        laySet = findViewById(R.id.lay_set);
        laySet.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                showToast("set");
            }
        });

        viewMap = findViewById(R.id.view_map);
        viewMap.setUseDataConnection(true);
        viewMap.setTileSource(TileSourceFactory.MAPNIK);
        viewMap.setBuiltInZoomControls(true);
        viewMap.setMultiTouchControls(true);
        IMapController mapController = viewMap.getController();
        mapController.setZoom(12.0);
        GeoPoint startPoint = new GeoPoint(14.7178, 121.1647);
        if (userLatitude != 0.0 && !_isTest) {
            startPoint = new GeoPoint(userLatitude, userLongitude);
        }
        mapController.setCenter(startPoint);
        viewMap.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                showToast("short press latitude: " + String.valueOf(p.getLatitude()));
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                showToast("long press latitude: " + String.valueOf(p.getLatitude()));
                return false;
            }
        }));
    }

    private void getCustomerLocation() {
        gpsTracker = new GpsTracker(PickupLocationActivity.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        userLatitude = latitude;
        userLongitude = longitude;
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PickupLocationActivity.this);
        builder.setTitle("Disable location services");
        builder.setMessage("Location services are required to use this app.\n"
                + "Would you like to edit your location settings?");
        builder.setCancelable(true);
        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}