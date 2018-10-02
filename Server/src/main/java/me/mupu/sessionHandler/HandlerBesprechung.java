package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import org.jooq.Record;
import org.jooq.Result;
import org.json.JSONArray;

public class HandlerBesprechung implements RequestHandler {
    @Override
    public NanoHTTPD.Response handle(IHTTPSession session, Result<Record> userData) {
        return NanoHTTPD.newFixedLengthResponse(
                Status.OK,
                NanoHTTPD.MIME_PLAINTEXT,"besprechung");
    }
}
