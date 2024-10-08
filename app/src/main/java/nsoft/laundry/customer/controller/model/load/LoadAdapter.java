package nsoft.laundry.customer.controller.model.load;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.OnMultiClickListener;
import nsoft.laundry.customer.controller.model.location.SavedLocationView;

public class LoadAdapter extends ArrayAdapter<LoadView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<LoadView> _serviceInfoView = null;

    private MyClickListener mListener;
    public LoadAdapter(@NonNull Context context, int resource, ArrayList<LoadView> data, MyClickListener listener) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._serviceInfoView = data;
        this.mListener = listener;
    }

    static class serviceInfoViewHolder
    {
        TextView txtTitle;
        TextView txtDescription;
        TextView txtNumber;
        ImageView imgService;
        LinearLayout layBtnPrice;
        ImageView imgPlus;

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
            holder.txtTitle = row.findViewById(R.id.txt_tile);
            holder.txtDescription = row.findViewById(R.id.txt_description);
            holder.txtNumber = row.findViewById(R.id.txt_price);
            holder.layBtnPrice = row.findViewById(R.id.lay_btn_price);
            holder.imgService = row.findViewById(R.id.img_service);
            holder.imgPlus = row.findViewById(R.id.img_plus);
            row.setTag(holder);
        }

        LoadView resultItem = _serviceInfoView.get(position);
        int type = resultItem.serviceType;
        if (type == 1) {
            holder.imgService.setImageResource(R.drawable.img_services_load);
        }
        else if (type == 2) {
            holder.imgService.setImageResource(R.drawable.img_services_kilo);
        }
        else if (type == 3) {
            holder.imgService.setImageResource(R.drawable.img_services_item);
            holder.txtDescription.setVisibility(View.GONE);
        }
        if (resultItem.active) {
            holder.layBtnPrice.setBackgroundResource(R.drawable.btn_green);
            holder.imgPlus.setImageResource(R.drawable.icon_plus_active);
        }

        holder.txtTitle.setText(resultItem.title);
        holder.txtDescription.setText(resultItem.description);
        holder.txtNumber.setText(resultItem.price);

        return row;
    }

    public static abstract class MyClickListener extends OnMultiClickListener {
        @Override
        public void onMultiClick(View v) {
            myBtnOnClick((Integer) v.getTag(), v);
        }
        public abstract void myBtnOnClick(int position, View v);
    }
}
