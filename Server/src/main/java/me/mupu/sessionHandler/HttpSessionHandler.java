package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;
import me.mupu.Mapper;
import me.mupu.sql.SQLQuery;
import org.jooq.Record;

import java.util.Arrays;

public class HttpSessionHandler {

    private static HttpSessionHandler instance = null;
    private Mapper<RequestHandler> context;
    public final static String COOKIE_USERNAME = "user";
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

        // add cookies
        session.getCookies().unloadQueue(response);
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
            session.getCookies().set(new MyCookie(COOKIE_PASSWORD, session.getCookies().read(COOKIE_PASSWORD), 1)
                    .setSecure(true)
                    .setHttpOnly(true)
                    .setSameSite("Strict")
            );
        }
            // override / add user cookie
            session.getCookies().set(new MyCookie(COOKIE_USERNAME, session.getCookies().read(COOKIE_USERNAME), 1)
                    .setSecure(true)
                    .setHttpOnly(true)
                    .setSameSite("Strict")
            );
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

    //TODO REMOVE
    public static class MyCookie extends Cookie {

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
            String fmt = super.getHTTPHeader() + " ;path=%s ;%s ;%s";
            return String.format(fmt,
                    this.path,
                    this.secure ? "secure" : "",
                    this.httpOnly ? "HttpOnly" : "") +
                    (!sameSite.equals("") ? " ;SameSite=" + sameSite : "");
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
