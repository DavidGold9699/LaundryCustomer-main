package nsoft.laundry.customer.controller.menu.ui.receipts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReceiptsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReceiptsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is receipts fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}