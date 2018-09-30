package me.mupu;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;


public class ServerMainTest {

    public static void main(String[] args) {
        new ServerMainTest().testIt();
    }

    private void testIt() {
        String https_url = "https://localhost:443/hallo?pete=ja%20&%20olo=4";
        try {
            // disable certificate check
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (certificate, authType) -> true).build();

            // setup client
            CookieStore httpCookieStore = new BasicCookieStore();
            CloseableHttpClient client = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                    .setDefaultCookieStore(httpCookieStore)
                    .build();

            // setup get request
            HttpGet httpGet = new HttpGet(https_url);
            httpGet.addHeader("Content-Type", "text/plain");

            // setup reponse handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpResponse response = client.execute(httpGet);

            System.out.println(responseHandler.handleResponse(response));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}