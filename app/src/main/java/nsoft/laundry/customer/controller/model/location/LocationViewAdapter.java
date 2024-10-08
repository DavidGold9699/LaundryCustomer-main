package nsoft.laundry.customer.controller.model.location;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nsoft.laundry.customer.R;
import nsoft.laundry.customer.controller.model.shop.ShopAdapter;

public class LocationViewAdapter extends RecyclerView.Adapter<LocationViewAdapter.ViewHolder> {

    private List<SavedLocationView> locations;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private LocationViewAdapter.CheckChangeListener mCheckListener;
    public LocationViewAdapter(Context context, List<SavedLocationView> locations, LocationViewAdapter.CheckChangeListener checkListener) {
        this.mInflater = LayoutInflater.from(context);
        this.locations = locations;
        this.mCheckListener = checkListener;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedLocationView savedLocationView = locations.get(position);
        String strTitle = savedLocationView.title;
        String strMachineStatus = savedLocationView.description;
        String strSelected = savedLocationView.selected;
        boolean selected = false;
        if (strSelected.equals("true")) {
            selected = true;
        }
        holder.txtTitle.setText(strTitle);
        holder.chkSelect.setChecked(selected);
        holder.chkSelect.setTag(position);
    }
    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout layTitle;
        TextView txtTitle;
        CheckBox chkSelect;

        ViewHolder(View itemView) {
            super(itemView);
            layTitle = itemView.findViewById(R.id.lay_title);
            txtTitle = itemView.findViewById(R.id.txt_title);
            chkSelect = itemView.findViewById(R.id.chk_select);
            chkSelect.setOnCheckedChangeListener(mCheckListener);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public static abstract class CheckChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            checkBoxChanged((Integer) compoundButton.getTag(), b);
        }
        public abstract void checkBoxChanged(int position, boolean b);
    }
}
