package me.mupu;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;

import java.util.Arrays;

public class HttpSessionHandler {

    public Response handle(IHTTPSession session) {
        
        Response response = NanoHTTPD.newFixedLengthResponse(debugString(session));
        response.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
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
                +   "\nCOOKIES:\n" + cookies;
    }

    private enum contexts {
        ausleihe                ("/ausleihe"),
        ausstattungsgegenstand  ("/ausstattungsgegenstand"),
        benutzer                ("/benutzer"),
        besprechung             ("/besprechung"),
        person                  ("/person"),
        raum                    ("/raum"),
        teilnahme               ("/teilnahme");

        private final String context;

        contexts(String context) {
            this.context = context;
        }

        public String get() {
            return context;
        }
    }

}
