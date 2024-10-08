package nsoft.laundry.customer.controller.base;


import static android.content.Context.MODE_PRIVATE;
import static nsoft.laundry.customer.common.Global.HOME_GET;
import static nsoft.laundry.customer.common.Global.MY_PROFILE_GET;
import static nsoft.laundry.customer.common.Global.customerId;
import static nsoft.laundry.customer.common.Global.customerUniqueId;
import static nsoft.laundry.customer.common.Global.getServerUrl;
import static nsoft.laundry.customer.common.Global.customerEmail;
import static nsoft.laundry.customer.common.Global.customerFirstName;
import static nsoft.laundry.customer.common.Global.customerLastName;
import static nsoft.laundry.customer.common.Global.customerMobileNumber;
import static nsoft.laundry.customer.common.Global.customerName;
import static nsoft.laundry.customer.common.Global.customerPhotoUrl;
import static nsoft.laundry.customer.common.Global.user_email_key;
import static nsoft.laundry.customer.common.Global.user_first_name_key;
import static nsoft.laundry.customer.common.Global.user_last_name_key;
import static nsoft.laundry.customer.common.Global.user_login_key;
import static nsoft.laundry.customer.common.Global.user_photo_url_key;
import static nsoft.laundry.customer.common.Global.user_sdk_key;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.Global;
import nsoft.laundry.customer.controller.login.LoginActivity;
import nsoft.laundry.customer.controller.menu.ui.home.HomeFragment;
import nsoft.laundry.customer.controller.menu.ui.profile.ProfileFragment;
import nsoft.laundry.customer.utils.dialog.CustomProgress;
import nsoft.laundry.customer.utils.network.HttpCall;
import nsoft.laundry.customer.utils.network.HttpRequest;

public class BaseFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public BaseFragment() {
        // Required empty public constructor
    }

    public static BaseFragment newInstance(String param1, String param2) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText("");

        return textView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void showToast(String content){
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }


    public void showProgressDialog(String message){
        CustomProgress.dismissDialog();
        if(!((Activity) getContext()).isFinishing())
            CustomProgress.show(getContext(), message, false, null, false);
    }

    public void tryLogOut(){
        customerEmail = "";
        customerName = "";
        customerPhotoUrl = "";
        customerFirstName = "";
        customerLastName = "";
        customerMobileNumber = "";
        setInformationToSystem(user_login_key, "");
        setInformationToSystem(user_email_key, "");
        setInformationToSystem(user_photo_url_key, "");
        setInformationToSystem(user_first_name_key, "");
        setInformationToSystem(user_last_name_key, "");
        setInformationToSystem(user_sdk_key, "");
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
    private SharedPreferences pref;
    public void setInformationToSystem(String keyname, String keyinfo){

        pref = getContext().getSharedPreferences("info", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(keyname, keyinfo);
        editor.commit();
    }

    public String getInformationFromSystem(String keyName){
        SharedPreferences shared = getContext().getSharedPreferences("info",MODE_PRIVATE);
        String string_temp = shared.getString(keyName, "");
        return string_temp;
    }

    public void sendRequestToServer(final int sqlNo, String searchKey){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = "\n" + getServerUrl()+ getString(R.string.api_prefix) + getString(R.string.url_request_uuid);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("user_id", customerId);
        paramsPost.put("unique_id", customerUniqueId);
        paramsPost.put("machine_id", Global.MACHINE_ID);
        paramsPost.put("request_by", customerEmail);
        paramsPost.put("sql_no", sqlNo + "");
        paramsPost.put("status_id", Global.DATA_REQUESTED + "");
        paramsPost.put("search_key", searchKey);
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        if (sqlNo == HOME_GET){
                            HomeFragment.getInstance().getRequestStatus(sqlNo, str);
                        }
                        if (sqlNo == MY_PROFILE_GET){
                            ProfileFragment.getInstance().getRequestStatus(sqlNo, str);
                        }
                    }
                    else if (responseCode == Global.RESULT_INCORRECT_UUID){
                        CustomProgress.dismissDialog();
                        tryLogOut();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
                        showToast("getting uuid failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.dismissDialog();
                    showToast("Network error");
                }
            }
        }.execute(httpCallPost);
    }

}
