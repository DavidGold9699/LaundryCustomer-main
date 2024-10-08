package nsoft.laundry.customer.controller.model.review;

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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import nsoft.laundry.customer.R;
import nsoft.laundry.customer.common.OnMultiClickListener;

public class ReviewAdapter extends ArrayAdapter<ReviewView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<ReviewView> _reviewInfoView = null;

    private MyClickListener mListener;
    public ReviewAdapter(@NonNull Context context, int resource, ArrayList<ReviewView> data, MyClickListener listener) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._reviewInfoView = data;
        this.mListener = listener;
    }

    static class reviewInfoViewHolder
    {
        CircleImageView imgCustomer;
        TextView txtCustomerName;
        TextView txtTransaction;
        TextView txtContent;
        ImageView imgMark1;
        ImageView imgMark2;
        ImageView imgMark3;
        ImageView imgMark4;
        ImageView imgMark5;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        reviewInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new reviewInfoViewHolder();
            holder.txtCustomerName = row.findViewById(R.id.txt_customer_name);
            holder.imgCustomer = row.findViewById(R.id.img_customer);
            holder.txtTransaction = row.findViewById(R.id.txt_transaction);
            holder.txtContent = row.findViewById(R.id.txt_content);
            holder.imgMark1 = row.findViewById(R.id.img_mark1);
            holder.imgMark2 = row.findViewById(R.id.img_mark2);
            holder.imgMark3 = row.findViewById(R.id.img_mark3);
            holder.imgMark4 = row.findViewById(R.id.img_mark4);
            holder.imgMark5 = row.findViewById(R.id.img_mark5);
            row.setTag(holder);
        }

        ReviewView resultItem = _reviewInfoView.get(position);

        if (!resultItem.customerUrl.equals("")) {
            Picasso.get()
                    .load(resultItem.customerUrl)
                    .placeholder(R.drawable.img_review_profile)
                    .into(holder.imgCustomer);
        }

        holder.txtCustomerName.setText(resultItem.customerName);
        String strTransaction = "";
        if (resultItem.transactionCount > 1) {
            strTransaction = resultItem.transactionCount + " transactions";
        }
        else {
            strTransaction = resultItem.transactionCount + " transaction";
        }
        holder.txtTransaction.setText(strTransaction);
        holder.txtContent.setText(resultItem.content);
        switch (resultItem.rate) {
            case 1:
                holder.imgMark1.setImageResource(R.drawable.img_star_good);
                holder.imgMark2.setImageResource(R.drawable.img_star_bad);
                holder.imgMark3.setImageResource(R.drawable.img_star_bad);
                holder.imgMark4.setImageResource(R.drawable.img_star_bad);
                holder.imgMark5.setImageResource(R.drawable.img_star_bad);
                break;
            case 2:
                holder.imgMark1.setImageResource(R.drawable.img_star_good);
                holder.imgMark2.setImageResource(R.drawable.img_star_good);
                holder.imgMark3.setImageResource(R.drawable.img_star_bad);
                holder.imgMark4.setImageResource(R.drawable.img_star_bad);
                holder.imgMark5.setImageResource(R.drawable.img_star_bad);
                break;
            case 3:
                holder.imgMark1.setImageResource(R.drawable.img_star_good);
                holder.imgMark2.setImageResource(R.drawable.img_star_good);
                holder.imgMark3.setImageResource(R.drawable.img_star_good);
                holder.imgMark4.setImageResource(R.drawable.img_star_bad);
                holder.imgMark5.setImageResource(R.drawable.img_star_bad);
                break;
            case 4:
                holder.imgMark1.setImageResource(R.drawable.img_star_good);
                holder.imgMark2.setImageResource(R.drawable.img_star_good);
                holder.imgMark3.setImageResource(R.drawable.img_star_good);
                holder.imgMark4.setImageResource(R.drawable.img_star_good);
                holder.imgMark5.setImageResource(R.drawable.img_star_bad);
                break;
            case 5:
                holder.imgMark1.setImageResource(R.drawable.img_star_good);
                holder.imgMark2.setImageResource(R.drawable.img_star_good);
                holder.imgMark3.setImageResource(R.drawable.img_star_good);
                holder.imgMark4.setImageResource(R.drawable.img_star_good);
                holder.imgMark5.setImageResource(R.drawable.img_star_good);
                break;
        }

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
