package nsoft.laundry.customer.controller.menu;

import static nsoft.laundry.customer.common.Global.customerName;
import static nsoft.laundry.customer.common.Global.customerPhotoUrl;
import static nsoft.laundry.customer.common.Global.shop_machine_id_key;
import static nsoft.laundry.customer.common.Global.shop_name_key;
import static nsoft.laundry.customer.common.Global.shop_review_key;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.OnMultiClickListener;
import nsoft.laundry.customer.controller.base.BaseActivity;
import nsoft.laundry.customer.controller.home.LocationActivity;
import nsoft.laundry.customer.controller.home.MyServicesActivity;
import nsoft.laundry.customer.controller.home.ShopInfoActivity;
import nsoft.laundry.customer.databinding.ActivityMainBinding;
import nsoft.laundry.customer.utils.database.DataBaseHelper;

import android.util.Log;

public class MainActivity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private LinearLayout layShop;
    private LinearLayout laySearch;
    private LinearLayout layMyService;
    private TextView txtUserName;
    private NavigationView navigationView;
    private ImageView imgAvatar;
    private TextView txtShopName;
    private TextView txtShopRate;
    private LinearLayout layMark;
    private ImageView imgMark1;
    private ImageView imgMark2;
    private ImageView imgMark3;
    private ImageView imgMark4;
    private ImageView imgMark5;

    DataBaseHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("d", "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_logout, R.id.nav_settings, R.id.nav_receipts)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        mydb = new DataBaseHelper(this)

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showShopInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initUI(){
        layShop = findViewById(R.id.layShop);
        layShop.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoShopInfoActivity();
            }
        });
        layMyService = findViewById(R.id.layMyService);
        layMyService.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoMyServiceActivity();
            }
        });
        laySearch = findViewById(R.id.laySearch);
        laySearch.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoLocationActivity();
            }
        });
        txtShopName = findViewById(R.id.txt_shop_name);
        txtShopRate = findViewById(R.id.txt_shop_rate);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        txtUserName = headerView.findViewById(R.id.txt_username);
        txtUserName.setText(customerName);
        imgAvatar = headerView.findViewById(R.id.img_avatar);
        if (!customerPhotoUrl.equals("")) {
            Picasso.get()
                    .load(customerPhotoUrl)
                    .placeholder(R.drawable.img_avatar)
                    .into(imgAvatar);
        }
        layMark = findViewById(R.id.lay_mark);
        imgMark1 = findViewById(R.id.img_mark1);
        imgMark2 = findViewById(R.id.img_mark2);
        imgMark3 = findViewById(R.id.img_mark3);
        imgMark4 = findViewById(R.id.img_mark4);
        imgMark5 = findViewById(R.id.img_mark5);
    }

    private void showShopInformation() {
        String machineId = getInformationFromSystem(shop_machine_id_key);
        if (machineId.equals("")) {
            laySearch.setVisibility(View.VISIBLE);
            layShop.setVisibility(View.GONE);
        }
        else {
            layShop.setVisibility(View.VISIBLE);
            laySearch.setVisibility(View.GONE);
        }
        String shopName = getInformationFromSystem(shop_name_key);
        txtShopName.setText(shopName);
        String shopRate = getInformationFromSystem(shop_review_key);
        Double mark = 0.0;
        if (!shopRate.equals("")) {
            mark =  Double.parseDouble(shopRate);
        }

        if(Double.isNaN(mark) || mark == 0.0) {
            layMark.setVisibility(View.INVISIBLE);
            txtShopRate.setVisibility(View.INVISIBLE);
        }
        else {
            txtShopRate.setText(String.format("%.1f", mark));
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
    }

    private void gotoShopInfoActivity(){
        Intent intent = new Intent(this, ShopInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    private void gotoLocationActivity() {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    private void gotoMyServiceActivity(){
        Intent intent = new Intent(this, MyServicesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }
}