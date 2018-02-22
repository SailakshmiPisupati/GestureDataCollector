package touchdatacollector.mobile.com.touchdatacollector;

/**
 * Created by saila on 12/5/17.
 */

public class Utils {
    public static final String IPADDRESS = "10.0.0.224";
//    public static final String IPADDRESS ="130.85.250.244";
    public static final String PORT_NUMBER = "3000";
    public String USER_ID;

    public static String deviceID;

    public static String getDeviceID() {
        return deviceID;
    }

    public static void setDeviceID(String deviceID) {
        Utils.deviceID = deviceID;
    }

    public static String getIPADDRESS() {
        return IPADDRESS;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public static String getPortNumber() {
        return PORT_NUMBER;
    }
}
