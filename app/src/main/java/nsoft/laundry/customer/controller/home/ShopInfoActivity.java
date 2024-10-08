package nsoft.laundry.customer.controller.home;

import static nsoft.laundry.customer.common.Global.SHOP_GET_INFO;
import static nsoft.laundry.customer.common.Global._isTest;
import static nsoft.laundry.customer.common.Global.connectCount;
import static nsoft.laundry.customer.common.Global.connectionTimer;
import static nsoft.laundry.customer.common.Global.currentOrderId;
import static nsoft.laundry.customer.common.Global.currentShopId;
import static nsoft.laundry.customer.common.Global.customerEmail;
import static nsoft.laundry.customer.common.Global.customerId;
import static nsoft.laundry.customer.common.Global.customerName;
import static nsoft.laundry.customer.common.Global.getServerUrl;
import static nsoft.laundry.customer.common.Global.currentShopMachineId;
import static nsoft.laundry.customer.common.Global.shop_id_key;
import static nsoft.laundry.customer.common.Global.shop_machine_id_key;
import static nsoft.laundry.customer.common.Global.shop_review_key;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import nsoft.laundry.customer.controller.model.review.ReviewAdapter;
import nsoft.laundry.customer.controller.model.review.ReviewView;
import nsoft.laundry.customer.utils.dialog.CustomProgress;
import nsoft.laundry.customer.utils.network.HttpCall;
import nsoft.laundry.customer.utils.network.HttpRequest;

public class ShopInfoActivity extends BaseActivity {

    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private LinearLayout layBtnInfo;
    private LinearLayout layBtnReview;
    private LinearLayout layInfo;
    private LinearLayout layReview;
    private LinearLayout laySwitch;
    private LinearLayout laySendMessage;
    private LinearLayout layAddReview;
    private TextView txtShopName;
    private TextView txtShopBranch;
    private TextView txtShopAddress;
    private TextView txtInfoAddress;
    private TextView txtMachineCount;
    private TextView txtWorkingHour;
    private ListView lstReviews;
    private LinearLayout layLookShop;
    private TextView txtMark;
    private ImageView imgMark1;
    private ImageView imgMark2;
    private ImageView imgMark3;
    private ImageView imgMark4;
    private ImageView imgMark5;

    private   static ArrayList<RequestInfo> requests = new ArrayList<RequestInfo>();

