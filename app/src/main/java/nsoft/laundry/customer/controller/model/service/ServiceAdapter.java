package nsoft.laundry.customer.controller.model.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.OnMultiClickListener;

public class ServiceAdapter extends ArrayAdapter<ServiceView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<ServiceView> _serviceInfoView = null;

    private MyClickListener mListener;
    public ServiceAdapter(@NonNull Context context, int resource, ArrayList<ServiceView> data, MyClickListener listener) {
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
        TextView txtPrice;
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
            holder.txtPrice = row.findViewById(R.id.txt_price);
            row.setTag(holder);
        }

        ServiceView resultItem = _serviceInfoView.get(position);

        holder.txtTitle.setText(resultItem.title);
        holder.txtDescription.setText(resultItem.description);
        holder.txtPrice.setText(resultItem.price);

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
