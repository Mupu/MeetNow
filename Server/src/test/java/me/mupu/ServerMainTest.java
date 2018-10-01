package me.mupu;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.net.ssl.SSLContext;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static me.mupu.sessionHandler.HttpSessionHandler.COOKIE_USERNAME;
import static me.mupu.sessionHandler.HttpSessionHandler.COOKIE_PASSWORD;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerMainTest {

    ServerMain server;

    @BeforeAll
    void startServer() {
        server = new ServerMain(7327);
        System.out.println("Started server...");
    }

    @AfterAll
    void stopServer() {
        server.stop();
        System.out.println("Stopped server!");
    }

    @Test
    void testConnectionAndGetReqeuest() throws Exception {
        String https_url = "https://localhost:7327/teilnahme?pete=ja%20&%20olo=4";

        // disable certificate check
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (certificate, authType) -> true).build();

        // setup client
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();

        CookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie c1 = new BasicClientCookie2(COOKIE_USERNAME, "Alex");
        BasicClientCookie c2 = new BasicClientCookie2(COOKIE_PASSWORD, "Schmidt");
        c1.setExpiryDate(date);
        c2.setExpiryDate(date);
        c1.setDomain("localhost");
        c2.setDomain("localhost");
        c1.setSecure(true);
        c2.setSecure(true);
        c1.setAttribute("httponly", "");

        cookieStore.addCookie(c1);
        cookieStore.addCookie(c2);

        CloseableHttpClient client = HttpClients.custom()
                .setSSLContext(sslContext)
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .setDefaultCookieStore(cookieStore)
                .build();

        // setup get request
        HttpGet httpGet = new HttpGet(https_url);
//        httpGet.addHeader("Content-Type", "text/plain");

        // setup reponse handler
        HttpResponse response = client.execute(httpGet);


        System.out.println("**********CLIENT**********");
        System.out.println(response.getStatusLine());
        System.out.println(Arrays.toString(response.getAllHeaders()));
        System.out.println(EntityUtils.toString(response.getEntity()));
        System.out.println("COOKIES:");
        cookieStore.getCookies().forEach(c -> System.out.println(c.getName() + ": " + c.getValue()));
        System.out.println();
    }

}