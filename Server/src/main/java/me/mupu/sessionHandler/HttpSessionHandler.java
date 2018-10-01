package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD.*;

import java.util.Arrays;

public class HttpSessionHandler {

    private static HttpSessionHandler instance = null;
    private MyHttpContext<RequestHandler> context;

    private HttpSessionHandler() {
        context = new MyHttpContext<>(new RequestHandler() {});
        context.setAttribute("/ausleihe",               new HandlerAusleihe());
        context.setAttribute("/ausstattungsgegenstand", new HandlerAusstattungsgegenstand());
        context.setAttribute("/benutzer",               new HandlerBenutzer());
        context.setAttribute("/besprechung",            new HandlerBesprechung());
        context.setAttribute("/person",                 new HandlerPerson());
        context.setAttribute("/raum",                   new HandlerRaum());
        context.setAttribute("/teilnahme",              new HandlerTeilnahme());
    }

    public static Response handle(IHTTPSession session) {

        if (instance == null)
            instance = new HttpSessionHandler();

        Response response = ((RequestHandler) instance.context.getAttribute(session.getUri())).handle(session);
//        response = NanoHTTPD.newFixedLengthResponse(debugString(session));
//        response.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
        return response;
    }

    private String debugString(IHTTPSession session) {
        StringBuilder cookies = new StringBuilder();
        session.getCookies().iterator().forEachRemaining(c -> cookies.append(c + ": " + session.getCookies().read(c)));

        StringBuilder params = new StringBuilder();
        session.getParameters().forEach((s, l) -> params.append(s + ": " + Arrays.deepToString(l.toArray()) + "\n"));

        StringBuilder headers = new StringBuilder();
        session.getHeaders().forEach((s, l) -> headers.append(s + ": " + l));

        return "Method:\n" + session.getMethod().name()
                + "\n\nHEADERS:\n" + headers
                + "\n\nURI:\n" + session.getUri()
                + "\n\nQUERYPARAMS:\n" + session.getQueryParameterString()
                + "\n\nPARAMS:\n" + params
                + "\nCOOKIES:\n" + cookies;
    }
}
