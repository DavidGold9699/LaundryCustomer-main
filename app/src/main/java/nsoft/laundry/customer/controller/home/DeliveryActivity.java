package nsoft.laundry.customer.controller.home;

import static nsoft.laundry.customer.common.Global.PAYMENT_METHOD_GET;
import static nsoft.laundry.customer.common.Global.PICKUP_GET_SERVICES;
import static nsoft.laundry.customer.common.Global.connectCount;
import static nsoft.laundry.customer.common.Global.connectionTimer;
import static nsoft.laundry.customer.common.Global.getServerUrl;
import static nsoft.laundry.customer.common.Global.pickupServiceItemViews;
import static nsoft.laundry.customer.common.Global.pickupServiceKiloViews;
import static nsoft.laundry.customer.common.Global.pickupServiceLoadViews;
import static nsoft.laundry.customer.common.Global.pickupServiceViews;
import static nsoft.laundry.customer.common.Global.shopInfos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.Global;
import nsoft.laundry.customer.common.OnMultiClickListener;
import nsoft.laundry.customer.controller.base.BaseActivity;
import nsoft.laundry.customer.controller.model.RequestInfo;
import nsoft.laundry.customer.controller.model.load.LoadAdapter;
import nsoft.laundry.customer.controller.model.load.LoadView;
import nsoft.laundry.customer.controller.model.location.LocationViewAdapter;
import nsoft.laundry.customer.controller.model.location.SavedLocationView;
import nsoft.laundry.customer.controller.model.shop.ShopAdapter;
import nsoft.laundry.customer.controller.model.washer.WasherViewAdapter;
import nsoft.laundry.customer.utils.database.DataBaseHelper;
import nsoft.laundry.customer.utils.database.LocationReaderContract;
import nsoft.laundry.customer.utils.dialog.CustomProgress;
import nsoft.laundry.customer.utils.network.HttpCall;
import nsoft.laundry.customer.utils.network.HttpRequest;

