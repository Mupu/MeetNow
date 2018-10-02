package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import org.jooq.Record;
import org.jooq.Result;

public class HandlerTeilnahme implements RequestHandler {
    @Override
    public Response handle(IHTTPSession session, Result<Record> userData) {


        return NanoHTTPD.newFixedLengthResponse(
                Status.OK,
                NanoHTTPD.MIME_PLAINTEXT,
                userData.toString());
    }
}