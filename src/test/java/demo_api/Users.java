package demo_api;
import config.BaseUrl;
import io.restassured.response.Response;
import logic_controller.UserController;
import test_data.UserTd;
import utils.Helper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.RequestHandler;

public class Users extends BaseUrl {

    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    ArrayList<String> Urls = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    UserController controller = new UserController();
    RequestHandler reqHandler = new RequestHandler();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger log = LoggerFactory.getLogger(Users.class);

    @BeforeTest
    public void prepEnv(){
        BASEURL = baseurl();
        MAX_CALL = UserTd.pagination.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = UserTd.INVOCATION_COUNT)
    public void createGetUsersRequest(int pagination){
        String url = UserController.prepUrl(BASEURL, pagination);
        Urls.add(url);
        Response response = reqHandler.createGetRequest(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == MAX_CALL){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Expected number of api calls not completed, manual check required!");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = UserTd.INVOCATION_COUNT)
    public void validateUsersDataArray(int pagination){
        Response response = RESPONSES.get(API_CALL);
        JSONObject responseObject = new JSONObject(response.asString());
        int page = responseObject.optInt("page");
        if(pagination == 0 || pagination == 1){
            Assert.assertEquals(page, 1,"Response page must be matched with passed pagination in url params!");
        }else{
            Assert.assertEquals(page, pagination,"Response page must be matched with passed pagination in url params!");
        }

        int per_page = responseObject.optInt("per_page");
        JSONArray data = responseObject.getJSONArray("data");
        Assert.assertEquals(per_page, data.length(),"Response data users objects must be equal to per_page object!");
        
        boolean isUserValidated = controller.validateUsersObject(API_CALL, Urls, data);
        Assert.assertEquals(isUserValidated, true,"User Object validation failed!");

        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = UserTd.INVOCATION_COUNT)
    public void validateSupportObject(int pagination){
        ArrayList<String> supportUrl = new ArrayList<>();
        Response response = RESPONSES.get(API_CALL);
        JSONObject responseObject = new JSONObject(response.asString());
        JSONObject support = responseObject.getJSONObject("support");
        supportUrl.add(support.optString("url").toString().trim());
        String txt = support.optString("text").toString().trim();
        Assert.assertEquals(txt.length() > 0, true,"Support object Text can't be null or empty!");

        if(supportUrl.size() > 0){
            Helper helper = new Helper();
            Assert.assertEquals(helper.validateActiveLinks(supportUrl), true,"Support Url validation failed!");
        }

        if(API_CALL == MAX_CALL)
            log.info("Support Object validated successfully.");
        
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object [][] url(){
        return new Object[][]
        {
            {
                UserTd.pagination[API_CALL]
            }
        };
    }
}