    private static ShopInfoActivity instance;
    public static ShopInfoActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        instance = this;

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_shop_info);

        requests = new ArrayList<>();

        sendRequestToServer(SHOP_GET_INFO, "");
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String shopId = getInformationFromSystem(shop_id_key);
        String shopMachineId = getInformationFromSystem(shop_machine_id_key);
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
        txtTopTitle.setText(getString(R.string.title_shop_information));

        txtShopName = findViewById(R.id.txt_shop_name);
        txtShopBranch = findViewById(R.id.txt_shop_branch);
        txtShopAddress = findViewById(R.id.txt_shop_address);
        txtInfoAddress = findViewById(R.id.txt_info_address);
        txtMachineCount = findViewById(R.id.txt_machine_count);
        txtWorkingHour = findViewById(R.id.txt_working_hour);
        layBtnInfo = findViewById(R.id.lay_btn_info);
        layBtnInfo.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
            }
        });
        layBtnReview= findViewById(R.id.lay_btn_review);
        layBtnReview.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {

            }
        });
        layInfo = findViewById(R.id.lay_info);
        layReview = findViewById(R.id.lay_review);
        laySwitch = findViewById(R.id.lay_switch);
        laySwitch.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
            if (layBtnInfo.getVisibility() == View.VISIBLE) {
                layBtnInfo.setVisibility(View.GONE);
                layBtnReview.setVisibility(View.VISIBLE);
                layInfo.setVisibility(View.GONE);
                layReview.setVisibility(View.VISIBLE);
                laySendMessage.setVisibility(View.GONE);
                layAddReview.setVisibility(View.VISIBLE);
            }
            else {
                layBtnInfo.setVisibility(View.VISIBLE);
                layBtnReview.setVisibility(View.GONE);
                layInfo.setVisibility(View.VISIBLE);
                layReview.setVisibility(View.GONE);
                laySendMessage.setVisibility(View.VISIBLE);
                layAddReview.setVisibility(View.GONE);
            }
            }
        });
        laySendMessage = findViewById(R.id.lay_send_message);
        laySendMessage.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
            showMessageBox();
            }
        });
        layAddReview = findViewById(R.id.lay_add_review);
        layAddReview.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
            showRateBox();
            }
        });
        lstReviews = findViewById(R.id.lst_reviews);
        layLookShop = findViewById(R.id.lay_look_shop);
        layLookShop.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
            gotoLocationActivity();
            }
        });
        txtMark = findViewById(R.id.txt_mark);
        imgMark1 = findViewById(R.id.img_mark1);
        imgMark2 = findViewById(R.id.img_mark2);
        imgMark3 = findViewById(R.id.img_mark3);
        imgMark4 = findViewById(R.id.img_mark4);
        imgMark5 = findViewById(R.id.img_mark5);
    }

    private void gotoLocationActivity() {
        Intent intent = new Intent(thisContext, LocationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showMessageBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_send_message, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();
        dialog.show();
        LinearLayout laySend = customLayout.findViewById(R.id.lay_send);
        EditText edtMessage = customLayout.findViewById(R.id.edt_message);
        laySend.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                String message = edtMessage.getText().toString();
                if(message.equals("")) {
                    showToast("Please input message");
                }
                else {
                    sendShopMessage(message);
                    dialog.dismiss();
                }
            }
        });
        LinearLayout layCancel = customLayout.findViewById(R.id.lay_cancel);
        layCancel.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void showRateBox() {
        final int[] marks = {5};
        AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_review, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();
        EditText edtReviewContent = customLayout.findViewById(R.id.edt_review_content);
        LinearLayout laySend = customLayout.findViewById(R.id.lay_send);
        laySend.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                showToast("content: " + edtReviewContent.getText().toString());
                submitReview(marks[0], edtReviewContent.getText().toString());
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
        ImageView imgMark1 = customLayout.findViewById(R.id.img_mark1);
        ImageView imgMark2 = customLayout.findViewById(R.id.img_mark2);
        ImageView imgMark3 = customLayout.findViewById(R.id.img_mark3);
        ImageView imgMark4 = customLayout.findViewById(R.id.img_mark4);
        ImageView imgMark5 = customLayout.findViewById(R.id.img_mark5);

        imgMark1.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                marks[0] = 1;
                imgMark1.setImageResource(R.drawable.img_star_good);
                imgMark2.setImageResource(R.drawable.img_star_bad);
                imgMark3.setImageResource(R.drawable.img_star_bad);
                imgMark4.setImageResource(R.drawable.img_star_bad);
                imgMark5.setImageResource(R.drawable.img_star_bad);
            }
        });
        imgMark2.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                marks[0] = 2;
                imgMark1.setImageResource(R.drawable.img_star_good);
                imgMark2.setImageResource(R.drawable.img_star_good);
                imgMark3.setImageResource(R.drawable.img_star_bad);
                imgMark4.setImageResource(R.drawable.img_star_bad);
                imgMark5.setImageResource(R.drawable.img_star_bad);
            }
        });
        imgMark3.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                marks[0] = 3;
                imgMark1.setImageResource(R.drawable.img_star_good);
                imgMark2.setImageResource(R.drawable.img_star_good);
                imgMark3.setImageResource(R.drawable.img_star_good);
                imgMark4.setImageResource(R.drawable.img_star_bad);
                imgMark5.setImageResource(R.drawable.img_star_bad);
            }
        });
        imgMark4.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                marks[0] = 4;
                imgMark1.setImageResource(R.drawable.img_star_good);
                imgMark2.setImageResource(R.drawable.img_star_good);
                imgMark3.setImageResource(R.drawable.img_star_good);
                imgMark4.setImageResource(R.drawable.img_star_good);
                imgMark5.setImageResource(R.drawable.img_star_bad);

            }
        });
        imgMark5.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                marks[0] = 5;
                imgMark1.setImageResource(R.drawable.img_star_good);
                imgMark2.setImageResource(R.drawable.img_star_good);
                imgMark3.setImageResource(R.drawable.img_star_good);
                imgMark4.setImageResource(R.drawable.img_star_good);
                imgMark5.setImageResource(R.drawable.img_star_good);
            }
        });
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

    private void showShopDetailInformation(JSONObject response){
        try {
            String shopName = response.getString("ONLINESHOPNAME");
            String shopBranch = response.getString("ONLINESHOPBRANCH");
            String shopAddress = response.getString("ONLINEADDRESS");
            String shopWorkingHour = response.getString("OPERATIONHOURS");
            String shopMachineCount = response.getString("NOOFMACHIES");
            String shopId = response.getString("ONLINESHOPID");
            currentShopMachineId = shopId;
            txtShopName.setText(shopName);
            txtShopBranch.setText(shopBranch);
            txtShopAddress.setText(shopAddress);
            txtInfoAddress.setText(shopAddress);
            txtMachineCount.setText(shopMachineCount);
            txtWorkingHour.setText(shopWorkingHour);

            String shopRate = getInformationFromSystem(shop_review_key);
            Double mark =  Double.parseDouble(shopRate);

            if(Double.isNaN(mark)) {
                txtMark.setVisibility(View.INVISIBLE);
                imgMark1.setVisibility(View.INVISIBLE);
                imgMark2.setVisibility(View.INVISIBLE);
                imgMark3.setVisibility(View.INVISIBLE);
                imgMark4.setVisibility(View.INVISIBLE);
                imgMark5.setVisibility(View.INVISIBLE);
            }
            else {
                txtMark.setText(String.format("%.1f", mark));
                if (mark < 1.5) {
                    imgMark1.setImageResource(R.drawable.img_star_good);
                    imgMark2.setImageResource(R.drawable.img_star_bad);
                    imgMark3.setImageResource(R.drawable.img_star_bad);
                    imgMark4.setImageResource(R.drawable.img_star_bad);
                    imgMark5.setImageResource(R.drawable.img_star_bad);
                }
                else if (mark < 2.5) {
                    imgMark1.setImageResource(R.drawable.img_star_good);
                    imgMark2.setImageResource(R.drawable.img_star_good);
                    imgMark3.setImageResource(R.drawable.img_star_bad);
                    imgMark4.setImageResource(R.drawable.img_star_bad);
                    imgMark5.setImageResource(R.drawable.img_star_bad);
                }
                else if (mark < 3.5) {
                    imgMark1.setImageResource(R.drawable.img_star_good);
                    imgMark2.setImageResource(R.drawable.img_star_good);
                    imgMark3.setImageResource(R.drawable.img_star_good);
                    imgMark4.setImageResource(R.drawable.img_star_bad);
                    imgMark5.setImageResource(R.drawable.img_star_bad);
                }
                else if (mark < 4.5) {
                    imgMark1.setImageResource(R.drawable.img_star_good);
                    imgMark2.setImageResource(R.drawable.img_star_good);
                    imgMark3.setImageResource(R.drawable.img_star_good);
                    imgMark4.setImageResource(R.drawable.img_star_good);
                    imgMark5.setImageResource(R.drawable.img_star_bad);
                }
                else {
                    imgMark1.setImageResource(R.drawable.img_star_good);
                    imgMark2.setImageResource(R.drawable.img_star_good);
                    imgMark3.setImageResource(R.drawable.img_star_good);
                    imgMark4.setImageResource(R.drawable.img_star_good);
                    imgMark5.setImageResource(R.drawable.img_star_good);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showReviews(JSONArray reviewData) throws JSONException {
        ArrayList<ReviewView> reviewViews = new ArrayList<>();
        for (int i = 0; i < reviewData.length(); i++) {
            JSONObject review = reviewData.getJSONObject(i);
            String firstName = review.getString("first_name");
            String lastName = review.getString("last_name");
            String photoUrl = review.getString("photo_url");
            String content = review.getString("content");
            int rate = review.getInt("rate");
            ArrayList<String> images = new ArrayList<>();
            ReviewView reviewView = new ReviewView(firstName+ lastName, photoUrl, rate, 3, content, images);
            reviewViews.add(reviewView);
        }
        ReviewAdapter reviewAdapter = new ReviewAdapter(thisContext,
                R.layout.item_review, reviewViews, mListener);
        lstReviews.setAdapter(reviewAdapter);
        if (reviewAdapter != null) {
            int totalHeight = 0;
            for (int i = 0; i < reviewAdapter.getCount(); i++) {
                View listItem = reviewAdapter.getView(i, null, lstReviews);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
                ViewGroup.LayoutParams params = lstReviews.getLayoutParams();
                params.height = totalHeight + (lstReviews.getDividerHeight() * (reviewAdapter.getCount() - 1));
                lstReviews.setLayoutParams(params);
            }
        }
    }


    private ReviewAdapter.MyClickListener mListener = new ReviewAdapter.MyClickListener() {
        @Override
        public void myBtnOnClick(int position, View v) {
        switch (v.getId()){
            case R.id.txt_customer_name:
                break;
            case R.id.lay_service:
                break;
        }
        }
    };


    private void getRequestData(final String uniqueId, final int sqlNo){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.api_prefix) + getString(R.string.url_get_shop_detail);
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
                        JSONObject responseData =  response.getJSONObject("data");
                        JSONObject shopMainData = responseData.getJSONObject("main");
                        JSONObject shopCloudData = responseData.getJSONObject("cloud");
                        JSONArray reviewData = responseData.getJSONArray("review");
                        currentShopId = shopCloudData.getInt("id");
                        connectionSuccess();
                        showShopDetailInformation(shopMainData);
                        showReviews(reviewData);
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


    private void submitReview(int rate, String content){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = "\n" + getServerUrl() + getString(R.string.api_prefix) + getString(R.string.url_submit_shop_review);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        if (_isTest)
            paramsPost.put("order_id", 1 + "");
        else
            paramsPost.put("order_id", currentOrderId + "");
        paramsPost.put("shop_id", currentShopId + "");
        paramsPost.put("machine_id", currentShopMachineId);
        paramsPost.put("customer_id", customerId);
        paramsPost.put("rate", rate+"");
        paramsPost.put("content", content);
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        JSONArray responseData =  response.getJSONArray("data");
                        showReviews(responseData);
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        showToast("submit failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("network exception");
                }

            }
        }.execute(httpCallPost);
    }


    private void sendShopMessage(String message){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = "\n" + getServerUrl() + getString(R.string.api_prefix) + getString(R.string.url_send_shop_message);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("user_id", customerId);
        paramsPost.put("unique_id", "111");
        paramsPost.put("machine_id", Global.MACHINE_ID);
        paramsPost.put("request_by", customerEmail);
        paramsPost.put("sql_no", Global.SHOP_POST_MESSAGE + "");
        paramsPost.put("status_id", Global.DATA_REQUESTED + "");

        JSONObject messageInfo = new JSONObject();
        try {
            messageInfo.put("CloudID", currentShopId);
            messageInfo.put("SenderName", customerName);
            messageInfo.put("SenderEmail", customerEmail);
            messageInfo.put("SenderNumber", customerId);
            messageInfo.put("MessageType", "ShopMessage");
            messageInfo.put("Message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        paramsPost.put("search_key", messageInfo.toString());
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        showToast("Message sent to server!");
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        showToast("Send message failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("network exception");
                }

            }
        }.execute(httpCallPost);
    }

}