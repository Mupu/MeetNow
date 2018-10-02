package me.mupu;

import fi.iki.elonen.NanoHTTPD;
import me.mupu.sessionHandler.HttpSessionHandler;
import me.mupu.sql.SQLQuery;

import javax.net.ssl.KeyManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;


public class ServerMain extends NanoHTTPD {

    public static void main(String[] args) {
        new ServerMain(443);
    }

    public ServerMain(int port) {
        super(port);
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

    @Override
    public Response serve(IHTTPSession session) {
        return HttpSessionHandler.getInstance().handle(session);
    }
}
