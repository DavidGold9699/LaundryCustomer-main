package nsoft.laundry.customer.controller.model;

public class RequestInfo {
    public int sqlNo;
    public String uuid;
    public boolean success;

    public RequestInfo(int inputSqlNo, String inputUuid, boolean inputSuccess){
        this.sqlNo = inputSqlNo;
        this.uuid = inputUuid;
        this.success = inputSuccess;
    }
}
