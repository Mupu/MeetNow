package me.mupu;

import fi.iki.elonen.NanoHTTPD;
import me.mupu.sql.SQLQuery;

import javax.net.ssl.KeyManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;


public class ServerMain extends NanoHTTPD {

    public static void main(String[] args) {
        new ServerMain();
        //todo remove this
        SQLQuery.getInstance();
    }

    private ServerMain() {
        super(443);
        try {
            // load keystore
            File f = new File("src/main/resources/keystore.jks");
            System.setProperty("javax.net.ssl.trustStore", f.getAbsolutePath());
            char[] password = "mypassword".toCharArray();
            FileInputStream fis = new FileInputStream(f);

            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(fis, password);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);

            // enable SSL
            makeSecure(makeSSLSocketFactory(ks, kmf), null);

            start(SOCKET_READ_TIMEOUT, false);

            System.out.print("\nRunning! Point your browsers to https://localhost:" + getListeningPort() + "/ \n\n\n");
            System.out.print("\nDebug: https://localhost:" + getListeningPort() + "/hallo?pete=ja%20&%20olo=4" + "/ \n\n\n");

        } catch (Exception e) {
            System.err.println("Couldn't start server:\n" + e);
        }
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

    @Override
    public Response serve(IHTTPSession session) {
        return new HttpSessionHandler().handle(session);
    }
}
