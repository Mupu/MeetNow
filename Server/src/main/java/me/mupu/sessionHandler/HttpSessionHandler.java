package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;
import me.mupu.Mapper;
import me.mupu.sql.SQLQuery;
import org.jooq.Record;

import java.util.Arrays;
import java.util.Calendar;

public class HttpSessionHandler {

    private static HttpSessionHandler instance = null;
    private Mapper<RequestHandler> context;
    public final static String COOKIE_USERNAME = "username";
    public final static String COOKIE_PASSWORD = "password";


    private HttpSessionHandler() {
        context = new Mapper<>(new RequestHandler() {
        });
        context.setAttribute("/ausleihe", new HandlerAusleihe());
        context.setAttribute("/ausstattungsgegenstand", new HandlerAusstattungsgegenstand());
        context.setAttribute("/benutzer", new HandlerBenutzer());
        context.setAttribute("/besprechung", new HandlerBesprechung());
        context.setAttribute("/person", new HandlerPerson());
        context.setAttribute("/raum", new HandlerRaum());
        context.setAttribute("/teilnahme", new HandlerTeilnahme());
    }

    public static Response handle(IHTTPSession session) {
        if (instance == null) instance = new HttpSessionHandler();

        System.out.println(debugString(session));

        Response response = NanoHTTPD.newFixedLengthResponse(loginAndSetCookies(session));

//        Response response = instance.context.getAttribute(session.getUri().toLowerCase()).handle(session);
//        response = NanoHTTPD.newFixedLengthResponse(debugString(session));
//        response.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
        return response;
    }

    private static String loginAndSetCookies(IHTTPSession session) {
        // check if login is correct
        Record r = SQLQuery.checkLogin(
                session.getCookies().read(COOKIE_USERNAME) != null ? session.getCookies().read(COOKIE_USERNAME) : "",
                session.getCookies().read(COOKIE_PASSWORD) != null ? session.getCookies().read(COOKIE_PASSWORD) : "");
        // if user has logged in successfully
        if (r != null) {
            // override / add password cookie
            session.getCookies().set(new Cookie(COOKIE_PASSWORD, session.getCookies().read(COOKIE_PASSWORD), 60*60*24*2)
                    .setSecure(true)
                    .setHttpOnly(true)
                    .setSameSite("Strict")
            );

            // override / add user cookie
            session.getCookies().set(new Cookie(COOKIE_USERNAME, session.getCookies().read(COOKIE_USERNAME), 60*60*24*2)
                    .setSecure(true)
                    .setHttpOnly(true)
                    .setSameSite("Strict")
            );
        }
        return r != null ? r.formatJSON() : "not logged in";
    }

    private static String debugString(IHTTPSession session) {
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
