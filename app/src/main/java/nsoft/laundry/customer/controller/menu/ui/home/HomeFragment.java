package nsoft.laundry.customer.controller.menu.ui.home;

import static nsoft.laundry.customer.common.Global.HOME_GET;
import static nsoft.laundry.customer.common.Global.MACHINE_ID;
import static nsoft.laundry.customer.common.Global.connectCount;
import static nsoft.laundry.customer.common.Global.connectionTimer;
import static nsoft.laundry.customer.common.Global.getServerUrl;
import static nsoft.laundry.customer.common.Global.shop_machine_id_key;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.Global;
import nsoft.laundry.customer.common.OnMultiClickListener;
import nsoft.laundry.customer.controller.base.BaseFragment;
import nsoft.laundry.customer.controller.home.DeliveryActivity;
import nsoft.laundry.customer.controller.home.DoitActivity;
import nsoft.laundry.customer.controller.home.MyServicesActivity;
import nsoft.laundry.customer.controller.model.RequestInfo;
import nsoft.laundry.customer.databinding.FragmentHomeBinding;
import nsoft.laundry.customer.utils.dialog.CustomProgress;
import nsoft.laundry.customer.utils.network.HttpCall;
import nsoft.laundry.customer.utils.network.HttpRequest;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    private LinearLayout layEmptyContainer;
    private ScrollView scrollContainer;
    private LinearLayout layDoit;
    private LinearLayout layDelivery;
    private LinearLayout layMoreServices;

    private   static ArrayList<RequestInfo> requests = new ArrayList<RequestInfo>();


    private static HomeFragment instance;
    public static HomeFragment getInstance() {
        return instance;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        instance = this;

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        scrollContainer = root.findViewById(R.id.scroll_container);
        layEmptyContainer = root.findViewById(R.id.lay_empty_container);

        layDoit = root.findViewById(R.id.lay_do_it);
        layDoit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DoitActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(intent);
            }
        });
        layDelivery = root.findViewById(R.id.lay_delivery);
        layDelivery.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(getContext(), DeliveryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(intent);
            }
        });
        layMoreServices = root.findViewById(R.id.lay_more_services);
        layMoreServices.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(getContext(), MyServicesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(intent);

            }
        });
        String machineId =  getInformationFromSystem(shop_machine_id_key);
        if (machineId.equals("")) {
            scrollContainer.setVisibility(View.GONE);
            layEmptyContainer.setVisibility(View.VISIBLE);
        }
        else {
            scrollContainer.setVisibility(View.VISIBLE);
            layEmptyContainer.setVisibility(View.GONE);
            MACHINE_ID = machineId;

            requests = new ArrayList<>();
            sendRequestToServer(HOME_GET, "");
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void startConnection() {
        showConnectingUI();
        initConnectionTimer();
    }

    private void showConnectingUI(){
        showProgressDialog(getString(R.string.loading_press));
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

    private void showConnectSuccessUI(){
        CustomProgress.dismissDialog();
    }

    private void showConnectFailedUI(){
        CustomProgress.dismissDialog();
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
                if (requests.size() > 0) {
                    for (int i = 0; i < requests.size(); i++) {
                        RequestInfo requestInfo = requests.get(i);
                        if (!requestInfo.success) {
                            getRequestData(requestInfo.uuid, requestInfo.sqlNo);
                        }
                    }
                }
            }
        }, 1000, 1500);
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
        if (sqlNo == HOME_GET) {
            url += getString(R.string.url_get_home_categories);
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
                        if (sqlNo == HOME_GET) {
                            JSONObject responseData =  response.getJSONObject("data");
                            showHomeCategories(responseData);
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

    private void showHomeCategories(JSONObject response) {
        try {
            int doit = response.getInt("DIY");
            int pud = response.getInt("PUD");
            int more = response.getInt("MORESERVICES");
            if (doit == 1) {
                layDoit.setVisibility(View.VISIBLE);
            }
            else {
                layDoit.setVisibility(View.GONE);
            }
            if (pud == 1) {
                layDelivery.setVisibility(View.VISIBLE);
            }
            else {
                layDelivery.setVisibility(View.GONE);
            }
            if (more == 1) {
                layMoreServices.setVisibility(View.VISIBLE);
            }
            else {
                layMoreServices.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}