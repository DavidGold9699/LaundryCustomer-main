package nsoft.laundry.customer.controller.base;

import static nsoft.laundry.customer.common.Global.HOME_GET_DOIT_ISSUE;
import static nsoft.laundry.customer.common.Global.HOME_GET_DOIT_WITH_MESSAGE;
import static nsoft.laundry.customer.common.Global.MACHINE_GET_DETAILS;
import static nsoft.laundry.customer.common.Global.MACHINE_GET_SERVICES;
import static nsoft.laundry.customer.common.Global.PICKUP_GET_SERVICES;
import static nsoft.laundry.customer.common.Global.SERVICE_GET_ORDER_INSTRUCTIONS;
import static nsoft.laundry.customer.common.Global.SHOP_GET_INFO;
import static nsoft.laundry.customer.common.Global.customerEmail;
import static nsoft.laundry.customer.common.Global.customerFirstName;
import static nsoft.laundry.customer.common.Global.customerId;
import static nsoft.laundry.customer.common.Global.customerLastName;
import static nsoft.laundry.customer.common.Global.customerMobileNumber;
import static nsoft.laundry.customer.common.Global.customerName;
import static nsoft.laundry.customer.common.Global.customerPhotoUrl;
import static nsoft.laundry.customer.common.Global.customerUniqueId;
import static nsoft.laundry.customer.common.Global.getServerUrl;
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
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.Global;
import nsoft.laundry.customer.controller.home.DeliveryActivity;
import nsoft.laundry.customer.controller.home.DoitActivity;
import nsoft.laundry.customer.controller.home.MachineInfoActivity;
import nsoft.laundry.customer.controller.home.MyServicesActivity;
import nsoft.laundry.customer.controller.home.ShopInfoActivity;
import nsoft.laundry.customer.controller.login.LoginActivity;
import nsoft.laundry.customer.utils.dialog.CustomProgress;
import nsoft.laundry.customer.utils.network.HttpCall;
import nsoft.laundry.customer.utils.network.HttpRequest;


public abstract class BaseActivity extends AppCompatActivity {
    public AppCompatActivity thisActivity;
    public Context thisContext;
    public View thisView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    public void showToast(String content){
        Toast.makeText(thisContext, content, Toast.LENGTH_SHORT).show();
    }


    public String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private SharedPreferences pref;
    public void setInformationToSystem(String keyName, String keyInfo){

        pref = getSharedPreferences("info", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(keyName, keyInfo);
        editor.commit();
    }

    public String getInformationFromSystem(String keyName){
        SharedPreferences shared = getSharedPreferences("info",MODE_PRIVATE);
        String string_temp = shared.getString(keyName, "");
        return string_temp;
    }


    public String standardDecimalFormat(String inputAmount){
        String strAmount = "";
        if (!inputAmount.equals("")){
            DecimalFormat df = new DecimalFormat("#,###.00");
            Double amount = Double.parseDouble(inputAmount);
            strAmount = df.format(amount);
        }
        if (strAmount.equals(".00")){
            strAmount = "0.00";
        }
        return strAmount;
    }

    public String standardDateFormat(String inputDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String strDate = "";
        try {
            Date date = format.parse(inputDate);
            SimpleDateFormat spf= new SimpleDateFormat("MMMM d, yyyy");
            String dateOne = spf.format(date);
            strDate = dateOne;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public String standardDateFormatThreeMonth(String inputDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String strDate = "";
        try {
            Date date = format.parse(inputDate);
            SimpleDateFormat spf= new SimpleDateFormat("MMM d, yyyy");
            String dateOne = spf.format(date);
            strDate = dateOne;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public String standardDateFormatThreeMonthForStaff(String inputDate){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = "";
        try {
            Date date = format.parse(inputDate);
            SimpleDateFormat spf= new SimpleDateFormat("MMM d, yyyy");
            String dateOne = spf.format(date);
            strDate = dateOne;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public String standardTimeFormat(String inputTime){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String strTime = "";
        try {
            Date date = format.parse(inputTime);
            SimpleDateFormat spf= new SimpleDateFormat("hh:mm a");
            String dateOne = spf.format(date);
            strTime = dateOne;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strTime;
    }

    public String standardTimeFormatForStaff(String inputTime){
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        String strTime = "";
        try {
            Date date = format.parse(inputTime);
            SimpleDateFormat spf= new SimpleDateFormat("hh:mm a");
            String dateOne = spf.format(date);
            strTime = dateOne;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strTime;
    }

    public void showProgressDialog(String message){
        CustomProgress.dismissDialog();
        if(!((Activity) thisContext).isFinishing())
            CustomProgress.show(thisContext, message, false, null, false);
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
        Intent intent = new Intent(thisContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);
        thisActivity.finish();
    }
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
                        if (sqlNo == HOME_GET_DOIT_ISSUE || sqlNo == HOME_GET_DOIT_WITH_MESSAGE){
                            DoitActivity.getInstance().getRequestStatus(sqlNo, str);
                        }
                        if (sqlNo == SHOP_GET_INFO){
                            ShopInfoActivity.getInstance().getRequestStatus(sqlNo, str);
                        }
                        if (sqlNo == MACHINE_GET_DETAILS || sqlNo == MACHINE_GET_SERVICES){
                            MachineInfoActivity.getInstance().getRequestStatus(sqlNo, str);
                        }
                        if (sqlNo == PICKUP_GET_SERVICES ){
                            DeliveryActivity.getInstance().getRequestStatus(sqlNo, str);
                        }
                        if (sqlNo == SERVICE_GET_ORDER_INSTRUCTIONS ){
                            MyServicesActivity.getInstance().getRequestStatus(sqlNo, str);
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
