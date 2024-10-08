package nsoft.laundry.customer.controller.model.report;

public class IssueView {
    public boolean selected;
    public String type;
    public String name;

    public IssueView(boolean inputSelected, String inputType, String inputName){
        this.selected = inputSelected;
        this.type = inputType;
        this.name = inputName;
    }
}
