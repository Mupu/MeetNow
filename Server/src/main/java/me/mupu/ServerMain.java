package me.mupu;

import fi.iki.elonen.NanoHTTPD;
import me.mupu.sql.SQLQuerries;

import javax.net.ssl.KeyManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ServerMain extends NanoHTTPD {

    private ServerMain() {
        super(443);
        try {

            File f = new File("src/main/resources/keystore.jks");
            System.setProperty("javax.net.ssl.trustStore", f.getAbsolutePath());
            char[] password = "mypassword".toCharArray();
            FileInputStream fis = new FileInputStream(f);
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(fis, password);

//             setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);


            makeSecure(makeSSLSocketFactory(ks, kmf), null);

            start(SOCKET_READ_TIMEOUT, false);

            System.out.print("\nRunning! Point your browsers to https://localhost:" + getListeningPort() + "/ \n");
        } catch (Exception e) {
            System.err.println("Couldn't start server:\n" + e);
        }
    }

    public static void main(String[] args) {
        // disable logo output of jooq
        System.getProperties().setProperty("org.jooq.no-logo", "true");
        new ServerMain();

        SQLQuerries.getInstance();
    }

    @Override
    public Response serve(IHTTPSession session) {
        CookieHandler ch = new CookieHandler(session.getHeaders());
        MyCookie cookie = new MyCookie("login",
                ch.read("login") == null ? "you logged in now" : "you have been logged in",
                1)
                .setHttpOnly(true)
                .setSecure(true)
                .setSameSite(MyCookie.SAMESITE_STRICT);

        ch.set(cookie);
        Response response = newFixedLengthResponse(ch.read("login") == null ? "you logged in now" : "you have been logged in");
        ch.unloadQueue(response);
        return response;
    }

    private static class MyCookie extends Cookie {

        public MyCookie(String name, String value) {
            super(name, value);
        }

        public MyCookie(String name, String value, int numDays) {
            super(name, value, numDays);
        }

        public MyCookie(String name, String value, String expires) {
            super(name, value, expires);
        }

        public final static String SAMESITE_STRICT = "Strict";
        public final static String SAMESITE_LAX = "Lax";

        private String path = "/";
        private String sameSite = "";
        private boolean httpOnly = false;
        private boolean secure = false;

        @Override
        public String getHTTPHeader() {
            String fmt =  super.getHTTPHeader() + " ;path=%s ;%s ;%s";
            return String.format(fmt,
                    this.path,
                    this.secure ? "secure" : "",
                    this.httpOnly ? "HttpOnly" : "") +
                    (!sameSite.equals("") ?  " ;SameSite=" + sameSite : "");
        }

        public MyCookie setPath(String path) {
            this.path = path;
            return this;
        }

        public MyCookie setHttpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
            return this;
        }

        public MyCookie setSecure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public MyCookie setSameSite(String sameSite) {
            this.sameSite = sameSite;
            return this;
        }
    }
}
