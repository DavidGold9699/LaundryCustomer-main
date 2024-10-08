package nsoft.laundry.customer.controller.menu.ui.receipts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import nsoft.laundry.customer.databinding.FragmentReceiptsBinding;

public class ReceiptsFragment extends Fragment {

    private FragmentReceiptsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReceiptsViewModel receiptsViewModel =
                new ViewModelProvider(this).get(ReceiptsViewModel.class);

        binding = FragmentReceiptsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}