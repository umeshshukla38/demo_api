package config;

import java.util.Properties;
import common.FileActions;
import common.GlobalConfigHandler;

public class BaseUrl {

    Properties prop;
    String env = GlobalConfigHandler.getEnv();

    public String baseurl() {
        String baseurl = "";
        if (env.equals(Constants.STAGE_ENV)) {
            prop = FileActions.readProp("local.properties");
        }else if(env.equals(Constants.PROD_ENV)){
            prop = FileActions.readProp("app.properties");
        }

        if(!prop.isEmpty()){
            baseurl = prop.get("baseurl").toString().trim();
            return baseurl;
        }

        return baseurl;
    }
}