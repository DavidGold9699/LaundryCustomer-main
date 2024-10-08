package nsoft.laundry.customer.controller.model.instruction;

public class InstructionView {
    public String title;
    public boolean active;

    public InstructionView(String inputTitle, boolean inputActive){
        this.title = inputTitle;
        this.active = inputActive;
    }
}
