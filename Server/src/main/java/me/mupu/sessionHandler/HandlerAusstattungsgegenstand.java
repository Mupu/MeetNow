package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import jooq.tables.records.BenutzerRecord;
import org.jooq.Record;
import org.jooq.Result;
import org.json.JSONArray;

public class HandlerAusstattungsgegenstand implements RequestHandler {
    @Override
    public NanoHTTPD.Response handle(IHTTPSession session, BenutzerRecord userData) {
        return NanoHTTPD.newFixedLengthResponse(
                Status.OK,
                NanoHTTPD.MIME_PLAINTEXT,"ausstattungsgegenstand");
    }
}