package common;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import config.Constants;
import java.text.SimpleDateFormat;

public class GlobalConfigHandler {

    /**
     * Only for Local debugging
     */
    public static void setLocalProps(){
        System.setProperty("env", "prod");
        System.setProperty("device_type", "0");
    }

    /**
     * Get Execution Environment
     * @return
     */
    public static String getEnv() {
        String environment = "";
        String env = System.getProperty("env");
        if (env != null) {
            if (env.equalsIgnoreCase("local")) {
                environment = Constants.STAGE_ENV;
            }else if(env.equalsIgnoreCase("prod")){
                environment = Constants.PROD_ENV;
            }
        } else {
            environment = Constants.STAGE_ENV;
        }
        return environment;
    }

    /**
     * Get Device Type default value android.
     */
    public static String getDeviceType() {
        String type = System.getProperty("device_type");
        if (type != null) {
            int device_type = Integer.parseInt(type.toString().trim());
            return globalDeviceType(device_type);
        }
        return globalDeviceType(0);
    }

    public static String todaysDate() {
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(today);
    }

    public int invocationCounter(int counter, int Max) {
        counter++;
        if (counter == Max) {
            counter = 0;
        }
        return counter;
    }

    public static String globalDeviceType(int val) {
        Map<Integer, String> device_types = new HashMap<>();
        device_types.put(0, "Android");
        device_types.put(1, "IOS");

        for(int device_type : device_types.keySet()) {
            if(device_type == val){
                return device_types.get(val).toString().trim();
            }
        }
        return null;
    }
}