public class DeliveryActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private LinearLayout layBtnLocation;
    private LinearLayout layBtnServices;
    private LinearLayout layBtnRider;
    private LinearLayout layBtnPayment;
    private LinearLayout layLocation;
    private LinearLayout layServices;
    private LinearLayout layRider;
    private LinearLayout layPayment;
    private LinearLayout laySaved;
    private LinearLayout layBtnServicesLoad;
    private LinearLayout layBtnServicesKilo;
    private LinearLayout layBtnServicesItem;
    private LinearLayout layServicesLoad;
    private LinearLayout layServicesKilo;
    private LinearLayout layServicesItem;
    private ListView lstServicesLoad;
    private ListView lstServicesKilo;
    private ListView lstServicesItem;
    private LinearLayout layCash;
    private LinearLayout layGCash;
    private LinearLayout layPayMaya;
    private RecyclerView rvSavedLocation;
    private LinearLayout layLocate;
    private LocationViewAdapter locationViewAdapter;
    private ArrayList<SavedLocationView> savedLocationViews;

    private   static ArrayList<RequestInfo> requests = new ArrayList<RequestInfo>();
    private DataBaseHelper dbHelper;

    private static DeliveryActivity instance;
    public static DeliveryActivity getInstance() {
        return instance;
    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        instance = this;

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_delivery);
        dbHelper = new DataBaseHelper(thisContext);

        requests = new ArrayList<>();

        sendRequestToServer(PAYMENT_METHOD_GET, "");
        sendRequestToServer(PICKUP_GET_SERVICES, "");
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSavedLocation();
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
        txtTopTitle.setText(getString(R.string.home_pickup));
        layLocation = findViewById(R.id.lay_location);
        layServices = findViewById(R.id.lay_services);
        layRider = findViewById(R.id.lay_rider);
        layPayment = findViewById(R.id.lay_payment);
        layBtnLocation = findViewById(R.id.lay_btn_location);
        layBtnLocation.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layBtnLocation.setBackgroundResource(R.drawable.btn_secondary);
                layBtnServices.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnRider.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnPayment.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layLocation.setVisibility(View.VISIBLE);
                layServices.setVisibility(View.GONE);
                layRider.setVisibility(View.GONE);
                layPayment.setVisibility(View.GONE);
            }
        });
        layBtnServices = findViewById(R.id.lay_btn_services);
        layBtnServices.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layBtnLocation.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnServices.setBackgroundResource(R.drawable.btn_secondary);
                layBtnRider.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnPayment.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layLocation.setVisibility(View.GONE);
                layServices.setVisibility(View.VISIBLE);
                layRider.setVisibility(View.GONE);
                layPayment.setVisibility(View.GONE);

            }
        });
        layBtnRider = findViewById(R.id.lay_btn_rider);
        layBtnRider.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layBtnLocation.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnServices.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnRider.setBackgroundResource(R.drawable.btn_secondary);
                layBtnPayment.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layLocation.setVisibility(View.GONE);
                layServices.setVisibility(View.GONE);
                layRider.setVisibility(View.VISIBLE);
                layPayment.setVisibility(View.GONE);

            }
        });
        layBtnPayment = findViewById(R.id.lay_btn_payment);
        layBtnPayment.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layBtnLocation.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnServices.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnRider.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnPayment.setBackgroundResource(R.drawable.btn_secondary);
                layLocation.setVisibility(View.GONE);
                layServices.setVisibility(View.GONE);
                layRider.setVisibility(View.GONE);
                layPayment.setVisibility(View.VISIBLE);

            }
        });
        laySaved = findViewById(R.id.lay_saved);
        laySaved.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoSavedLocationActivity();
            }
        });
        layServicesLoad = findViewById(R.id.lay_services_load);
        layServicesKilo = findViewById(R.id.lay_services_kilo);
        layServicesItem = findViewById(R.id.lay_services_item);
        layBtnServicesLoad = findViewById(R.id.lay_btn_services_load);
        layBtnServicesLoad.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layBtnServicesLoad.setBackgroundResource(R.drawable.btn_secondary);
                layBtnServicesKilo.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnServicesItem.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layServicesLoad.setVisibility(View.VISIBLE);
                layServicesKilo.setVisibility(View.GONE);
                layServicesItem.setVisibility(View.GONE);
            }
        });
        layBtnServicesKilo = findViewById(R.id.lay_btn_services_kilo);
        layBtnServicesKilo.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layBtnServicesLoad.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnServicesKilo.setBackgroundResource(R.drawable.btn_secondary);
                layBtnServicesItem.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layServicesLoad.setVisibility(View.GONE);
                layServicesKilo.setVisibility(View.VISIBLE);
                layServicesItem.setVisibility(View.GONE);

            }
        });
        layBtnServicesItem = findViewById(R.id.lay_btn_services_item);
        layBtnServicesItem.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layBtnServicesLoad.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnServicesKilo.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnServicesItem.setBackgroundResource(R.drawable.btn_secondary);
                layServicesLoad.setVisibility(View.GONE);
                layServicesKilo.setVisibility(View.GONE);
                layServicesItem.setVisibility(View.VISIBLE);
            }
        });
        lstServicesLoad = findViewById(R.id.lst_services_load);
        lstServicesKilo = findViewById(R.id.lst_services_kilo);
        lstServicesItem = findViewById(R.id.lst_services_item);
        layCash = findViewById(R.id.lay_cash);
        layCash.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                showToast("cash");
            }
        });
        layGCash = findViewById(R.id.lay_gcash);
        layGCash.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                showToast("gcash");
            }
        });
        layPayMaya = findViewById(R.id.lay_paymaya);
        layPayMaya.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                showToast("paymaya");
            }
        });
        layLocate = findViewById(R.id.lay_locate);
        layLocate.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoPickupLocationActivity();
            }
        });

        rvSavedLocation = findViewById(R.id.rv_saved_location);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSavedLocation.setLayoutManager(horizontalLayoutManager);
    }

    private void gotoPickupLocationActivity(){
        Intent intent = new Intent(thisContext, PickupLocationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showSavedLocation(){
        savedLocationViews = new ArrayList<>();
        try{
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] projection = {
                BaseColumns._ID,
                LocationReaderContract.LocationEntry.LOCATION_TITLE,
                LocationReaderContract.LocationEntry.LOCATION_SUB_TITLE,
                LocationReaderContract.LocationEntry.LOCATION_PHONE_NUMBER,
                LocationReaderContract.LocationEntry.LOCATION_ADDRESS,
                LocationReaderContract.LocationEntry.LOCATION_SELECTED
            };
            String sortOrder =
                LocationReaderContract.LocationEntry._ID + " DESC";

            Cursor cursor = db.query(
                LocationReaderContract.LocationEntry.LOCATION_TABLE,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
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
        }catch (SQLiteException e) {
            e.printStackTrace();
        }

        locationViewAdapter = new LocationViewAdapter(this, savedLocationViews, mCheckListener);
        rvSavedLocation.setAdapter(locationViewAdapter);
    }

    private void showServicesLoads() {
        LoadAdapter loadAdapter = new LoadAdapter(thisContext,
                R.layout.item_services_load, pickupServiceLoadViews, mListener);
        lstServicesLoad.setAdapter(loadAdapter);
    }
    private void showServicesKilo() {
        LoadAdapter loadAdapter = new LoadAdapter(thisContext,
                R.layout.item_services_load, pickupServiceKiloViews, mListener);
        lstServicesKilo.setAdapter(loadAdapter);
    }
    private void showServicesItem() {
        LoadAdapter loadAdapter = new LoadAdapter(thisContext,
                R.layout.item_services_load, pickupServiceItemViews, mListener);
        lstServicesItem.setAdapter(loadAdapter);
    }

    private void gotoSavedLocationActivity() {
        Intent intent = new Intent(this, SavedLocationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private LoadAdapter.MyClickListener mListener = new LoadAdapter.MyClickListener() {
        @Override
        public void myBtnOnClick(int position, View v) {
        showToast("item click");
        }
    };

    private void startConnection() {
        showConnectingUI();
        initConnectionTimer();
    }

    private void showConnectingUI(){
        showProgressDialog(getString(R.string.loading_press));
    }

    public void initConnectionTimer() {
        connectCount = 0;
        connectionTimer = new Timer();
        connectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                connectCount++;
                if (connectCount > Global.SERVER_CONNECTION_COUNT){
                    stopConnection();
                }
                else{
                    if (requests.size() > 0) {
                        for (int i = 0; i < requests.size(); i++) {
                            RequestInfo requestInfo = requests.get(i);
                            if (!requestInfo.success) {
                                getRequestData(requestInfo.uuid, requestInfo.sqlNo);
                            }
                        }
                    }
                }
            }
        }, 1000, 1500);
    }

    private void stopConnection(){
        stopConnectionTimer();
    }

    public void stopConnectionTimer(){
        if(connectionTimer != null){
            connectionTimer.cancel();
            connectionTimer = null;
        }
    }

    private void connectionSuccess(){
        stopConnectionTimer();
        showConnectSuccessUI();
    }

    private void showConnectFailedUI(){
        CustomProgress.dismissDialog();
    }

    private void showConnectSuccessUI(){
        CustomProgress.dismissDialog();
    }


    public void getRequestStatus(int sqlNo, String res) {
        try {
            JSONObject response = new JSONObject(res);
            int responseCode = (int) response.get("code");
            if (responseCode == Global.RESULT_SUCCESS) {
                JSONObject responseData =  response.getJSONObject("data");
                String requestUUID = responseData.getString("uuid");
                requests.add(new RequestInfo(sqlNo, requestUUID, false));
                startConnection();
            }
            else if (responseCode == Global.RESULT_FAILED) {
                CustomProgress.dismissDialog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            CustomProgress.dismissDialog();
        }
    }

    private void getRequestData(final String uniqueId, final int sqlNo){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.api_prefix);
        if (sqlNo == PICKUP_GET_SERVICES) {
            url += getString(R.string.url_get_pickup_services);
        }
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("unique_id", uniqueId);
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        for (int i = 0; i < requests.size(); i++) {
                            RequestInfo requestInfo = requests.get(i);
                            if (requestInfo.uuid.equals(uniqueId)  && requestInfo.sqlNo == sqlNo) {
                                requestInfo.success = true;
                                break;
                            }
                        }
                        if (sqlNo == PICKUP_GET_SERVICES) {
                            JSONArray responseData =  response.getJSONArray("data");
                            savePickupServices(responseData);
                        }
                        connectionSuccess();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        if (connectCount == Global.SERVER_CONNECTION_COUNT){
                            showConnectFailedUI();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("network exception");
                }

            }
        }.execute(httpCallPost);
    }

    private void savePickupServices(JSONArray response) {
        try {
            pickupServiceViews = new ArrayList<>();
            pickupServiceLoadViews = new ArrayList<>();
            pickupServiceKiloViews = new ArrayList<>();
            pickupServiceItemViews = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONArray serviceArray = response.getJSONArray(i);
                int type = 1;
                String strType = serviceArray.getString(0);
                String code = serviceArray.getString(1);
                String name = serviceArray.getString(2);
                String price = serviceArray.getString(3);
                String description = serviceArray.getString(4);
                if (strType.equals("PER LOAD")) {
                    type = 1;
                }
                else if (strType.equals("PER KILO")) {
                    type = 2;
                }
                else if (strType.equals("PER ITEM")) {
                    type = 3;
                }
                LoadView service = new LoadView(name,description, price, type, false);
                pickupServiceViews.add(service);
                if (type == 1) {
                    pickupServiceLoadViews.add(service);
                }
                else if (type == 2) {
                    pickupServiceKiloViews.add(service);
                }
                else if (type == 3) {
                    pickupServiceItemViews.add(service);
                }
            }
            showServicesLoads();
            showServicesKilo();
            showServicesItem();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private LocationViewAdapter.CheckChangeListener mCheckListener = new LocationViewAdapter.CheckChangeListener(){
        @Override
        public void checkBoxChanged(int position, boolean b) {
            if(b) {
                for (int i = 0; i < savedLocationViews.size(); i++) {
                    savedLocationViews.get(i).selected = "false";
                    if (i == position) {
                        savedLocationViews.get(i).selected  = "true";
                    }
                }
                locationViewAdapter.notifyDataSetChanged();
            }
        }
    };


}