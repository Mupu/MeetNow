package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import jooq.tables.records.BenutzerRecord;
import org.jooq.Record;
import org.jooq.Result;
import org.json.JSONArray;

public class HandlerRaum implements RequestHandler {
    @Override
    public NanoHTTPD.Response handle(IHTTPSession session, BenutzerRecord userdata) {
        return NanoHTTPD.newFixedLengthResponse(
                Status.OK,
                HttpSessionHandler.CONTENT_TYPE,
                "<html><body>" +
                        "<button type=\"button\" onclick=\"document.cookie=&quot;lastname=Smith;expires=Wed, 18 Dec 2023 12:00:00 GMT&quot;\">Create Cookie 2</button>" +
                        "</body></html>");
    }
}