package logic_controller;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.asserts.SoftAssert;
import common.Endpoints;
import test_data.UserTd;
import utils.Helper;

public class UserController {

    Helper helper = new Helper();
    private static Logger log = LoggerFactory.getLogger(UserController.class);

    public static String prepUrl(String baseurl, int pagination) {
        return baseurl + Endpoints.users + pagination;
    }

    public boolean validateUsersObject(int api_call_count, ArrayList<String> urls, JSONArray data) {
        String commonDetails = "For URL : "+urls.get(api_call_count);
        ArrayList<String> avatars = new ArrayList<>();
        SoftAssert softAssert = new SoftAssert();
        Iterator<Object> dataItr = data.iterator();
        while (dataItr.hasNext()) {
            JSONObject user = (JSONObject) dataItr.next();
            if (api_call_count == 0) {
                List<Object> keys = helper.keys(user);
                boolean isKeysValid = validateKey(keys);
                softAssert.assertEquals(isKeysValid, true,"User Object keys validation failed!");
            }

            int id = Integer.parseInt(user.optString(UserTd.Users_Object_Keys[0]).toString().trim());
            softAssert.assertEquals(id > 0, true,  commonDetails+" id must be greater than 0!");

            String email = user.optString(UserTd.Users_Object_Keys[1]).toString().trim();
            softAssert.assertEquals(email.length() > 0, true, commonDetails+" email can't be empty!");

            String first_name = user.optString(UserTd.Users_Object_Keys[2]).toString().trim();
            softAssert.assertEquals(first_name.length() > 0, true, commonDetails+" first_name can't be empty!");

            String last_name = user.optString(UserTd.Users_Object_Keys[3]).toString().trim();
            softAssert.assertEquals(last_name.length() > 0, true, commonDetails+" last_name can't be empty!");

            String avatar = user.optString(UserTd.Users_Object_Keys[4]).toString().trim();
            softAssert.assertEquals(avatar.length() > 0, true, commonDetails+" avatar can't be empty!");
            avatars.add(avatar);

            softAssert.assertAll();
            boolean isAvtarLinkValid = false;
            if(avatars.size() > 0){
                isAvtarLinkValid = helper.validateActiveLinks(avatars);
                softAssert.assertEquals(isAvtarLinkValid, true, commonDetails+" avatar links validation failed!");
            }
            return true;
        }
        return false;
    }

    private boolean validateKey(List<Object> keys) {
        boolean isKeyValid = false;
        if(!keys.isEmpty()){
            for(String key : UserTd.Users_Object_Keys){
                if(keys.contains(key)){
                    isKeyValid = true;
                }else{
                    isKeyValid = false;
                    log.info(key+" key is not valid and not found in response object!");
                    break;
                }
            }
        }
        return isKeyValid;
    }
}