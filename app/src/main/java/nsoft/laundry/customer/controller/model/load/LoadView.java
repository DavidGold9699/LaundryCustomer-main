package nsoft.laundry.customer.controller.model.load;

public class LoadView {
    public int serviceType; // 1: load, 2: kilo, 3: item
    public String title;
    public String description;
    public String price;
    public boolean active;

    public LoadView(String inputTitle, String inputDescription, String inputPrice, int inputType, boolean inputActive){
        this.title = inputTitle;
        this.description = inputDescription;
        this.price = inputPrice;
        this.serviceType = inputType;
        this.active = inputActive;
    }
}
