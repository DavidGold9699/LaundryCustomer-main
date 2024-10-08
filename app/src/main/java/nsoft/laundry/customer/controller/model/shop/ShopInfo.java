package nsoft.laundry.customer.controller.model.shop;

public class ShopInfo {
    public String shopId;
    public  String machineId;
    public String shopName;
    public String branch;
    public String amount;
    public int onlineStatus;
    public double latitude;
    public double longitude;
    public int riderCount;
    public double rate;
    public double distance;
    public boolean checked;

    public ShopInfo(String inputShopId, String inputMachineId, String inputShopName, String inputBranch,
                    String inputAmount, int inputOnlineStatus, double inputLatitude, double inputLongitude,
                    int inputRiderCount, double inputRate, double inputDistance, boolean inputChecked){
        this.shopId = inputShopId;
        this.machineId = inputMachineId;
        this.shopName = inputShopName;
        this.branch = inputBranch;
        this.amount = inputAmount;
        this.onlineStatus = inputOnlineStatus;
        this.latitude = inputLatitude;
        this.longitude = inputLongitude;
        this.riderCount = inputRiderCount;
        this.rate = inputRate;
        this.distance = inputDistance;
        this.checked = inputChecked;
    }
}
