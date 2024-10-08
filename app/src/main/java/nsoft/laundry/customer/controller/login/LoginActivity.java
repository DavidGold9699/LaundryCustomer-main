package nsoft.laundry.customer.controller.login;

import static nsoft.laundry.customer.common.Global.SDK_GOOGLE;
import static nsoft.laundry.customer.common.Global._isTest;
import static nsoft.laundry.customer.common.Global.customerId;
import static nsoft.laundry.customer.common.Global.customerUniqueId;
import static nsoft.laundry.customer.common.Global.customerEmail;
import static nsoft.laundry.customer.common.Global.customerFirstName;
import static nsoft.laundry.customer.common.Global.customerLastName;
import static nsoft.laundry.customer.common.Global.customerMobileNumber;
import static nsoft.laundry.customer.common.Global.customerName;
import static nsoft.laundry.customer.common.Global.customerPhotoUrl;
import static nsoft.laundry.customer.common.Global.customerQrImageUrl;
import static nsoft.laundry.customer.common.Global.user_email_key;
import static nsoft.laundry.customer.common.Global.user_first_name_key;
import static nsoft.laundry.customer.common.Global.user_last_name_key;
import static nsoft.laundry.customer.common.Global.user_login_key;
import static nsoft.laundry.customer.common.Global.user_photo_url_key;
import static nsoft.laundry.customer.common.Global.user_sdk_key;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import nsoft.laundry.customer.common.Global;
import nsoft.laundry.customer.common.OnMultiClickListener;
import nsoft.laundry.customer.controller.base.BaseActivity;
import nsoft.laundry.customer.controller.menu.MainActivity;
import nsoft.laundry.customer.R;
import nsoft.laundry.customer.utils.dialog.CustomProgress;
import nsoft.laundry.customer.utils.network.HttpCall;
import nsoft.laundry.customer.utils.network.HttpRequest;

public class LoginActivity extends BaseActivity {

    private LinearLayout layGoogle;
    private LinearLayout layFacebook;
    private LinearLayout layGuest;
    int RG_SIGN_IN = 0;
    SignInButton btnGoogleSignIn;
    GoogleSignInClient mGoogleSignInClient;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_login);

        initUI();

        checkAutoLogin();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initUI(){
        layGoogle = findViewById(R.id.lay_google);
        layGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
        layFacebook = findViewById(R.id.lay_facebook);
        layGuest = findViewById(R.id.lay_guest);
        layGuest.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                setInformationToSystem(user_login_key, "false");
                gotoHomeActivity();
            }
        });
        btnGoogleSignIn = findViewById(R.id.google_sign_in);
        btnGoogleSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnGoogleSignIn.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                googleSignIn();
            }
        });

    }

    private void checkAutoLogin(){
        String strEmail = "";
        String strSdk = "";
        String strFirstName = "";
        String strLastName = "";
        String strPhotoUrl = "";
        strEmail = getInformationFromSystem(user_email_key);
        strFirstName = getInformationFromSystem(user_first_name_key);
        strLastName = getInformationFromSystem(user_last_name_key);
        strSdk = getInformationFromSystem(user_sdk_key);
        strPhotoUrl = getInformationFromSystem(user_photo_url_key);

        if (!strSdk.equals("")){
            if (!strEmail.equals("")){
                int sdkType = Integer.parseInt(strSdk);
                tryCustomerLogin(strEmail, strPhotoUrl, strFirstName, strLastName, sdkType);
            }
        }
        else if (_isTest) {
//            tryCustomerLogin("joseowen1101@gmail.com", strPhotoUrl, strFirstName, strLastName, 1);
            tryCustomerLogin("sonny.eustaquio@gmail.com", "", "Sonny", "Eustaquio", 1);
        }
    }

    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RG_SIGN_IN);
    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    public boolean checkInputValues(String email, String password) {
        boolean isValue = true;
        if(email.equals("")) {
            showToast("Please input Email");
            isValue = false;
        }
        else if(password.equals("")) {
            showToast("Please input Password");
            isValue = false;
        }
        else if (!isEmailValid(email)){
            showToast("Please input correct Email");
            isValue = false;
        }
        return isValue;
    }

    private void getGoogleInformation(GoogleSignInAccount account){
        String email = account.getEmail();
        String name = account.getDisplayName();
        String lastName = account.getFamilyName();
        String firstName = name.replace(lastName, "");
        String id = account.getId();
        String image_url = "";
        if (account.getPhotoUrl() != null){
            image_url = account.getPhotoUrl().toString();
        }
        Toast.makeText(LoginActivity.this, "Google sign in success email: " + email, Toast.LENGTH_LONG).show();
        tryCustomerLogin(email, image_url, firstName, lastName, SDK_GOOGLE);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            getGoogleInformation(account);
        } catch (ApiException e) {
            Log.w("LoginActivity", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this, "Google sign in failed", Toast.LENGTH_LONG).show();
        }
    }

    private void tryCustomerLogin(String email, final String imageUrl, final String firstName, final String lastName, final int sdkType){
        showProgressDialog(getString(R.string.loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + Global.getServerUrl() + getString(R.string.api_prefix) + getString(R.string.url_login);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("email", email);
        paramsPost.put("type", sdkType + "");
        paramsPost.put("photo_url", imageUrl);
        paramsPost.put("first_name", firstName);
        paramsPost.put("last_name", lastName);
        httpCallPost.setParams(paramsPost);

        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        CustomProgress.dismissDialog();
                        JSONObject responseData =  response.getJSONObject("data");
                        String strUserId = responseData.getString("id");
                        String strUserEmail = responseData.getString("email");
                        String strUserFirstName = responseData.getString("first_name");
                        String strUserLastName = responseData.getString("last_name");
                        String strUserPhotoUrl = responseData.getString("photo_url");
                        String strUserMobile = responseData.getString("mobile_number");
                        String strUserQrImageUrl = responseData.getString("qr_img_url");
                        String strUserUniqueId = responseData.getString("unique_id");
                        setInformationToSystem(user_login_key, "true");
                        setInformationToSystem(user_email_key, strUserEmail);
                        setInformationToSystem(user_photo_url_key, strUserPhotoUrl);
                        setInformationToSystem(user_first_name_key, strUserFirstName);
                        setInformationToSystem(user_last_name_key, strUserLastName);
                        setInformationToSystem(user_sdk_key, sdkType + "");
                        customerId = strUserId;
                        customerUniqueId = strUserUniqueId;
                        customerEmail = strUserEmail;
                        customerName = strUserFirstName + strUserLastName;
                        customerPhotoUrl = strUserPhotoUrl;
                        customerFirstName = strUserFirstName;
                        customerLastName = strUserLastName;
                        customerMobileNumber = strUserMobile;
                        customerQrImageUrl = strUserQrImageUrl;
                        gotoHomeActivity();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
                        showToast("Login Failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.dismissDialog();
                    showToast("Network error");
                }

            }
        }.execute(httpCallPost);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RG_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
}
