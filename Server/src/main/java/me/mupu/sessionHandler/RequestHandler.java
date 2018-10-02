package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import org.jooq.Record;
import org.jooq.Result;


public interface  RequestHandler {
    Response response = NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "File not Found!");

    default Response handle(IHTTPSession session, Result<Record> userData) {
        return response;
    }

}
