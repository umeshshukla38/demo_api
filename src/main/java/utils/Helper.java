package utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {
    
    int resCode = 200;
    HttpURLConnection uc = null;
    private static Logger log = LoggerFactory.getLogger(Helper.class);

    public List<Object> keys(JSONObject val) {
        return Arrays.asList(val.keySet().toArray());
    }

    public boolean validateActiveLinks(ArrayList<String> links) {
        boolean linkActive = false;
        ArrayList<String> inactiveUrls = new ArrayList<>();

        if(links.size() <= 0){
            return linkActive;
        }

        int count = 1;
		for(String link : links) {
			if(link.contains("http")) {
                try {
                    uc = (HttpURLConnection)(new URL(link).openConnection());
                    uc.setRequestMethod("HEAD");
                    uc.connect();
                    int res = uc.getResponseCode();
                    if(res == resCode) {
                        linkActive = true;
                    }else {
                        linkActive = false;
                        inactiveUrls.add("Count : "+count+", URL :"+link);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            count++;
        }

        if(inactiveUrls.size() > 0){
            linkActive = false;
            log.error("Below given urls are inactive state, please manually validate the same : \n"+inactiveUrls);
        }
		return linkActive;
	}
}
