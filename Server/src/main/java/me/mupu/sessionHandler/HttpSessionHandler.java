package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;
import jooq.tables.records.BenutzerRecord;
import me.mupu.Mapper;
import me.mupu.sql.SQLQuery;
import org.jooq.Record;
import org.jooq.Result;

import java.util.*;

public class HttpSessionHandler {

    private static HttpSessionHandler instance = null;

    public static HttpSessionHandler getInstance() {
        if (instance == null)
            instance = new HttpSessionHandler();
        return instance;
    }

    /**
     * This mapper is used to handle the contexts of http (https://ip/thisPart).
     * When a context is not found, the default handler is used .
     */
    private final Mapper<RequestHandler> context;
    public final static String COOKIE_USERNAME = "username";
    public final static String COOKIE_PASSWORD = "password";
    public final static String COOKIE_CONNECTION_TYPE = "connection_type";
    public static String CONTENT_TYPE = NanoHTTPD.MIME_PLAINTEXT;


    private HttpSessionHandler() {
        // default handler
        context = new Mapper<>(new HandlerContextNotFound());
        context.setAttribute("/settings", new HandlerSettings());
        context.setAttribute("/ausleihe/", new HandlerAusleihe());
        context.setAttribute("/ausstattungsgegenstand/", new HandlerAusstattungsgegenstand());
        context.setAttribute("/benutzer/", new HandlerBenutzer());
        context.setAttribute("/besprechung/", new HandlerBesprechung());
        context.setAttribute("/person/", new HandlerPerson());
        context.setAttribute("/raum/", new HandlerRaum());
        context.setAttribute("/teilnahme/", new HandlerTeilnahme());
    }

    public Response handle(IHTTPSession session) {
        if (instance == null) instance = new HttpSessionHandler();
        System.out.println(debugString(session));

        Response response;

        BenutzerRecord userdata = SQLQuery.checkLogin(
                session.getCookies().read(COOKIE_USERNAME),
                session.getCookies().read(COOKIE_PASSWORD));

        // override / add connection type cookie
        if (session.getCookies().read(COOKIE_CONNECTION_TYPE) == null || !session.getCookies().read(COOKIE_CONNECTION_TYPE).equals("desktop")) {
            session.getCookies().set(new Cookie(COOKIE_CONNECTION_TYPE, "app", Integer.MAX_VALUE));
            CONTENT_TYPE = NanoHTTPD.MIME_HTML;
        } else {
            session.getCookies().set(new Cookie(COOKIE_CONNECTION_TYPE, "desktop", Integer.MAX_VALUE));
            CONTENT_TYPE = NanoHTTPD.MIME_PLAINTEXT;
        }


        if (userdata != null) {
            // logged in

            if (userdata.getAccountstatus().intValue() == 1 || session.getUri().toLowerCase().equals("/settings")) {
                response = instance.context.getAttribute(session.getUri().toLowerCase()).handle(session, userdata);
            } else {
                // force user to change name and password
                String s;
                if (session.getCookies().read(COOKIE_CONNECTION_TYPE).equals("desktop"))
                    s = "/settings";
                else
                    s = "<head><meta http-equiv=\"refresh\" content=\"0; URL=https://"
                            + session.getHeaders().get("host")
                            + "/settings\" /></head>";

                response = NanoHTTPD.newFixedLengthResponse(
                        Response.Status.TEMPORARY_REDIRECT,
                        CONTENT_TYPE,
                        s);
                }
            setCookies(session.getCookies());
        } else {
            // failed to log in
            response = NanoHTTPD.newFixedLengthResponse(
                    Response.Status.UNAUTHORIZED,
                    CONTENT_TYPE,
                    "Wrong username or password.");
        }

        return response;
    }

    private void setCookies(CookieHandler cookieHandler) {
        // override / add password cookie
        cookieHandler.set(new Cookie(COOKIE_PASSWORD, cookieHandler.read(COOKIE_PASSWORD), 60 * 60 * 24 * 2)
                .setSecure(true)
                .setHttpOnly(true)
                .setSameSite("Strict")
        );

        // override / add user cookie
        cookieHandler.set(new Cookie(COOKIE_USERNAME, cookieHandler.read(COOKIE_USERNAME), 60 * 60 * 24 * 2)
                .setSecure(true)
                .setHttpOnly(true)
                .setSameSite("Strict")
        );
    }

    private String debugString(IHTTPSession session) {
        StringBuilder cookies = new StringBuilder();
        session.getCookies().iterator().forEachRemaining(c -> cookies.append(c + ": " + session.getCookies().read(c) + "\n"));

        StringBuilder params = new StringBuilder();
        session.getParameters().forEach((s, l) -> params.append(s + ": " + Arrays.deepToString(l.toArray()) + "\n"));

        StringBuilder headers = new StringBuilder();
        session.getHeaders().forEach((s, l) -> headers.append(s + ": " + l));

        return "\n**********SERVER**********\n\nMethod:\n" + session.getMethod().name()
                + "\n\nHEADERS:\n" + headers
                + "\n\nURI:\n" + session.getUri()
                + "\n\nQUERYPARAMS:\n" + session.getQueryParameterString()
                + "\n\nPARAMS:\n" + params
                + "\nCOOKIES:\n" + cookies;
    }

}
