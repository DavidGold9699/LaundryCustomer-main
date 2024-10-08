package nsoft.laundry.customer.controller.home;

import static nsoft.laundry.customer.common.Global.SERVICE_GET_ORDER_INSTRUCTIONS;
import static nsoft.laundry.customer.common.Global.connectCount;
import static nsoft.laundry.customer.common.Global.connectionTimer;
import static nsoft.laundry.customer.common.Global.getServerUrl;
import static nsoft.laundry.customer.common.Global.instructionViews;
import static nsoft.laundry.customer.common.Global.washerIssueViews;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import nsoft.laundry.customer.controller.model.instruction.InstructionView;
import nsoft.laundry.customer.controller.model.report.IssueView;
import nsoft.laundry.customer.utils.dialog.CustomProgress;
import nsoft.laundry.customer.utils.network.HttpCall;
import nsoft.laundry.customer.utils.network.HttpRequest;

public class MyServicesActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private LinearLayout layInstructions;
    private   static ArrayList<RequestInfo> requests = new ArrayList<RequestInfo>();
    private static MyServicesActivity instance;
    public static MyServicesActivity getInstance() {
        return instance;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);
        instance = this;

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_my_services);


        requests = new ArrayList<>();

        sendRequestToServer(SERVICE_GET_ORDER_INSTRUCTIONS, "");

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
        txtTopTitle.setText(getString(R.string.title_more_services));
        layInstructions = findViewById(R.id.lay_instructions);
        layInstructions.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                showInstructionBox();
            }
        });

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

    private void startConnection() {
        showConnectingUI();
        initConnectionTimer();
    }
    public void stopConnectionTimer(){
        if(connectionTimer != null){
            connectionTimer.cancel();
            connectionTimer = null;
        }
    }

    private void stopConnection(){
        stopConnectionTimer();
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

    private void showInstructionBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
        final Boolean[] reportItems = {false, false, false, false, false};

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_introduction, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);

        LinearLayout layAdd = customLayout.findViewById(R.id.lay_add);
        MultiSelectToggleGroup groupInstructions = customLayout.findViewById(R.id.group_instructions);
        groupInstructions.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {
                Log.e("MachineInfoActivity", "checkedId:" + checkedId);
            }
        });
        layAdd.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                dialog.dismiss();
            }
        });
        LinearLayout layCancel = customLayout.findViewById(R.id.lay_cancel);
        layCancel.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                dialog.dismiss();
            }
        });

        ArrayList<InstructionView> issues = instructionViews;
        for (int i = 0; i < issues.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            Button btn = new Button(this);
            btn.setText(issues.get(i).title);
            btn.setBackground(getDrawable(R.drawable.btn_white_border));
            btn.setTextColor(getColor(R.color.white));
            btn.setPadding(20, 0, 20, 0);

            if(issues.get(i).active) {
                btn.setBackground(getDrawable(R.drawable.btn_red));
            } else {
                btn.setBackground(getDrawable(R.drawable.btn_white_border));
            }
            int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(issues.get(finalI).active){
                        issues.get(finalI).active = false;
                        btn.setBackground(getDrawable(R.drawable.btn_white_border));
                    } else {
                        issues.get(finalI).active = true;
                        btn.setBackground(getDrawable(R.drawable.btn_red));
                    }
                }
            });

            groupInstructions.addView(btn, params);
        }
        dialog.show();
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
        if (sqlNo == SERVICE_GET_ORDER_INSTRUCTIONS) {
            url += getString(R.string.url_get_service_order_instructions);
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
                        if (sqlNo == SERVICE_GET_ORDER_INSTRUCTIONS) {
                            JSONArray responseData =  response.getJSONArray("data");
                            instructionViews = new ArrayList<>();
                            for (int i = 0; i<responseData.length(); i++) {
                                JSONArray arrIntroduction = responseData.getJSONArray(i);
                                String introduction = arrIntroduction.getString(0);
                                InstructionView instructionView = new InstructionView(introduction, false);
                                instructionViews.add(instructionView);
                            }
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
}