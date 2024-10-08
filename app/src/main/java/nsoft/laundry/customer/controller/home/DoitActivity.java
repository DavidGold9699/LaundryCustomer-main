package nsoft.laundry.customer.controller.home;

import static nsoft.laundry.customer.common.Global.HOME_GET_DOIT_ISSUE;
import static nsoft.laundry.customer.common.Global.HOME_GET_DOIT_WITH_MESSAGE;
import static nsoft.laundry.customer.common.Global.connectCount;
import static nsoft.laundry.customer.common.Global.connectionTimer;
import static nsoft.laundry.customer.common.Global.dryerIssueViews;
import static nsoft.laundry.customer.common.Global.getServerUrl;
import static nsoft.laundry.customer.common.Global.issueViews;
import static nsoft.laundry.customer.common.Global.washerIssueViews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.Result;

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
import nsoft.laundry.customer.controller.home.scan.decoding.DoitActivityHandler;
import nsoft.laundry.customer.controller.home.scan.decoding.InactivityTimer;
import nsoft.laundry.customer.controller.home.scan.view.ViewfinderView;
import nsoft.laundry.customer.controller.model.RequestInfo;
import nsoft.laundry.customer.controller.model.report.IssueView;
import nsoft.laundry.customer.controller.model.service.ServiceAdapter;
import nsoft.laundry.customer.controller.model.washer.WasherViewAdapter;
import nsoft.laundry.customer.utils.dialog.CustomProgress;
import nsoft.laundry.customer.utils.network.HttpCall;
import nsoft.laundry.customer.utils.network.HttpRequest;

public class DoitActivity extends BaseActivity {

    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private LinearLayout laySwitch;
    private LinearLayout layBtnWasher;
    private LinearLayout layBtnDryer;
    private LinearLayout laySeeMachine;
    private LinearLayout layDryer;
    private LinearLayout layWasher;
    private RecyclerView rvWasherList;
    private WasherViewAdapter washerViewAdapter;
    private LinearLayout layBtnScan;
    private LinearLayout layBtnServices;
    private LinearLayout layBtnPay;
    private LinearLayout layWasherScan;
    private LinearLayout layWasherServices;
    private LinearLayout layWasherPay;
    private ViewfinderView viewfinderView;
    private DoitActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private static final long VIBRATE_DURATION = 200L;
    private ListView lstWasherServices;
    private LinearLayout layCash;
    private LinearLayout layWait;

    private   static ArrayList<RequestInfo> requests = new ArrayList<RequestInfo>();

