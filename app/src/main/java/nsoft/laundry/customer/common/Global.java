package nsoft.laundry.customer.common;

import java.util.ArrayList;
import java.util.Timer;

import nsoft.laundry.customer.controller.model.instruction.InstructionView;
import nsoft.laundry.customer.controller.model.load.LoadView;
import nsoft.laundry.customer.controller.model.report.IssueView;
import nsoft.laundry.customer.controller.model.shop.ShopInfo;

public class Global {


    public static int SDK_FACEBOOK = 1;
    public static int SDK_GOOGLE = 2;


    // region -- valuer in DB --
    public static int ACTIVATED = 1;
    public static int DEACTIVATED = 2;
    public static int EXPIRED = 3;
    public static int DATA_REQUESTED = 4;
    public static int DATA_RESPONSED = 5;
    // endregion

    public static String MACHINE_ID = "";
    public static int RESULT_INCORRECT_UUID = 211;


    // region -- query --
    public static int SHOP_GET = 501;
    public static int SHOP_GET_INFO = 502;
    public static int SHOP_GET_REVIEWS = 503;
    public static int SHOP_POST_MESSAGE = 504;
    public static int SHOP_POST_REVIEW = 505;
    public static int SHOP_GET_RIDER = 506;
    public static int MY_PROFILE_GET = 511;
    public static int SETTINGS_GET = 521;
    public static int E_RECEIPTS_GET = 531;
    public static int HOME_GET = 541;

    public static int HOME_GET_DOIT = 5411;
    public static int HOME_GET_DOIT_WITH_MESSAGE = 5412;
    public static int HOME_GET_DOIT_ISSUE = 5413;
    public static int PICKUP_GET = 542;
    public static int PICKUP_GET_SAVED_LOCATION = 5421;
    public static int PICKUP_GET_SERVICES = 543;
    public static int PICKUP_GET_SERVICES_LOAD = 5431;
    public static int PICKUP_GET_SERVICES_KILO = 5432;
    public static int PICKUP_GET_SERVICES_ITEM = 5433;
    public static int PICKUP_GET_RIDER = 544;
    public static int PICKUP_GET_PAYMENT = 545;
    public static int PICKUP_GET_MY_SERVICES = 546;
    public static int SERVICE_GET_ORDER_INSTRUCTIONS = 547;
    public static int MACHINE_GET = 551;
    public static int MACHINE_GET_INFO = 552;
    public static int MACHINE_GET_SERVICES = 553;
    public static int MACHINE_GET_DETAILS = 554;
    public static int MACHINE_GET_ISSUES = 5541;
    public static int MACHINE_POST_REPORT = 555;
    public static int PAYMENT_METHOD_GET = 561;
    public static int LOCATE_SHOP_GET = 571;
    public static int LOCATE_SHOP_COUNT_RIDER = 5711;

    // endregion


    // region -- test --
    public static boolean _isTest = true;
    public static boolean _isCloud = false;
    // endregion

    // region -- http communication [201, 300] --
    public static int RESULT_SUCCESS = 201;
    public static int RESULT_FAILED = 202;
    public static int RESULT_OVER_EXPIRED = 208;
    public static int $RESULT_EMPTY_DATA = 212;
    public static int RESULT_EMAIL_PASSWORD_INCORRECT = 203;
    // endregion

    public  static String url_cloud_server = "http://portal.laundryarea.com/";
    public  static String url_local_server = "http://192.168.10.100:10000";

    public static String getServerUrl() {
        String httpUrl;
        if (_isCloud)
            httpUrl = url_cloud_server;
        else
            httpUrl = url_local_server;
        return httpUrl;
    }

    // region -- system reference keys --
    public static String user_login_key = "user_login";
    public static String user_email_key = "user_email";
    public static String user_photo_url_key = "user_photo_url";
    public static String user_first_name_key = "user_first_name";
    public static String user_last_name_key = "user_last_name";
    public static String user_sdk_key = "user_sdk";
    public static String shop_machine_id_key = "shop_machine_id";
    public static String shop_id_key = "shop_id";
    public static String shop_name_key = "shop_name";
    public static String shop_review_key = "shop_review";
    // endregion

    public static Timer connectionTimer;
    public static int connectCount = 0;


    // region -- user information --
    public static String customerId = "";
    public static String customerUniqueId = "";
    public static String customerEmail = "";
    public static String customerName = "";
    public static String customerFirstName = "";
    public static String customerLastName = "";
    public static String customerPhotoUrl = "";
    public static String customerMobileNumber = "";
    public static String customerQrImageUrl = "";
    // endregion

    // region -- shop information --
    public static String currentShopMachineId = "";
    public static int currentShopId = -1;
    // endregion

    // region -- order information --
    public static int currentOrderId = -1;
    // endregion

    public static int SERVER_CONNECTION_COUNT = 3;
    public  static ArrayList<IssueView> issueViews = new ArrayList<IssueView>();
    public  static ArrayList<IssueView> washerIssueViews = new ArrayList<IssueView>();
    public  static ArrayList<IssueView> dryerIssueViews = new ArrayList<IssueView>();
    public  static ArrayList<LoadView> pickupServiceViews = new ArrayList<LoadView>();
    public  static ArrayList<LoadView> pickupServiceLoadViews = new ArrayList<LoadView>();
    public  static ArrayList<LoadView> pickupServiceKiloViews = new ArrayList<LoadView>();
    public  static ArrayList<LoadView> pickupServiceItemViews = new ArrayList<LoadView>();
    public static ArrayList<InstructionView> instructionViews = new ArrayList<>();
    public static ArrayList<ShopInfo> shopInfos = new ArrayList<>();


}