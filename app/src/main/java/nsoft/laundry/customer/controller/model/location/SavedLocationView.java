package nsoft.laundry.customer.controller.model.location;

public class SavedLocationView {
    public long id;
    public String title;
    public String description;
    public String number;
    public String selected;

    public SavedLocationView(long inputId, String inputTitle, String inputDescription, String inputNumber, String inputSelected){
        this.id = inputId;
        this.title = inputTitle;
        this.description = inputDescription;
        this.number = inputNumber;
        this.selected = inputSelected;
    }
}
