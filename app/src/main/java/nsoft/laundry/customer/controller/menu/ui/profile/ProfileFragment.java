package nsoft.laundry.customer.controller.menu.ui.profile;

import static nsoft.laundry.customer.common.Global.MY_PROFILE_GET;
import static nsoft.laundry.customer.common.Global.connectCount;
import static nsoft.laundry.customer.common.Global.connectionTimer;
import static nsoft.laundry.customer.common.Global.customerEmail;
import static nsoft.laundry.customer.common.Global.customerFirstName;
import static nsoft.laundry.customer.common.Global.customerLastName;
import static nsoft.laundry.customer.common.Global.customerMobileNumber;
import static nsoft.laundry.customer.common.Global.customerName;
import static nsoft.laundry.customer.common.Global.customerPhotoUrl;
import static nsoft.laundry.customer.common.Global.customerQrImageUrl;
import static nsoft.laundry.customer.common.Global.getServerUrl;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.Global;
import nsoft.laundry.customer.common.OnMultiClickListener;
import nsoft.laundry.customer.controller.base.BaseFragment;
import nsoft.laundry.customer.controller.home.LocationActivity;
import nsoft.laundry.customer.controller.model.RequestInfo;
import nsoft.laundry.customer.databinding.FragmentProfileBinding;
import nsoft.laundry.customer.utils.dialog.CustomProgress;
import nsoft.laundry.customer.utils.network.HttpCall;
import nsoft.laundry.customer.utils.network.HttpRequest;

public class ProfileFragment extends BaseFragment {

    private FragmentProfileBinding binding;
    private ImageView imgQrCode;
    private LinearLayout layLocate;
    private CircleImageView imgAvatar;
    private TextView txtUserName;
    private TextView txtUserFirstName;
    private TextView txtUserLastName;
    private TextView txtUserEmail;
    private TextView txtUserMobile;
    private LinearLayout layReview;
    private TextView txtMark;
    private ImageView imgMark1;
    private ImageView imgMark2;
    private ImageView imgMark3;
    private ImageView imgMark4;
    private ImageView imgMark5;
    private   static ArrayList<RequestInfo> requests = new ArrayList<RequestInfo>();
    private static ProfileFragment instance;
    public static ProfileFragment getInstance() {
        return instance;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        instance = this;

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imgQrCode = root.findViewById(R.id.img_qr);
        if(!customerQrImageUrl.equals("")) {
            Picasso.get()
                    .load(getServerUrl() + '/' + customerQrImageUrl)
                    .placeholder(R.drawable.img_profile)
                    .into(imgQrCode);

        }
        imgQrCode.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                showQrAlert();
            }
        });
        imgAvatar = root.findViewById(R.id.img_avatar);
        if (!customerPhotoUrl.equals("")) {
            Picasso.get()
                    .load(customerPhotoUrl)
                    .placeholder(R.drawable.img_profile)
                    .into(imgAvatar);
        }

        layLocate = root.findViewById(R.id.lay_locate);
        layLocate.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoLocationActivity();
            }
        });
        txtUserName = root.findViewById(R.id.txt_user_name);
        txtUserName.setText(customerName);

        txtUserFirstName = root.findViewById(R.id.txt_first_name);
        txtUserFirstName.setText(customerFirstName);
        txtUserLastName = root.findViewById(R.id.txt_last_name);
        txtUserLastName.setText(customerLastName);
        txtUserEmail = root.findViewById(R.id.txt_email);
        txtUserEmail.setText(customerEmail);
        txtUserMobile = root.findViewById(R.id.txt_mobile);
        txtUserMobile.setText(customerMobileNumber);
        layReview = root.findViewById(R.id.lay_review);
        txtMark = root.findViewById(R.id.txt_mark);
        imgMark1 = root.findViewById(R.id.img_mark1);
        imgMark2 = root.findViewById(R.id.img_mark2);
        imgMark3 = root.findViewById(R.id.img_mark3);
        imgMark4 = root.findViewById(R.id.img_mark4);
        imgMark5 = root.findViewById(R.id.img_mark5);
        requests = new ArrayList<>();

        sendRequestToServer(MY_PROFILE_GET, customerEmail);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void hideReview(){
        layReview.setVisibility(View.GONE);
    }
    private void showReview(int review) {
        layReview.setVisibility(View.VISIBLE);
        txtMark.setText(review + ".0");
        switch (review){
            case 1:
                imgMark1.setImageResource(R.drawable.img_star_good);
                imgMark2.setImageResource(R.drawable.img_star_bad);
                imgMark3.setImageResource(R.drawable.img_star_bad);
                imgMark4.setImageResource(R.drawable.img_star_bad);
                imgMark5.setImageResource(R.drawable.img_star_bad);
                break;
            case 2:
                imgMark1.setImageResource(R.drawable.img_star_good);
                imgMark2.setImageResource(R.drawable.img_star_good);
                imgMark3.setImageResource(R.drawable.img_star_bad);
                imgMark4.setImageResource(R.drawable.img_star_bad);
                imgMark5.setImageResource(R.drawable.img_star_bad);
                break;
            case 3:
                imgMark1.setImageResource(R.drawable.img_star_good);
                imgMark2.setImageResource(R.drawable.img_star_good);
                imgMark3.setImageResource(R.drawable.img_star_good);
                imgMark4.setImageResource(R.drawable.img_star_bad);
                imgMark5.setImageResource(R.drawable.img_star_bad);
                break;
            case 4:
                imgMark1.setImageResource(R.drawable.img_star_good);
                imgMark2.setImageResource(R.drawable.img_star_good);
                imgMark3.setImageResource(R.drawable.img_star_good);
                imgMark4.setImageResource(R.drawable.img_star_good);
                imgMark5.setImageResource(R.drawable.img_star_bad);
                break;
            case 5:
                imgMark1.setImageResource(R.drawable.img_star_good);
                imgMark2.setImageResource(R.drawable.img_star_good);
                imgMark3.setImageResource(R.drawable.img_star_good);
                imgMark4.setImageResource(R.drawable.img_star_good);
                imgMark5.setImageResource(R.drawable.img_star_good);
                break;
        }
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

    private void gotoLocationActivity() {
        Intent intent = new Intent(getContext(), LocationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);

    }

    private void showQrAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_qrcode, null);
        ImageView imgQrCode = customLayout.findViewById(R.id.img_qrcode);

        if(!customerQrImageUrl.equals("")) {
            Picasso.get()
                    .load(getServerUrl() + '/' + customerQrImageUrl)
                    .placeholder(R.drawable.img_profile)
                    .into(imgQrCode);
        }

        builder.setView(customLayout);

        AlertDialog dialog = builder.create();
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
        if (sqlNo == MY_PROFILE_GET) {
            url += getString(R.string.url_get_profile_review);
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
                        if (sqlNo == MY_PROFILE_GET) {
                            JSONArray responseData =  response.getJSONArray("data");
                            int review = responseData.getInt(0);
                            showReview(review);
                        }
                        connectionSuccess();
                    }
                    else if (responseCode == Global.$RESULT_EMPTY_DATA) {
                        for (int i = 0; i < requests.size(); i++) {
                            RequestInfo requestInfo = requests.get(i);
                            if (requestInfo.uuid.equals(uniqueId)  && requestInfo.sqlNo == sqlNo) {
                                requestInfo.success = true;
                                break;
                            }
                        }
                        if (sqlNo == MY_PROFILE_GET) {
                            hideReview();
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