package common;
import config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.restassured.RestAssured;
import java.util.concurrent.TimeUnit;
import io.restassured.response.Response;

public class RequestHandler {

    private static Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    public Response createGetRequest(String url){
        Response response = RestAssured.given()
            .urlEncodingEnabled(true)
            // .log().all()
            .when().get(url);

        // response.prettyPrint();
        if(validateStatusCodeAndResponseTime(response, url)){
            return response;
        }

        return response;
    }

    /**
     * validate status code and response time
     * @param response
     * @return
     */
    private boolean validateStatusCodeAndResponseTime(Response response, String url){
        boolean validateBasics = false;
        if((response.getStatusCode() == 200) && response.getTimeIn(TimeUnit.SECONDS) <= Constants.RESPONSE_TIME){
            validateBasics = true;
        }

        if(response != null){
            if(validateBasics){
                log.info(url);
                return validateBasics;
            }else{
                validateBasics = false;
                log.info(url);
                log.info(response.asString());
                log.error("The get api call was taking more time than expected, total time taken : "+response.getTimeIn(TimeUnit.SECONDS));
            }
        }
        return validateBasics;
    }
}
