package Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpClientUtil {
    private static final Logger log = Logger.getLogger(HttpClientUtil.class);

    synchronized public static String getFromUrl(String url) {
        System.out.println(url);
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            log.error("fail execute request. URL: " + url);
            return "";
        }
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {

            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            } catch (IOException e) {
                log.error("fail create BufferedReader. URL: " + url);
            }
            StringBuffer result = new StringBuffer();
            String line = "";
            while (true) {
                try {
                    if (!((line = rd.readLine()) != null)) break;
                } catch (IOException e) {
                    log.error("fail readLine. URL: " + url);
                }
                result.append(line);
            }
            return result.toString();
        } else {
            log.error("error to connect. Status code: " + statusCode + " URL: " + url);
            return "";
        }
    }
}
