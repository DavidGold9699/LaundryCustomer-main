package nsoft.laundry.customer.controller.model.shop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.OnMultiClickListener;

public class ShopAdapter extends ArrayAdapter<ShopInfo> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<ShopInfo> _serviceInfoView = null;

    private MyClickListener mListener;
    private CheckChangeListener checkListener;
    public ShopAdapter(@NonNull Context context, int resource, ArrayList<ShopInfo> data, MyClickListener listener, CheckChangeListener checkListener) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._serviceInfoView = data;
        this.mListener = listener;
        this.checkListener = checkListener;
    }

    static class serviceInfoViewHolder
    {
        TextView txtShopName;
        TextView txtDistance;
        TextView txtRiderNumber;
        LinearLayout layMark;
        ImageView imgMark1;
        ImageView imgMark2;
        ImageView imgMark3;
        ImageView imgMark4;
        ImageView imgMark5;
        CheckBox chkSave;

    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        serviceInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new serviceInfoViewHolder();
            holder.txtShopName = row.findViewById(R.id.txt_shop_name);
            holder.txtDistance = row.findViewById(R.id.txt_location);
            holder.txtRiderNumber = row.findViewById(R.id.txt_count_rider);
            holder.layMark = row.findViewById(R.id.lay_mark);

            holder.imgMark1 = row.findViewById(R.id.img_mark1);
            holder.imgMark2 = row.findViewById(R.id.img_mark2);
            holder.imgMark3 = row.findViewById(R.id.img_mark3);
            holder.imgMark4 = row.findViewById(R.id.img_mark4);
            holder.imgMark5 = row.findViewById(R.id.img_mark5);
            holder.chkSave = row.findViewById(R.id.chk_save);
            row.setTag(holder);
        }

        ShopInfo resultItem = _serviceInfoView.get(position);

        holder.txtShopName.setText(resultItem.shopName);
        holder.txtDistance.setText((int)resultItem.distance + "Km");
        holder.txtRiderNumber.setText("(" + resultItem.riderCount + " Rider) ");
        if(Double.isNaN(resultItem.rate)) {
            holder.layMark.setVisibility(View.INVISIBLE);
        }
        else {
            if (resultItem.rate < 1.5) {
                holder.imgMark1.setImageResource(R.drawable.img_star_good);
                holder.imgMark2.setImageResource(R.drawable.img_star_bad);
                holder.imgMark3.setImageResource(R.drawable.img_star_bad);
                holder.imgMark4.setImageResource(R.drawable.img_star_bad);
                holder.imgMark5.setImageResource(R.drawable.img_star_bad);
            }
            else if (resultItem.rate < 2.5) {
                holder.imgMark1.setImageResource(R.drawable.img_star_good);
                holder.imgMark2.setImageResource(R.drawable.img_star_good);
                holder.imgMark3.setImageResource(R.drawable.img_star_bad);
                holder.imgMark4.setImageResource(R.drawable.img_star_bad);
                holder.imgMark5.setImageResource(R.drawable.img_star_bad);
            }
            else if (resultItem.rate < 3.5) {
                holder.imgMark1.setImageResource(R.drawable.img_star_good);
                holder.imgMark2.setImageResource(R.drawable.img_star_good);
                holder.imgMark3.setImageResource(R.drawable.img_star_good);
                holder.imgMark4.setImageResource(R.drawable.img_star_bad);
                holder.imgMark5.setImageResource(R.drawable.img_star_bad);
            }
            else if (resultItem.rate < 4.5) {
                holder.imgMark1.setImageResource(R.drawable.img_star_good);
                holder.imgMark2.setImageResource(R.drawable.img_star_good);
                holder.imgMark3.setImageResource(R.drawable.img_star_good);
                holder.imgMark4.setImageResource(R.drawable.img_star_good);
                holder.imgMark5.setImageResource(R.drawable.img_star_bad);
            }
            else {
                holder.imgMark1.setImageResource(R.drawable.img_star_good);
                holder.imgMark2.setImageResource(R.drawable.img_star_good);
                holder.imgMark3.setImageResource(R.drawable.img_star_good);
                holder.imgMark4.setImageResource(R.drawable.img_star_good);
                holder.imgMark5.setImageResource(R.drawable.img_star_good);
            }
        }
        holder.chkSave.setTag(position);
        holder.chkSave.setChecked(resultItem.checked);
        holder.chkSave.setOnCheckedChangeListener(checkListener);

        return row;
    }

    public static abstract class MyClickListener extends OnMultiClickListener {
        @Override
        public void onMultiClick(View v) {
            myBtnOnClick((Integer) v.getTag(), v);
        }
        public abstract void myBtnOnClick(int position, View v);
    }

    public static abstract class CheckChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            checkBoxChanged((Integer) compoundButton.getTag(), b);
        }
        public abstract void checkBoxChanged(int position, boolean b);
    }
}
