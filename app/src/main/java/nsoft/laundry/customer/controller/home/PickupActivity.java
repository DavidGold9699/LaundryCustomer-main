package nsoft.laundry.customer.controller.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.controller.base.BaseActivity;

public class PickupActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);
    }
}