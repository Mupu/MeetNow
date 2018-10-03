package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import jooq.tables.records.BenutzerRecord;

public class HandlerSettings implements RequestHandler {
    @Override
    public Response handle(IHTTPSession session, BenutzerRecord userData) {
        return NanoHTTPD.newFixedLengthResponse(
                Status.OK,
                HttpSessionHandler.CONTENT_TYPE,"settings");
    }
}