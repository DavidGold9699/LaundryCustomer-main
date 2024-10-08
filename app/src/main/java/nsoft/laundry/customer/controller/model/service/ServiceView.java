package nsoft.laundry.customer.controller.model.service;

public class ServiceView {
    public String code;
    public String title;
    public String description;
    public String price;

    public ServiceView(String inputCode,String inputTitle, String inputDescription, String inputPrice){
        this.code = inputCode;
        this.title = inputTitle;
        this.description = inputDescription;
        this.price = inputPrice;
    }
}
