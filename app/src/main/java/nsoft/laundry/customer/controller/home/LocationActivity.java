package nsoft.laundry.customer.controller.home;

import static nsoft.laundry.customer.common.Global.MACHINE_ID;
import static nsoft.laundry.customer.common.Global._isTest;
import static nsoft.laundry.customer.common.Global.getServerUrl;
import static nsoft.laundry.customer.common.Global.shopInfos;
import static nsoft.laundry.customer.common.Global.shop_id_key;
import static nsoft.laundry.customer.common.Global.shop_machine_id_key;
import static nsoft.laundry.customer.common.Global.shop_name_key;
import static nsoft.laundry.customer.common.Global.shop_review_key;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.HashMap;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.Global;
import nsoft.laundry.customer.common.OnMultiClickListener;
import nsoft.laundry.customer.controller.base.BaseActivity;
import nsoft.laundry.customer.controller.model.shop.ShopAdapter;
import nsoft.laundry.customer.controller.model.shop.ShopInfo;
import nsoft.laundry.customer.utils.location.GpsTracker;
import nsoft.laundry.customer.utils.network.HttpCall;
import nsoft.laundry.customer.utils.network.HttpRequest;

public class LocationActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private MapView viewMap;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private GpsTracker gpsTracker;
    private Double userLatitude = 0.0;
    private Double userLongitude = 0.0;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private ListView lstShops;
    private LinearLayout layBoth;
    private LinearLayout layMap;
    private LinearLayout layList;
    private ShopAdapter shopAdapter;
    private LinearLayout layListContainer;
    private LinearLayout layConfirm;
    private ShopInfo selectedShop;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_location);

        String[] permissions =  {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissionsIfNecessary(permissions);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }

        getCustomerLocation();
        getAllShops();

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

        lstShops = findViewById(R.id.lst_shops);
        layBoth = findViewById(R.id.lay_both);
        layBoth.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layListContainer.setVisibility(View.VISIBLE);
                viewMap.setVisibility(View.VISIBLE);
            }
        });
        layMap = findViewById(R.id.lay_map);
        layMap.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layListContainer.setVisibility(View.GONE);
                viewMap.setVisibility(View.VISIBLE);
            }
        });
        layList = findViewById(R.id.lay_list);
        layList.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layListContainer.setVisibility(View.VISIBLE);
                viewMap.setVisibility(View.GONE);
            }
        });
        layListContainer = findViewById(R.id.lay_list_container);
        layConfirm = findViewById(R.id.lay_confirm);
        layConfirm.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                setInformationToSystem(shop_machine_id_key, selectedShop.machineId);
                setInformationToSystem(shop_id_key, selectedShop.shopId);
                setInformationToSystem(shop_review_key, selectedShop.rate + "");
                setInformationToSystem(shop_name_key, selectedShop.shopName);
                MACHINE_ID = selectedShop.machineId;
                finish();
            }
        });
    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
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

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void getCustomerLocation() {
        gpsTracker = new GpsTracker(LocationActivity.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        userLatitude = latitude;
        userLongitude = longitude;
    }

    private void showShopsOnMap() {
        ArrayList<OverlayItem> items = new ArrayList<>();
        for (int i = 0; i < shopInfos.size(); i++) {
            ShopInfo shopInfo = shopInfos.get(i);
            GeoPoint startPoint = new GeoPoint(shopInfo.latitude, shopInfo.longitude);
            items.add(new OverlayItem(shopInfo.shopName, shopInfo.branch, startPoint));
        }

        //the overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
            new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                @Override
                public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                    //do something
                    return true;
                }
                @Override
                public boolean onItemLongPress(final int index, final OverlayItem item) {
                    return false;
                }
            }, thisContext);
        mOverlay.setFocusItemsOnTap(true);

        viewMap.getOverlays().add(mOverlay);
    }

    private void showShopsOnList() {
        shopAdapter = new ShopAdapter(thisContext,
                R.layout.item_shop, shopInfos, mListener, mCheckListener);
        shopAdapter.notifyDataSetChanged();
        lstShops.setAdapter(shopAdapter);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toArray(new String[0]),
                REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) {
                    getCustomerLocation();
                    return;
                }
                break;
        }
    }

    private void getAllShops(){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.api_prefix) + getString(R.string.url_get_shop_list);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        JSONArray responseData = response.getJSONArray("data");
                        shopInfos = new ArrayList<>();
                        for (int i = 0; i< responseData.length(); i++) {
                            JSONObject shopInfoData = responseData.getJSONObject(i);
                            String id = shopInfoData.getString("id");
                            String shopName = shopInfoData.getString("shop_name");
                            String machineId = shopInfoData.getString("machine_id");
                            String branch = shopInfoData.getString("branch");
                            String amount = shopInfoData.getString("amount");
                            int onlineStatus = shopInfoData.getInt("online_status");
                            double latitude = shopInfoData.getDouble("latitude");
                            double longitude = shopInfoData.getDouble("longitude");
                            int riderCount = shopInfoData.getInt("rider_count");
                            double rate = shopInfoData.optDouble("rate");
                            double distance = distance(latitude, longitude, userLatitude, userLongitude);
                            Log.e("LocationActivity",  "lat>>>" + latitude + ", long>>>" + longitude + ", userLatitude>>" + userLatitude + ", userLong>>>" + userLongitude);
                            Log.e("LocationActivity", distance+ "");
                            ShopInfo shopInfo = new ShopInfo(id, machineId, shopName, branch, amount, onlineStatus, latitude, longitude, riderCount, rate, distance, false);
                            shopInfos.add(shopInfo);
                        }
                        showShopsOnMap();
                        showShopsOnList();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("network exception");
                }

            }
        }.execute(httpCallPost);
    }


    private ShopAdapter.MyClickListener mListener = new ShopAdapter.MyClickListener() {
        @Override
        public void myBtnOnClick(int position, View v) {
        showToast("item click");
        }
    };

    private ShopAdapter.CheckChangeListener mCheckListener = new ShopAdapter.CheckChangeListener(){
        @Override
        public void checkBoxChanged(int position, boolean b) {
        if(b) {
            for (int i = 0; i < shopInfos.size(); i++) {
                shopInfos.get(i).checked = false;
                if (i == position) {
                    shopInfos.get(i).checked = true;
                    selectedShop = shopInfos.get(i);
                }
            }
            shopAdapter.notifyDataSetChanged();
        }
        }
    };
}