package nsoft.laundry.customer.controller.model.review;

import java.util.ArrayList;

public class ReviewView {
    public String customerName;
    public String customerUrl;
    public int rate;
    public String content;
    public int transactionCount;
    public ArrayList<String> photoUrls;

    public ReviewView(String inputCustomerName, String inputCustomerUrl, int inputRate, int inputTransactionCount, String inputContent, ArrayList<String> inputPhotoUrls){
        this.customerName = inputCustomerName;
        this.customerUrl = inputCustomerUrl;
        this.rate = inputRate;
        this.transactionCount = inputTransactionCount;
        this.content = inputContent;
        this.photoUrls = inputPhotoUrls;
    }
}
