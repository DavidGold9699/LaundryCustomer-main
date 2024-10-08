package nsoft.laundry.customer.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.controller.base.BaseActivity;
import nsoft.laundry.customer.controller.login.LoginActivity;

public class SplashActivity extends BaseActivity {
    private LinearLayout laySkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initUI();
    }

    private void initUI() {
        laySkip = findViewById(R.id.lay_skip);
        laySkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLoginActivity();
            }
        });
    }

    private void gotoLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }
}