    private static DoitActivity instance;
    public static DoitActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doit);
        instance = this;

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_do_it);

        requests = new ArrayList<>();

        sendRequestToServer(HOME_GET_DOIT_WITH_MESSAGE, "WASHER");
        sendRequestToServer(HOME_GET_DOIT_ISSUE, "");

        initUI();
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
        txtTopTitle.setText(getString(R.string.title_do_it));


        laySwitch = findViewById(R.id.lay_switch);
        laySwitch.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                sendRequestToServer(MACHINE_GET, "");
            if (layBtnWasher.getVisibility() == View.VISIBLE) {
                layBtnWasher.setVisibility(View.GONE);
                layBtnDryer.setVisibility(View.VISIBLE);
                layDryer.setVisibility(View.VISIBLE);
                layWasher.setVisibility(View.GONE);
            }
            else {
                layBtnWasher.setVisibility(View.VISIBLE);
                layBtnDryer.setVisibility(View.GONE);
                layDryer.setVisibility(View.GONE);
                layWasher.setVisibility(View.VISIBLE);
            }
            }
        });
        layBtnWasher = findViewById(R.id.lay_btn_washer);
        layBtnWasher.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {

            }
        });
        layBtnDryer = findViewById(R.id.lay_btn_dryer);
        layBtnDryer.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {

            }
        });
        layDryer = findViewById(R.id.lay_dryer);
        layWasher = findViewById(R.id.lay_washer);
        laySeeMachine = findViewById(R.id.lay_see_machine);
        laySeeMachine.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoMachineInfoActivity();
            }
        });
        layBtnScan = findViewById(R.id.lay_btn_scan);
        layBtnServices = findViewById(R.id.lay_btn_services);
        layBtnPay = findViewById(R.id.lay_btn_pay);
        layBtnScan.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layBtnScan.setBackgroundResource(R.drawable.btn_secondary);
                layBtnServices.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnPay.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layWasherScan.setVisibility(View.VISIBLE);
                layWasherServices.setVisibility(View.GONE);
                layWasherPay.setVisibility(View.GONE);
                layWait.setVisibility(View.GONE);
            }
        });
        layBtnServices.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layBtnScan.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnServices.setBackgroundResource(R.drawable.btn_secondary);
                layBtnPay.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layWasherScan.setVisibility(View.GONE);
                layWasherServices.setVisibility(View.VISIBLE);
                layWasherPay.setVisibility(View.GONE);
                layWait.setVisibility(View.GONE);
            }
        });
        layBtnPay.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layBtnScan.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnServices.setBackgroundResource(R.drawable.btn_secondary_disabled);
                layBtnPay.setBackgroundResource(R.drawable.btn_secondary);
                layWasherScan.setVisibility(View.GONE);
                layWasherServices.setVisibility(View.GONE);
                layWasherPay.setVisibility(View.VISIBLE);
                layWait.setVisibility(View.GONE);
            }
        });
        layWasherScan = findViewById(R.id.lay_washer_scan);
        layWasherServices = findViewById(R.id.lay_washer_services);
        layWasherPay = findViewById(R.id.lay_washer_pay);
        lstWasherServices = findViewById(R.id.lst_washer_services);

        rvWasherList = findViewById(R.id.rv_washer_list);
        layCash = findViewById(R.id.lay_cash);
        layCash.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                layWait.setVisibility(View.VISIBLE);
                layWasherScan.setVisibility(View.GONE);
                layWasherServices.setVisibility(View.GONE);
                layWasherPay.setVisibility(View.GONE);
            }
        });
        layWait = findViewById(R.id.lay_wait);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvWasherList.setLayoutManager(horizontalLayoutManager);
        showWasherList();

        viewfinderView = findViewById(R.id.viewfinder_content);
    }


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

    public Handler getHandler() {
        return handler;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }
    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(DoitActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            String[] strSplit = resultString.split(",");
            if (strSplit.length == 3) {
                showToast(getResources().getString(R.string.scan_success));
            }
            else {
                showToast(getResources().getString(R.string.scan_incorrect));
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private void showWasherList(){
        ArrayList<String> washerNumbers = new ArrayList<>();
        ArrayList<String> washerStatus = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            washerNumbers.add(i + "");
            if (i >3) {
                washerStatus.add("AVAILABLE");
            }
            else {
                washerStatus.add("ON-USE");
            }
        }
        washerViewAdapter = new WasherViewAdapter(this,washerNumbers, washerStatus);
        rvWasherList.setAdapter(washerViewAdapter);
    }


    private ServiceAdapter.MyClickListener mListener = new ServiceAdapter.MyClickListener() {
        @Override
        public void myBtnOnClick(int position, View v) {
            showToast("item click");
        }
    };

    private void gotoMachineInfoActivity() {
        Intent intent = new Intent(this, MachineInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
        String url = "\n" + getServerUrl()+ getString(R.string.api_prefix);
        if (sqlNo == HOME_GET_DOIT_WITH_MESSAGE) {
            url += getString(R.string.url_get_doit_machine);
        }
        else if (sqlNo == HOME_GET_DOIT_ISSUE) {
            url += getString(R.string.url_get_doit_issue);
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
                        JSONArray responseData =  response.getJSONArray("data");
                        saveIssues(responseData);
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

    private void saveIssues (JSONArray response) {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONArray issueArray = response.getJSONArray(i);
                String type = issueArray.getString(0);
                String name = issueArray.getString(1);
                boolean selected = false;
                IssueView issue = new IssueView(selected, type, name);
                issueViews.add(issue);
                if (issue.type.equals("WASHER") ) {
                    washerIssueViews.add(issue);
                }
                else {
                    dryerIssueViews.add(issue);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}