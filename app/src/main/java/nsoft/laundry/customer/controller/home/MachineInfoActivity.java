package nsoft.laundry.customer.controller.home;

import static nsoft.laundry.customer.common.Global.MACHINE_GET_DETAILS;
import static nsoft.laundry.customer.common.Global.MACHINE_GET_SERVICES;
import static nsoft.laundry.customer.common.Global.connectCount;
import static nsoft.laundry.customer.common.Global.connectionTimer;
import static nsoft.laundry.customer.common.Global.getServerUrl;
import static nsoft.laundry.customer.common.Global.washerIssueViews;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;

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
import nsoft.laundry.customer.controller.model.report.IssueView;
import nsoft.laundry.customer.controller.model.service.ServiceAdapter;
import nsoft.laundry.customer.controller.model.service.ServiceView;
import nsoft.laundry.customer.utils.dialog.CustomProgress;
import nsoft.laundry.customer.utils.network.HttpCall;
import nsoft.laundry.customer.utils.network.HttpRequest;

public class MachineInfoActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private LinearLayout laySwitch;
    private LinearLayout layBtnInfo;
    private LinearLayout layBtnServices;
    private LinearLayout layReport;
    private LinearLayout layInfo;
    private LinearLayout layServices;
    private ListView lstService;
    private TextView txtMachineStatus;
    private TextView txtWarning;
    private TextView txtMinLoad;
    private TextView txtMaxLoad;

    private   static ArrayList<RequestInfo> requests = new ArrayList<RequestInfo>();

    private static MachineInfoActivity instance;
    public static MachineInfoActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_info);
        instance = this;

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_machine_info);

        requests = new ArrayList<>();

        sendRequestToServer(MACHINE_GET_DETAILS, "M01");
        sendRequestToServer(MACHINE_GET_SERVICES, "M01");
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
        txtTopTitle.setText("Machine");

        txtMachineStatus = findViewById(R.id.txt_machine_status);
        txtWarning = findViewById(R.id.txt_warning);
        txtMinLoad = findViewById(R.id.txt_min_load);
        txtMaxLoad = findViewById(R.id.txt_max_load);

        laySwitch = findViewById(R.id.lay_switch);
        laySwitch.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (layBtnInfo.getVisibility() == View.VISIBLE) {
                    layBtnInfo.setVisibility(View.GONE);
                    layBtnServices.setVisibility(View.VISIBLE);
                    layInfo.setVisibility(View.GONE);
                    layServices.setVisibility(View.VISIBLE);
                }
                else {
                    layBtnInfo.setVisibility(View.VISIBLE);
                    layBtnServices.setVisibility(View.GONE);
                    layInfo.setVisibility(View.VISIBLE);
                    layServices.setVisibility(View.GONE);
                }
            }
        });

        layBtnInfo = findViewById(R.id.lay_btn_info);
        layBtnInfo.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {

            }
        });
        layBtnServices = findViewById(R.id.lay_btn_services);
        layBtnServices.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {

            }
        });
        layReport = findViewById(R.id.lay_report);
        layReport.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                showReportBox();
            }
        });
        layInfo = findViewById(R.id.lay_info);
        layServices = findViewById(R.id.lay_services);
        lstService = findViewById(R.id.lst_services);

    }


    private ServiceAdapter.MyClickListener mListener = new ServiceAdapter.MyClickListener() {
        @Override
        public void myBtnOnClick(int position, View v) {
            switch (v.getId()){
                case R.id.txt_price:
                    break;
                case R.id.lay_service:
                    break;
            }
        }
    };

    private void showReportBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
        final Boolean[] reportItems = {false, false, false, false, false};

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_report, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);

        LinearLayout laySend = customLayout.findViewById(R.id.lay_submit);
        MultiSelectToggleGroup groupIssues = customLayout.findViewById(R.id.group_issues);
        groupIssues.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {
                Log.e("MachineInfoActivity", "checkedId:" + checkedId);
            }
        });
        laySend.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                dialog.dismiss();
            }
        });
        LinearLayout layCancel = customLayout.findViewById(R.id.lay_hide);
        layCancel.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                dialog.dismiss();
            }
        });

        ArrayList<IssueView> issues = washerIssueViews;
        for (int i = 0; i < issues.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            Button btn = new Button(this);
            btn.setText(issues.get(i).name);
            btn.setBackground(getDrawable(R.drawable.btn_white_border));
            btn.setTextColor(getColor(R.color.white));
            btn.setPadding(20, 0, 20, 0);

            if(issues.get(i).selected) {
                btn.setBackground(getDrawable(R.drawable.btn_red));
            } else {
                btn.setBackground(getDrawable(R.drawable.btn_white_border));
            }
            int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(issues.get(finalI).selected){
                        issues.get(finalI).selected = false;
                        btn.setBackground(getDrawable(R.drawable.btn_white_border));
                    } else {
                        issues.get(finalI).selected = true;
                        btn.setBackground(getDrawable(R.drawable.btn_red));
                    }
                }
            });

            groupIssues.addView(btn, params);
        }
        dialog.show();
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
        if (sqlNo == MACHINE_GET_DETAILS) {
            url += getString(R.string.url_get_machine_detail);
        }
        else if (sqlNo == MACHINE_GET_SERVICES) {
            url += getString(R.string.url_get_machine_services);
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
                        if (sqlNo == MACHINE_GET_DETAILS) {
                            JSONArray responseData =  response.getJSONArray("data");
                            showMachineDetailInformation(responseData);
                        }
                        if (sqlNo == MACHINE_GET_SERVICES) {
                            JSONArray responseData =  response.getJSONArray("data");
                            showMachineServices(responseData);
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

    private void showMachineDetailInformation(JSONArray response) {
        try {
            Log.e("MachineInfoActivity", "showMachineDetailInformation called");
            JSONArray machineArray = response.getJSONArray(0);
            String machineName = machineArray.getString(1);
            String machineStatus = machineArray.getString(2);
            JSONArray infoArray = response.getJSONArray(1);
            String minMaxLoad = infoArray.getString(1);
            String minContent = minMaxLoad.split(",")[0];
            String maxContent = minMaxLoad.split(",")[1];
            String minLoad = minContent.split(":")[1];
            String maxLoad = maxContent.split(":")[1];
            String warningContent = infoArray.getString(2);
            txtTopTitle.setText(machineName);
            txtMachineStatus.setText(machineStatus);
            txtWarning.setText(warningContent);
            txtMinLoad.setText(minLoad);
            txtMaxLoad.setText(maxLoad);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showMachineServices(JSONArray response) {
        try {
            ArrayList<ServiceView> serviceViews = new ArrayList<>();

            for (int i = 0; i < response.length(); i++) {
                JSONArray service = response.getJSONArray(i);
                String serviceCode = service.getString(0);
                String serviceName = service.getString(1);
                String servicePrice ="P " + service.getString(2);
                String serviceDescription = service.getString(3);
                serviceViews.add(new ServiceView(serviceCode, serviceName, serviceDescription, servicePrice));
            }
            ServiceAdapter serviceAdapter = new ServiceAdapter(thisContext,
                    R.layout.item_services, serviceViews, mListener);
            lstService.setAdapter(serviceAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}