package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import jooq.tables.records.BenutzerRecord;
import org.jooq.Record;
import org.jooq.Result;


public interface  RequestHandler {
    Response response = NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, HttpSessionHandler.CONTENT_TYPE, "File not Found!");

    default Response handle(IHTTPSession session, BenutzerRecord userdata) {
        return response;
    }

}
