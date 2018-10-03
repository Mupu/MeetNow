package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import jooq.tables.records.BenutzerRecord;
import org.jooq.Record;
import org.jooq.Result;

public class HandlerContextNotFound implements RequestHandler {
    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session, BenutzerRecord userData) {
        return response;
    }
}
